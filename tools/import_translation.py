"""Import Amharic translations from an external file and align them to the book.

The importer understands:

* **JSON** — a list of objects with Arabic/Amharic keys, a mapping of
  ``{"duaId": "text"}``, or a nested structure that contains either of those.
* **CSV / TSV** — a header row with ``arabic`` and ``amharic`` columns (or the
  first two columns are used).
* **DOCX / PDF / TXT / MD** — paragraphs or lines; Arabic-script blocks are
  paired with the Ethiopic-script block that follows them.

Entries are matched to the 267 duas of the book by their Arabic text, with
diacritics, alef variants, punctuation and spacing normalised away. Exact
matches win; near matches are accepted with a similarity threshold and a margin
over the runner-up, so a doubtful match never silently overwrites a translation.

Usage:
    python3 tools/import_translation.py <file>              # dry run + report
    python3 tools/import_translation.py <file> --apply      # write the overlay
    python3 tools/import_translation.py <file> --apply --source ../_data/en.json

``--apply`` writes ``tools/translations/imported.json`` (duaId -> Amharic); that
file is applied *last* by ``build_asset.py``, so it wins over the hand-written
parts. Delete it to fall back to the previous wording.
"""
from __future__ import annotations

import argparse
import csv
import difflib
import io
import json
import re
import sys
import unicodedata
import zipfile
from dataclasses import dataclass, field
from pathlib import Path

TOOLS = Path(__file__).resolve().parent
ASSET = TOOLS.parent / "app" / "src" / "main" / "assets" / "data" / "hisnul_muslim_am.json"
OVERLAY = TOOLS / "translations" / "imported.json"
REPORT = TOOLS / "import-report.md"

EXACT_THRESHOLD = 1.0
FUZZY_THRESHOLD = 0.86
FUZZY_MARGIN = 0.03

ARABIC_SCRIPT = re.compile(r"[\u0600-\u06FF\u0750-\u077F\uFB50-\uFDFF\uFE70-\uFEFF]")
ETHIOPIC_SCRIPT = re.compile(r"[\u1200-\u137F\u1380-\u139F\u2D80-\u2DDF\uAB00-\uAB2F]")
TASHKEEL = re.compile(r"[\u0610-\u061A\u064B-\u065F\u0670\u06D6-\u06ED\u0640]")

ARABIC_KEYS = ("arabic", "arabic_text", "ar", "ar_text", "text_ar", "original")
AMHARIC_KEYS = ("amharic", "amharic_text", "am", "am_text", "translation", "text_am",
                "translated_text", "meaning", "amharic_translation")
ID_KEYS = ("id", "dua_id", "duaid", "number", "no")


@dataclass
class Entry:
    """One candidate translation found in the source file."""
    amharic: str
    arabic: str = ""
    hint_id: str = ""
    location: str = ""
    dua_id: str = ""
    score: float = 0.0
    how: str = "unmatched"


@dataclass
class Report:
    entries: list[Entry] = field(default_factory=list)
    book_ids: set[str] = field(default_factory=set)

    @property
    def matched(self) -> list[Entry]:
        return [e for e in self.entries if e.dua_id]

    @property
    def unmatched(self) -> list[Entry]:
        return [e for e in self.entries if not e.dua_id]


# --------------------------------------------------------------------------- #
# normalisation
# --------------------------------------------------------------------------- #
def normalize_arabic(text: str) -> str:
    """Strip diacritics/punctuation and unify letter variants for comparison."""
    value = unicodedata.normalize("NFKC", text or "")   # presentation forms -> base letters
    value = TASHKEEL.sub("", value)
    value = re.sub(r"[أإآٱ]", "ا", value)
    value = value.replace("ى", "ي").replace("ة", "ه").replace("ؤ", "و").replace("ئ", "ي")
    value = re.sub(r"[^\u0600-\u06FF\s]", " ", value)
    value = re.sub(r"\s+", " ", value)
    return value.strip()


def looks_amharic(text: str) -> bool:
    return bool(ETHIOPIC_SCRIPT.search(text or ""))


def looks_arabic(text: str) -> bool:
    return bool(ARABIC_SCRIPT.search(text or ""))


