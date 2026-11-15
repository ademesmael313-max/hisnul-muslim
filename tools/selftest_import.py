"""Self-test for tools/import_translation.py (runs in CI, no external deps).

Builds fixtures from the shipped asset, runs the readers and the matcher and
asserts that every entry lands on the right dua.
"""
from __future__ import annotations

import csv
import json
import sys
import tempfile
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))

import import_translation as imp  # noqa: E402

ASSET = Path(__file__).resolve().parent.parent / "app/src/main/assets/data/hisnul_muslim_am.json"


def sample_duas(count: int = 3) -> list[dict]:
    book = json.loads(ASSET.read_text(encoding="utf-8"))
    duas = [d for ch in book["chapters"] for d in ch["duas"] if d["arabic"].strip()]
    # spread the picks over the book
    step = max(1, len(duas) // (count + 1))
    return [duas[min(i * step, len(duas) - 1)] for i in range(count)]


def build_csv(path: Path, duas: list[dict]) -> None:
    with path.open("w", newline="", encoding="utf-8") as handle:
        writer = csv.writer(handle)
        writer.writerow(["arabic", "amharic"])
        for index, dua in enumerate(duas, start=1):
            writer.writerow([dua["arabic"], f"የሙከራ ትርጉም {index}፦ {dua['amharic'][:40]}"])


def build_chapter_text(path: Path) -> set[str]:
    """Amharic only, no Arabic — aligned by the chapter marker + order inside it."""
    book = json.loads(ASSET.read_text(encoding="utf-8"))
    chapters = book["chapters"]
    expected: set[str] = set()
    lines: list[str] = []
    for chapter in (chapters[0], chapters[7]):
        lines.append(f"{chapter['id']}. {chapter['title']}")
        for dua in chapter["duas"]:
            lines.append(dua["amharic"][:80])
            lines.append("")
            expected.add(str(dua["id"]))
    path.write_text("\n".join(lines), encoding="utf-8")
    return expected


def build_text(path: Path, duas: list[dict]) -> None:
    with path.open("w", encoding="utf-8") as handle:
        for index, dua in enumerate(duas, start=1):
            handle.write(dua["arabic"].replace("\n", " ") + "\n")
            handle.write(f"የሙከራ ትርጉም {index}፦ {dua['amharic'][:40]}\n\n")


def check(entries: list[imp.Entry], expected_ids: set[str], label: str) -> None:
    report = imp.match_entries(entries, imp.load_book(ASSET))
    got = {e.dua_id for e in report.matched}
    if got != expected_ids:
        raise SystemExit(f"{label}: matched {sorted(got)}, expected {sorted(expected_ids)}")
    if len(entries) != len(expected_ids):
        raise SystemExit(f"{label}: found {len(entries)} entries, expected {len(expected_ids)}")
    print(f"  {label}: {len(entries)}/{len(expected_ids)} matched correctly")


def main() -> None:
    duas = sample_duas(3)
    with tempfile.TemporaryDirectory() as tmp:
        tmp_path = Path(tmp)
        csv_path = tmp_path / "book.csv"
        txt_path = tmp_path / "book.txt"
        chapter_path = tmp_path / "chapters.txt"
        build_csv(csv_path, duas)
        build_text(txt_path, duas)
        expected_chapters = build_chapter_text(chapter_path)
        print("import self-test")
        check(imp.read_source(csv_path), {str(d["id"]) for d in duas}, "csv")
        check(imp.read_source(txt_path), {str(d["id"]) for d in duas}, "txt")
        check(imp.read_source(chapter_path), expected_chapters, "txt with chapter markers")
    print("import self-test OK ✅")


if __name__ == "__main__":
    main()
