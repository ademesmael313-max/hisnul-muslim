# Changelog

All notable changes to **ሂስኑል ሙስሊም / Hisnul Muslim (Amharic)**.

## 1.1.0 — 2026-11-12

Polish and hardening release.

### Added
* **Favourites are grouped by chapter** with a dua count per group and a total in the app bar.
* **Confirmation dialogs** before the two destructive actions (reset all dhikr counters, clear
  favourites) so nothing is lost by accident.
* **Crash-proof start-up**: the book is parsed off the main thread; if the bundled asset is ever
  missing or corrupt the app shows an Amharic error screen with a *retry* button instead of
  closing.
* **Unit tests** (`app/src/test`) for the JSON parser, the diacritic-insensitive Arabic search,
  and the shipped asset (chapter/dua counts, unique ids, no empty translations).
* **Play Store bundle** (`bundleRelease`, `.aab`) is built by CI next to the APKs.
* Tablet/foldable layout: content is capped at 720 dp and centred instead of stretching.
* Section headers are exposed to TalkBack as headings.

### Changed
* Arabic text is now justified and laid out right-to-left explicitly, which fixes punctuation
  and parenthesis placement on long passages.
* Dua counts use one shared string resource everywhere (chapter bar, chapter rows, search
  results, favourites).
* `BookViewModel` exposes `BookUiState` (loading / ready / failed) instead of a non-null book.

### Fixed
* A missing chapter route no longer renders a hard-coded string; it uses a proper resource.
* Recovering from a failed load no longer needs an app restart.

## 1.0.0 — 2026-11-12

First release.

* 132 chapters · 267 duas · 12 categories, fully offline, Arabic + Amharic.
* Home with search (Amharic, Arabic without harakāt, chapter titles), dua of the day, category
  grid; chapter reader with adjustable font sizes and per-dua dhikr counters; favourites;
  light/dark/system theme; tasbīḥ counter; About screen.
* Noto Sans Ethiopic bundled; generated adaptive launcher icon; no permissions at all.
* CI builds and lints the app, runs 5 instrumented Compose tests on an Android 11 emulator and
  publishes the APKs to GitHub Releases.