# --------------------------------------------------------------------------- #
# readers — every reader returns a list of Entry with at least `amharic`
# --------------------------------------------------------------------------- #
def _pick(data: dict, keys: tuple[str, ...]) -> str:
    lowered = {str(k).strip().lower(): v for k, v in data.items()}
    for key in keys:
        if key in lowered and isinstance(lowered[key], str):
            return lowered[key].strip()
    return ""


def _entry_from_mapping(data: dict, location: str) -> Entry | None:
    amharic = _pick(data, AMHARIC_KEYS)
    if not amharic or not looks_amharic(amharic):
        return None
    return Entry(
        amharic=amharic,
        arabic=_pick(data, ARABIC_KEYS),
        hint_id=_pick(data, ID_KEYS),
        location=location,
    )


def read_json(path: Path) -> list[Entry]:
    payload = json.loads(path.read_text(encoding="utf-8-sig"))
    entries: list[Entry] = []

    def walk(node, where: str) -> None:
        if isinstance(node, dict):
            entry = _entry_from_mapping(node, where)
            if entry:
                entries.append(entry)
                return
            for key, value in node.items():
                if isinstance(value, str):
                    # {"12": "Amharic text"} — id keyed mapping
                    if looks_amharic(value) and str(key).isdigit():
                        entries.append(Entry(amharic=value.strip(), hint_id=str(key), location=f"{where}.{key}"))
                else:
                    walk(value, f"{where}.{key}")
        elif isinstance(node, list):
            for index, value in enumerate(node):
                walk(value, f"{where}[{index}]")

    walk(payload, path.name)
    return entries


def read_delimited(path: Path) -> list[Entry]:
    text = path.read_text(encoding="utf-8-sig")
    sample = text[:4096]
    try:
        dialect = csv.Sniffer().sniff(sample, delimiters=",;\t|")
    except csv.Error:
        dialect = csv.excel
    rows = list(csv.reader(io.StringIO(text), dialect))
    if not rows:
        return []

    header = [cell.strip().lower() for cell in rows[0]]
    arabic_col = next((i for i, name in enumerate(header) if name in ARABIC_KEYS), None)
    amharic_col = next((i for i, name in enumerate(header) if name in AMHARIC_KEYS), None)
    id_col = next((i for i, name in enumerate(header) if name in ID_KEYS), None)

    if amharic_col is None:
        arabic_col, amharic_col = 0, 1
        body = rows
    else:
        body = rows[1:]

    entries = []
    for line_no, row in enumerate(body, start=2):
        if len(row) <= max(arabic_col or 0, amharic_col or 0):
            continue
        amharic = row[amharic_col].strip()
        if not looks_amharic(amharic):
            continue
        entries.append(Entry(
            amharic=amharic,
            arabic=row[arabic_col].strip() if arabic_col is not None and arabic_col < len(row) else "",
            hint_id=row[id_col].strip() if id_col is not None and id_col < len(row) else "",
            location=f"row {line_no}",
        ))
    return entries


def pairs_from_blocks(blocks: list[str], where: str) -> list[Entry]:
    """Pair Arabic blocks with the Ethiopic block that follows them."""
    entries: list[Entry] = []
    arabic_buf: list[str] = []
    amharic_buf: list[str] = []

    def flush(index: int) -> None:
        nonlocal arabic_buf, amharic_buf
        if amharic_buf:
            entries.append(Entry(
                amharic=" ".join(amharic_buf).strip(),
                arabic=" ".join(arabic_buf).strip(),
                location=f"{where} block {index}",
            ))
        arabic_buf, amharic_buf = [], []

    for index, block in enumerate(blocks, start=1):
        text = block.strip()
        if not text:
            continue
        has_ar, has_am = looks_arabic(text), looks_amharic(text)
        if has_am:
            if has_ar and arabic_buf == []:
                # a dua quoted inside the Amharic paragraph
                pass
            amharic_buf.append(text)
        elif has_ar:
            if amharic_buf:
                flush(index)
            arabic_buf.append(text)
    flush(len(blocks) + 1)
    return entries


def read_docx(path: Path) -> list[Entry]:
    with zipfile.ZipFile(path) as archive:
        raw = archive.read("word/document.xml").decode("utf-8", "ignore")
    raw = raw.replace("</w:p>", "</w:p>\n")
    paragraphs = []
    for chunk in raw.split("\n"):
        text = "".join(re.findall(r"<w:t[^>]*>(.*?)</w:t>", chunk, flags=re.S))
        text = re.sub(r"<[^>]+>", "", text)
        if text.strip():
            paragraphs.append(text)
    return pairs_from_blocks(paragraphs, path.name)


