"""Sanity checks for the shipped book asset. Exits non-zero on any problem.

Run locally with `python3 tools/validate_asset.py`; CI runs it before every build.
"""
import json
import re
import sys
from pathlib import Path

ASSET = Path(__file__).resolve().parent.parent / "app/src/main/assets/data/hisnul_muslim_am.json"

problems: list[str] = []


def check(condition: bool, message: str) -> None:
    if not condition:
        problems.append(message)


def main() -> int:
    book = json.loads(ASSET.read_text(encoding="utf-8"))

    categories = book.get("categories", [])
    chapters = book.get("chapters", [])
    category_ids = {c["id"] for c in categories}

    check(bool(categories), "no categories")
    check(bool(chapters), "no chapters")
    check(len(category_ids) == len(categories), "duplicate category ids")

    chapter_ids = [c["id"] for c in chapters]
    check(len(chapter_ids) == len(set(chapter_ids)), "duplicate chapter ids")

    dua_ids: list[int] = []
    arabic_missing = []
    for chapter in chapters:
        check(chapter["categoryId"] in category_ids, f"chapter {chapter['id']} has unknown category")
        check(bool(chapter.get("title")), f"chapter {chapter['id']} has no title")
        check(bool(chapter.get("duas")), f"chapter {chapter['id']} has no duas")
        for dua in chapter["duas"]:
            dua_ids.append(dua["id"])
            check(dua["amharic"].strip() != "", f"dua {dua['id']} has no Amharic text")
            check(dua["repeat"] >= 1, f"dua {dua['id']} has invalid repeat count")
            if not dua["arabic"].strip():
                arabic_missing.append(dua["id"])
            # guard against stray Latin placeholder text in the translation
            check(
                not re.search(r"\b(TODO|TBD|FIXME|palms)\b", dua["amharic"]),
                f"dua {dua['id']} contains placeholder text",
            )

    check(len(dua_ids) == len(set(dua_ids)), "duplicate dua ids across chapters")

    print(f"chapters : {len(chapters)}")
    print(f"categories: {len(categories)}")
    print(f"duas     : {len(dua_ids)}")
    print(f"without Arabic (narration-only entries): {arabic_missing}")

    if problems:
        print("\nFAILED:")
        for problem in problems:
            print(" -", problem)
        return 1

    print("\nAsset OK ✅")
    return 0


if __name__ == "__main__":
    sys.exit(main())
