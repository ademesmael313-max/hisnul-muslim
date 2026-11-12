"""Merge the Arabic source, the Amharic translations and the category map into
the single JSON asset shipped inside the APK.

Usage:
    python3 tools/build_asset.py <path/to/en.json>

`en.json` is the English/Arabic export of "Fortress of the Muslim"
(hisnul-muslim.com dataset, one JSON object per chapter with an English TITLE
and a TEXT array holding ARABIC_TEXT / TRANSLATED_TEXT / REPEAT).
Only the Arabic text and the repeat counts are taken from it.
"""
import json
import sys
from pathlib import Path

TOOLS = Path(__file__).resolve().parent
OUT = TOOLS.parent / "app" / "src" / "main" / "assets" / "data" / "hisnul_muslim_am.json"


def load_translations() -> tuple[dict, dict]:
    titles = json.loads((TOOLS / "translations" / "chapters.json").read_text(encoding="utf-8"))
    duas: dict[str, str] = {}
    for path in sorted((TOOLS / "translations").glob("part*.json")):
        part = json.loads(path.read_text(encoding="utf-8"))
        dupes = set(part) & set(duas)
        if dupes:
            raise SystemExit(f"duplicate dua ids in {path.name}: {sorted(dupes)}")
        duas.update(part)
    return titles, duas


def main() -> None:
    if len(sys.argv) < 2:
        raise SystemExit("usage: build_asset.py <en.json>")
    source = json.loads(Path(sys.argv[1]).read_text(encoding="utf-8-sig"))["English"]
    titles, amharic = load_translations()
    categories = json.loads((TOOLS / "categories.json").read_text(encoding="utf-8"))

    by_id = {str(ch["ID"]): ch for ch in source}
    chapters, used_ids, missing, order = [], set(), [], 0

    for cat in categories:
        for raw in cat["chapters"]:
            cid = str(raw)
            ch = by_id[cid]
            duas = []
            for entry in ch.get("TEXT", []):
                did = str(entry["ID"])
                arabic = (entry.get("ARABIC_TEXT") or "").strip()
                text = amharic.get(did, "").strip()
                if not text:                        # no Amharic translation -> never ship a silent gap
                    missing.append(did)
                    continue
                duas.append({
                    "id": int(did),
                    "arabic": arabic,
                    "amharic": text,
                    "repeat": int(entry.get("REPEAT") or 1),
                })
            if not duas:
                continue
            used_ids.add(cid)
            order += 1
            chapters.append({
                "id": int(cid),
                "title": titles[cid],
                "categoryId": cat["id"],
                "order": order,
                "duas": duas,
            })

    unlisted = sorted(set(by_id) - used_ids, key=int)
    if unlisted:
        raise SystemExit(f"chapters missing from categories.json: {unlisted}")

    payload = {
        "schemaVersion": 1,
        "title": "ሂስኑል ሙስሊም",
        "subtitle": "የአላህ ባሪያ የሚያስታውሳቸው ዱዓዎችና ዚክሮች",
        "author": "ሰዒድ ቢን ዓሊ ቢን ወህፍ አል-ቀህጣኒ",
        "language": "am",
        "categories": [{"id": c["id"], "title": c["title"], "description": c["description"], "icon": c["icon"]} for c in categories],
        "chapters": chapters,
    }
    OUT.parent.mkdir(parents=True, exist_ok=True)
    OUT.write_text(json.dumps(payload, ensure_ascii=False, separators=(",", ":")), encoding="utf-8")

    total = sum(len(c["duas"]) for c in chapters)
    print(f"chapters: {len(chapters)}  duas: {total}  size: {OUT.stat().st_size/1024:.0f} kB")
    print(f"skipped empty source entries: {sorted(missing, key=int)}")
    unknown = sorted(set(amharic) - {str(d['id']) for c in chapters for d in c['duas']}, key=int)
    leftover = sorted(set(amharic) - {str(d['ID']) for ch in source for d in ch.get('TEXT', [])}, key=int)
    print(f"translations not used: {leftover}")
    assert not unknown  # every translation reached the asset


if __name__ == "__main__":
    main()