def read_pdf(path: Path) -> list[Entry]:
    try:
        from pypdf import PdfReader
    except ImportError:  # pragma: no cover
        raise SystemExit("PDF support needs pypdf:  pip install pypdf")
    reader = PdfReader(str(path))
    blocks: list[str] = []
    for page in reader.pages:
        for line in (page.extract_text() or "").splitlines():
            blocks.append(line)
    return pairs_from_blocks(blocks, path.name)


def read_text(path: Path) -> list[Entry]:
    return pairs_from_blocks(path.read_text(encoding="utf-8-sig").splitlines(), path.name)


def read_source(path: Path) -> list[Entry]:
    suffix = path.suffix.lower()
    if suffix == ".json":
        return read_json(path)
    if suffix in (".csv", ".tsv"):
        return read_delimited(path)
    if suffix == ".docx":
        return read_docx(path)
    if suffix == ".pdf":
        return read_pdf(path)
    return read_text(path)


def _is_delimited(path: Path) -> bool:
    try:
        head = path.read_text(encoding="utf-8-sig")[:2048]
    except UnicodeDecodeError:
        return False
    first = head.splitlines()[0] if head.splitlines() else ""
    return any(sep in first for sep in (",", ";", "\t", "|"))


# --------------------------------------------------------------------------- #
# matching
# --------------------------------------------------------------------------- #
def load_book(path: Path) -> list[tuple[str, str]]:
    book = json.loads(path.read_text(encoding="utf-8"))
    return [
        (str(dua["id"]), normalize_arabic(dua.get("arabic", "")))
        for chapter in book["chapters"]
        for dua in chapter["duas"]
        if dua.get("arabic")
    ]


def match_entries(entries: list[Entry], book: list[tuple[str, str]]) -> Report:
    by_exact = {arabic: dua_id for dua_id, arabic in book if arabic}
    by_id = {dua_id for dua_id, _ in book}
    used: dict[str, Entry] = {}
    report = Report(entries=entries, book_ids=by_id)

    for entry in entries:
        # 1. the source told us the id
        if entry.hint_id and entry.hint_id in by_id and entry.hint_id not in used:
            entry.dua_id, entry.score, entry.how = entry.hint_id, 1.0, "id"
            used[entry.hint_id] = entry
            continue
        if not entry.arabic:
            continue

        normalised = normalize_arabic(entry.arabic)

        # 2. exact Arabic match
        dua_id = by_exact.get(normalised)
        if dua_id and dua_id not in used:
            entry.dua_id, entry.score, entry.how = dua_id, 1.0, "exact"
            used[dua_id] = entry
            continue

        # 3. fuzzy match, with a margin so we never guess between two similar duas
        scored = sorted(
            ((difflib.SequenceMatcher(None, normalised, arabic).ratio(), dua_id)
             for dua_id, arabic in book),
            reverse=True,
        )[:2]
        if not scored:
            continue
        best_score, best_id = scored[0]
        runner_up = scored[1][0] if len(scored) > 1 else 0.0
        if best_id in used:
            continue
        if best_score >= FUZZY_THRESHOLD and best_score - runner_up >= FUZZY_MARGIN:
            entry.dua_id, entry.score, entry.how = best_id, best_score, "fuzzy"
            used[best_id] = entry
            continue

        # 4. long text truncated in the source: compare the first N characters
        prefix = normalised[:60]
        if len(prefix) >= 30:
            prefixed = sorted(
                ((difflib.SequenceMatcher(None, prefix, arabic[:60]).ratio(), dua_id)
                 for dua_id, arabic in book),
                reverse=True,
            )[:2]
            if prefixed and prefixed[0][0] >= 0.92 and prefixed[0][1] not in used:
                entry.dua_id, entry.score, entry.how = prefixed[0][1], prefixed[0][0], "prefix"
                used[prefixed[0][1]] = entry

    return report


