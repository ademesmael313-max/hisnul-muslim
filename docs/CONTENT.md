# Content notes, provenance and how to correct a translation

## What ships

`app/src/main/assets/data/hisnul_muslim_am.json` — one JSON document, read once at
startup:

```json
{
  "schemaVersion": 1,
  "categories": [{ "id": "daily", "title": "የዕለት ተዕለት ዚክር", "icon": "WbSunny" }],
  "chapters": [
    {
      "id": 27,
      "title": "የጠዋትና የማታ ዚክር",
      "categoryId": "daily",
      "order": 1,
      "duas": [
        { "id": 75, "arabic": "…", "amharic": "…", "repeat": 1 }
      ]
    }
  ]
}
```

Coverage: **132 chapters, 267 duas, 12 categories.** Dua id `267` is a narration about
etiquette and therefore has no Arabic text in the source; the app hides the Arabic block
for entries like that automatically.

## Provenance

| Part | Source |
|---|---|
| Arabic text, chapter order, repeat counts | *Fortress of the Muslim* English/Arabic JSON export (hisnmuslim.com dataset) |
| Chapter titles, category names, UI | Written for this project |
| Amharic meaning of each dua | Written for this project, in `tools/translations/part*.json` |

The Amharic text is a **meaning-based translation**, not a literal one: parenthetical
clarifications, transliterated formulas (e.g. ሰይይዱል ኢስቲግፋር) and short explanatory notes
are kept in brackets so the reader can recognise the original. Where the source contains a
long virtue/commentary note, the note is summarised instead of copied.

## Correcting a translation (fast path)

1. Open `tools/translations/part*.json` and find the dua id (ids are the numbers shown in
   the JSON, 1–267).
2. Rewrite the string. Keep it valid JSON — Amharic quotation marks `« »` are safer than
   escaped double quotes.
3. Regenerate and check:
   ```bash
   python3 tools/build_asset.py /path/to/en.json
   python3 tools/validate_asset.py
   ```
4. Commit both the translation file and the regenerated asset.

If you only have the APK source and not the original dataset, edit
`app/src/main/assets/data/hisnul_muslim_am.json` directly and run
`python3 tools/validate_asset.py`.

## Rebuilding the asset from scratch

`build_asset.py` needs the English/Arabic dataset (one JSON object with an `English` array;
each chapter has `ID`, `TITLE`, `TEXT[]`, and each dua has `ID`, `ARABIC_TEXT`,
`TRANSLATED_TEXT`, `REPEAT`). Download it from the *Fortress of the Muslim* dataset
project, then:

```bash
python3 tools/build_asset.py /path/to/en.json
```

The script refuses to run if a chapter in `tools/categories.json` is unknown, if a
translation is missing, or if two translation files define the same dua id — so a partial
edit cannot silently ship a gap.

## Review checklist before publishing

- [ ] Arabic text has not been altered (compare with the source dataset / a printed copy).
- [ ] Repeat counts (3×, 7×, 33×, 100× …) still match the Arabic note.
- [ ] Amharic reviewed by a qualified scholar, especially theological terms
      (ሺርክ, ተውባ, ዱዓ, ሰላት, አል-ሙቀድድም, አስ-ሰመድ …).
- [ ] Chapter titles read naturally in Amharic.
- [ ] `python3 tools/validate_asset.py` prints `Asset OK`.
