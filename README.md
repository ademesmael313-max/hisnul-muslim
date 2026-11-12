# ሂስኑል ሙስሊም — Hisnul Muslim in Amharic

[![Build APK](https://github.com/ademesmael313-max/hisnul-muslim/actions/workflows/build-apk.yml/badge.svg)](https://github.com/ademesmael313-max/hisnul-muslim/actions/workflows/build-apk.yml)

A complete, offline Android app of **Hisnul Muslim (Fortress of the Muslim)** with an
**Amharic translation of all 267 supplications** across **132 chapters**, built with Kotlin
and Jetpack Compose. Open the folder in Android Studio, press ▶, and you get an APK.

<p align="center">
  <img src="docs/icon-512.png" width="140" alt="App icon" />
</p>

> **በአማርኛ፦** ይህ መተግበሪያ የሂስኑል ሙስሊምን 132 ምዕራፎችና 267 ዱዓዎች በአማርኛ ትርጉም አንድ ላይ ያሰባስባል። ሙሉ በሙሉ ከመስመር ውጪ ይሠራል፤ ፍለጋ፣ ተወዳጆች፣ የፊደል መጠን ማስተካከያ፣ የዚክር ቆጣሪና የተስቢሕ ቆጣሪ አሉት። ፕሮጀክቱን በAndroid Studio ከፍተው በቀጥታ APK ማውጣት ይችላሉ።

---

## ኤፒኬ ማውረድ / Download the APK

Every push builds the app on GitHub. Grab the ready-to-install file from the
**[Releases page](https://github.com/ademesmael313-max/hisnul-muslim/releases)** — or download the
`hisnul-muslim-amharic-debug.apk` artifact from the latest
[Actions run](https://github.com/ademesmael313-max/hisnul-muslim/actions/workflows/build-apk.yml)
(**Artifacts → hisnul-muslim-amharic-apk**).

On the phone: open the downloaded file → allow "install unknown apps" for your browser → install.
Android 7.0 (API 24) or newer is required. The app works fully offline.

---

## What is inside

| | |
|---|---|
| **Content** | 132 chapters · 267 duas · Arabic text + Amharic meaning |
| **Categories** | 12 themed groups (morning/evening, prayer, travel, Ḥajj, illness, food, social etiquette…) |
| **Search** | Amharic, Arabic (diacritic-insensitive: works without harakāt) and chapter titles |
| **Reading** | Adjustable Arabic & Amharic font sizes, show/hide Arabic or translation |
| **Dhikr tools** | Per-dua tap counter (respects the "repeat 3×/33×/100×" counts) and a standalone tasbīḥ counter |
| **Personal** | Bookmarks, light/dark/system theme, copy & share any dua |
| **Dua of the day** | One dua per day, deterministic (changes at midnight) |
| **Privacy** | No internet permission, no analytics, no ads — everything is bundled and stored on device |

Screens: **Home** (search, dua of the day, category grid, chapter list) · **Chapter** ·
**Favourites** · **Settings** · **About** · **Tasbīḥ counter**.

---

## Requirements

| Tool | Version |
|---|---|
| Android Studio | Ladybug (2024.2.1) or newer — older versions work too, just update the Gradle JDK |
| JDK | 17 (the one bundled with Android Studio is fine) |
| Android SDK | Platform 34 installed (`compileSdk = 34`) |
| Gradle | provided by the wrapper (8.7) — no manual install |
| Min Android | 7.0 (API 24) |

Kotlin 1.9.24 · AGP 8.5.2 · Compose BOM 2024.06.00 · Material 3.

---

## Build it

### Android Studio
1. **File → Open** and select this folder (the one containing `settings.gradle.kts`).
2. Let Gradle sync finish (first sync downloads dependencies).
3. Run **app** on an emulator or a USB device (`▶`).

### Command line
```bash
./gradlew assembleDebug          # app/build/outputs/apk/debug/app-debug.apk
./gradlew installDebug           # build + install on a connected device
./gradlew bundleRelease          # .aab for Play Store (sign it first, see below)
```

The debug APK uses `applicationId com.hisnulmuslim.amharic.debug`, so it can live
next to a release install.

---

## Project layout

```
.
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── assets/data/hisnul_muslim_am.json    ← the whole book (Arabic + Amharic)
│       ├── java/com/hisnulmuslim/amharic/
│       │   ├── MainActivity.kt
│       │   ├── BookViewModel.kt
│       │   ├── data/          Models, JSON repository, DataStore settings
│       │   └── ui/
│       │       ├── HisnulApp.kt        navigation graph
│       │       ├── components/         DuaCard, CategoryCard, … 
│       │       ├── screens/            Home, Chapter, Favorites, Settings, About, Tasbih
│       │       └── theme/              colours, Ethiopic typography, Material 3 theme
│       └── res/
│           ├── font/noto_sans_ethiopic.ttf
│           ├── mipmap-*/ic_launcher*.png
│           └── values/{strings,colors,themes}.xml   ← all UI text is Amharic
├── tools/                      content pipeline (Python, not part of the APK)
│   ├── translations/           chapter titles + Amharic text of every dua
│   ├── categories.json         the 12 categories and their chapter mapping
│   ├── build_asset.py          merges Arabic source + translations → asset JSON
│   └── validate_asset.py       CI gate: structure, gaps, placeholders
└── .github/workflows/build-apk.yml   builds the APK on GitHub and uploads it as an artifact
```

---

## Where the text comes from

* **Arabic** — the open *Fortress of the Muslim* dataset (hisnmuslim.com export),
  chapter structure, narration notes and repeat counts included.
* **Amharic** — written for this project (`tools/translations/part*.json`), one entry per
  dua, meaning-focused rather than word-for-word. Chapter titles, category names and the
  whole UI are Amharic too.
* **Font** — Noto Sans Ethiopic, bundled so Amharic renders identically on every phone
  (SIL Open Font License 1.1, see `docs/LICENSE-NotoSansEthiopic.txt`).

> **Please have a scholar review the Amharic.** Translations of religious texts should be
> checked before publishing; the Arabic text is the authoritative source. See
> [`docs/CONTENT.md`](docs/CONTENT.md) for how to correct a single dua in two minutes.

### Editing or adding a dua

1. Edit `tools/translations/part*.json` (keys are the dua ids).
2. Rebuild the asset:
   ```bash
   python3 tools/build_asset.py path/to/en.json     # source dataset, see docs/CONTENT.md
   python3 tools/validate_asset.py                  # must print "Asset OK"
   ```
3. Rebuild the app. Nothing else to touch — the asset is read at runtime.

You can also edit `app/src/main/assets/data/hisnul_muslim_am.json` directly if you only
need a quick text fix; just keep it valid JSON.

---

## Customising

| What | Where |
|---|---|
| Colours (emerald/gold palette) | `ui/theme/Color.kt` |
| Font sizes and typing | `ui/theme/Type.kt` |
| Any Amharic UI label | `res/values/strings.xml` |
| App name / package | `res/values/strings.xml`, `applicationId` in `app/build.gradle.kts` |
| Categories | `tools/categories.json`, then rebuild the asset |
| Launcher icon | replace `res/mipmap-*/ic_launcher*.png` and `docs/icon-512.png` |

---

## Signing a release

Create a keystore once:

```bash
keytool -genkey -v -keystore hisnul.jks -keyalg RSA -keysize 2048 \
        -validity 10000 -alias hisnul
```

Then add a `keystore.properties` **outside version control**:

```properties
storeFile=../hisnul.jks
storePassword=…
keyAlias=hisnul
keyPassword=…
```

`app/build.gradle.kts` already reads that file. As soon as it exists, release builds are signed
with your key; while it does not exist, release builds fall back to the debug key so that the APK
you download from CI is still installable. Keep the keystore and the properties file out of Git
(both are already in `.gitignore`).

Release builds have R8 + resource shrinking enabled, so the APK stays small (~3 MB).

---

## Building the APK on GitHub

`.github/workflows/build-apk.yml` runs on every push/PR (and manually via
**Actions → Build APK → Run workflow**). It validates the content asset, runs Android lint, builds
the debug and release APKs and uploads them as artifacts. Pushing a tag
(`git tag v1.0.0 && git push origin v1.0.0`) additionally attaches both APKs to a GitHub
**Release**, which gives you a permanent download link you can open on a phone.

---

## Licence

* App code: MIT — see [`LICENSE`](LICENSE).
* Arabic dua text and chapter structure: from the widely distributed *Hisnul Muslim*
  dataset; keep the book attribution (Saʿīd ibn ʿAlī ibn Wahf al-Qaḥṭānī) in the app's
  About screen, which is already the case.
* Amharic translation and UI: © the project authors, released under the same MIT terms —
  review before commercial reuse.
* Noto Sans Ethiopic: SIL OFL 1.1 (`docs/LICENSE-NotoSansEthiopic.txt`).