# --------------------------------------------------------------------------- #
# output
# --------------------------------------------------------------------------- #
def write_report(report: Report, source: Path, applied: bool) -> None:
    matched = report.matched
    missed_ids = sorted(report.book_ids - {e.dua_id for e in matched}, key=int)
    lines = [
        "# Import report",
        "",
        f"* source: `{source.name}`",
        f"* overlay written: **{'yes' if applied else 'no (dry run)'}**",
        f"* candidate translations found: **{len(report.entries)}**",
        f"* matched to a dua: **{len(matched)}** "
        f"(exact {sum(e.how == 'exact' for e in matched)}, "
        f"fuzzy {sum(e.how == 'fuzzy' for e in matched)}, "
        f"prefix {sum(e.how == 'prefix' for e in matched)}, "
        f"id {sum(e.how == 'id' for e in matched)})",
        f"* unmatched source entries: **{len(report.unmatched)}**",
        f"* duas still using the existing translation: **{len(missed_ids)}**",
        "",
    ]
    if matched:
        lines += ["## Sample of matches", "", "| dua | how | score | Amharic (truncated) |", "|---|---|---|---|"]
        for entry in matched[:15]:
            snippet = entry.amharic.replace("|", "\\|")[:70]
            lines.append(f"| {entry.dua_id} | {entry.how} | {entry.score:.2f} | {snippet} |")
        lines.append("")
    if report.unmatched:
        lines += ["## Unmatched source entries (first 15)", "", "```"]
        for entry in report.unmatched[:15]:
            lines.append(f"{entry.location}: {entry.arabic[:60]!r} -> {entry.amharic[:60]!r}")
        lines += ["```", ""]
    if missed_ids:
        lines += ["## Duas that keep the existing translation (first 20)", "",
                  ", ".join(missed_ids[:20]) + (" …" if len(missed_ids) > 20 else ""), ""]
    REPORT.write_text("\n".join(lines), encoding="utf-8")


def main() -> None:
    parser = argparse.ArgumentParser(description="Import Amharic translations")
    parser.add_argument("file", type=Path, help="JSON, CSV, DOCX, PDF, TXT or MD file")
    parser.add_argument("--apply", action="store_true", help="write tools/translations/imported.json")
    parser.add_argument("--book", type=Path, default=ASSET, help="asset used as the alignment target")
    parser.add_argument(
        "--by-order",
        action="store_true",
        help="if the file holds the whole book in order, align entry i to dua i (only when the "
             "counts match exactly and Arabic matching left most entries unmatched)",
    )
    args = parser.parse_args()

    if not args.file.exists():
        raise SystemExit(f"no such file: {args.file}")

    entries = read_source(args.file)
    if not entries:
        raise SystemExit(
            "no Amharic text found — expected either an arabic/amharic column pair or "
            "Arabic paragraphs followed by Ethiopic paragraphs"
        )

    book = load_book(args.book)
    report = match_entries(entries, book)

    if args.by_order and len(entries) == len(book) and len(report.matched) < len(book) // 2:
        ordered_ids = sorted((dua_id for dua_id, _ in book), key=int)
        for entry, dua_id in zip(entries, ordered_ids):
            entry.dua_id, entry.score, entry.how = dua_id, 1.0, "order"
        report.entries = entries
        print("info: aligned by book order instead of Arabic text")

    overlay = {e.dua_id: e.amharic for e in report.matched}
    if args.apply and overlay:
        existing = {}
        if OVERLAY.exists():
            existing = json.loads(OVERLAY.read_text(encoding="utf-8"))
        existing.update(overlay)
        OVERLAY.write_text(
            json.dumps(dict(sorted(existing.items(), key=lambda kv: int(kv[0]))),
                       ensure_ascii=False, indent=1) + "\n",
            encoding="utf-8",
        )

    write_report(report, args.file, applied=args.apply and bool(overlay))

    print(f"entries found : {len(entries)}")
    print(f"matched       : {len(report.matched)}")
    print(f"unmatched     : {len(report.unmatched)}")
    print(f"duas untouched: {len(report.book_ids - {e.dua_id for e in report.matched})}")
    print(f"report        : {REPORT.relative_to(TOOLS.parent)}")
    if args.apply and overlay:
        print(f"overlay       : {OVERLAY.relative_to(TOOLS.parent)} ({len(overlay)} entries)")
        print("next          : python3 tools/build_asset.py <en.json> && python3 tools/validate_asset.py")
    elif overlay:
        print("dry run       : re-run with --apply to write the overlay")


if __name__ == "__main__":
    main()
