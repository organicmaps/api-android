This file provides guidance to AI agents when working with code in this repository.

## Overview

Thin Android library that wraps Organic Maps deep links (`om://...`) so client apps can show POIs on the OM map and receive a selected point back. Distributed as an AAR via `:lib`; the two `sample-*` modules are demo apps, not tests.

Public surface is documented in `README.md`. The library does not require Organic Maps to be installed — when no resolver is found, `DownloadDialog` prompts the user to install it.

## Build

Gradle wrapper 9.4.1 (AGP 9.2.1, Java 17, minSdk 21, targetSdk 36). Modules: `:lib`, `:sample-app-capitals`, `:sample-pick-point`.

```bash
./gradlew assemble                # build everything
./gradlew :lib:assembleRelease    # AAR only
./gradlew :sample-pick-point:installDebug   # install a sample on a connected device
./gradlew clean
```

There is no test suite, no lint task wired up beyond `-Xlint:unchecked -Xlint:deprecation` on `JavaCompile`, and no CI.

## Architecture

Request/response flow over Android Intents:

- **Request builders** construct an `om://`-scheme `Uri` and wrap it in `Intent.ACTION_VIEW`:
  - `MapRequest` → `om://map?appname=&z=&ll=lat,lon&n=&id=&s=&...` (one `ll=`/`n=`/`id=`/`s=` group per point). Setting `pickPointMode` also adds the `EXTRA_PICK_POINT` boolean extra.
  - `CrosshairRequest` → `om://crosshair?appname=`, always with `EXTRA_PICK_POINT`.
- **`OrganicMapsApi`** is the entry point: convenience `showPointOnMap` / `showPointsOnMap` wrap `MapRequest`, and `sendRequest` either `startActivity`s the intent or shows `DownloadDialog` if `resolveActivity` returns null. `canHandleOrganicMapsIntents` resolves the intent; `isOrganicMapsPackageInstalled` checks known package IDs (`app.organicmaps`, `.beta`, `.debug`, `.web`).
- **Response parsing**: `PickPointResponse.extractFromIntent` reads the `EXTRA_POINT_*` and `EXTRA_ZOOM_LEVEL` extras into a `Point`.
- **`Const.java`** is the wire-format contract — URI scheme, authority, and `EXTRA_*` keys. Any change here must match the consuming Organic Maps app, so treat it as a versioned public ABI.
- **`AndroidManifest.xml`** in `:lib` declares a `<queries><package android:name="app.organicmaps"/>` so package-visibility on Android 11+ permits `resolveActivity`. The hard-coded literal is required — `@string/` is not allowed in `<queries>`.

`Point.Style` enumerates the placemark colors OM understands (see TODOs in `Point.java` for unmapped styles).

## Source layout

Sources live under `<module>/src/main/`. The repo's `.gitignore` excludes the Eclipse-era module-root layout (`/*/AndroidManifest.xml`, `/*/build.xml`, `/*/project.properties`, `/*/res/`, `/*/src/com/`) so the legacy `com.mapswithme.*` tree cannot be reintroduced. Edit `src/main/java/app/organicmaps/api/`.

## Conventions

- Java only; package `app.organicmaps.api` for the library, `app.organicmaps.api.sample.*` for samples.
- Fluent builders: `MapRequest`/`CrosshairRequest` setters return `@NonNull` self.
- Brace style is Allman (braces on their own line) in existing files — match it when editing.
- Per-file BSD-2-clause headers are kept on every source file.
