# Organic Maps Android API

A thin Java library that wraps the [Organic Maps deep-link API](https://omaps.app/api) so your app can:

* show one or more points of interest on the offline map in [Organic Maps][linkOM];
* let the user pick a point on that map and receive its coordinates back via [Activity Result][linkActivityResult];
* brand the map screen with your app's name as a title.

Two-way communication between your app and Organic Maps lets you use OM as the map UI while keeping the user's tap inside your own activity stack.

> The library is convenience over [deep links](https://omaps.app/api). You can also build the `om://` URIs yourself; read [`MapRequest.java`][linkMapRequest] / [`CrosshairRequest.java`][linkCrosshair] for the format.

## Prerequisites

* Familiarity with Android development, [Intents][linkIntents], and the [Activity Result APIs][linkActivityResult].
* Organic Maps runs on Android 5 (SDK 21) and above.

## Setup

Clone the repo and add `:lib` as a Gradle module of your project — or build the AAR yourself:

```bash
./gradlew :lib:assembleRelease   # produces lib/build/outputs/aar/lib-release.aar
```

No additional permissions are required in your `AndroidManifest.xml`.

The two samples in this repo are good starting points:

| Module                  | Demonstrates                                                      |
|-------------------------|-------------------------------------------------------------------|
| `:sample-app-capitals`  | Show many points and round-trip the user's pick back to your app. |
| `:sample-pick-point`    | Crosshair pick using the modern `ActivityResultLauncher` flow.    |

## Core classes

* [`OrganicMapsApi`][linkApiClass] — entry point with `showPointOnMap` / `showPointsOnMap` / `sendRequest` helpers and `canHandleOrganicMapsIntents` / `isOrganicMapsPackageInstalled` probes.
* [`MapRequest`][linkMapRequest] — fluent builder for `om://map?…` intents. Call `setPickPointMode(true)` to ask OM to return the picked point.
* [`CrosshairRequest`][linkCrosshair] — fluent builder for `om://crosshair?…` intents (always pick-point).
* [`Point`][linkPointClass] — POI model: `lat`, `lon`, `name`, optional `id`, optional `Style`.
* [`PickPointResponse`][linkRespClass] — parses the result intent into a `Point` plus the zoom level.

## Show points on the map

```java
// Single point — title defaults to the point name.
OrganicMapsApi.showPointOnMap(this, lat, lon, "Eiffel Tower");

// Multiple points with a custom title.
final ArrayList<Point> points = new ArrayList<>();
for (SomeDomainObject obj : list)
  points.add(new Point(obj.lat, obj.lon, obj.name));
OrganicMapsApi.showPointsOnMap(this, "Capitals of the World", points);
```

If Organic Maps is not installed, the library shows a download dialog instead of crashing.

## Round-trip: let the user pick a point

Register an `ActivityResultLauncher` and launch a `MapRequest` (or `CrosshairRequest`) with pick-point mode enabled:

```java
private final ActivityResultLauncher<Intent> pickPoint = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    result -> {
      if (result.getResultCode() != RESULT_OK || result.getData() == null)
        return;
      final PickPointResponse response = PickPointResponse.extractFromIntent(result.getData());
      if (response == null)
        return;
      final Point point = response.getPoint();
      // point.getId() lets you map the result back to your domain object.
      onUserPicked(point, response.getZoomLevel());
    });

void launchPicker(List<MyPoi> pois)
{
  final ArrayList<Point> points = new ArrayList<>(pois.size());
  for (MyPoi poi : pois)
    points.add(new Point(poi.lat, poi.lon, poi.name, poi.id));

  final Intent intent = new MapRequest()
      .setAppName(getString(R.string.app_name))
      .setPoints(points)
      .setPickPointMode(true)        // ask OM to send the picked point back
      .toIntent();

  if (!OrganicMapsApi.canHandleOrganicMapsIntents(this))
  {
    new DownloadDialog(this).show();
    return;
  }
  pickPoint.launch(intent);
}
```

For a free-form pick (no candidate points) use `CrosshairRequest` instead — see [`sample-pick-point/MainActivity`][linkPickPointMain].

## FAQ

#### Which versions of Organic Maps support this API?

All versions since 2022-07-26.

#### What happens if Organic Maps is not installed?

`OrganicMapsApi.sendRequest` (and the manual flow above) falls back to `DownloadDialog`, which links to <https://omaps.app/get?api>.

#### Can I check beforehand whether Organic Maps will handle the intent?

Yes — `OrganicMapsApi.canHandleOrganicMapsIntents(context)` does a `resolveActivity`. `OrganicMapsApi.isOrganicMapsPackageInstalled(context)` checks for the known package IDs (`app.organicmaps`, `.beta`, `.debug`, `.web`).

## License

Copyright (c) 2026, Organic Maps OÜ.

Copyright (c) 2019, MY.COM B.V.

Copyright (c) 2013, MapsWithMe GmbH.

All rights reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

[linkOM]: https://organicmaps.app/ "Organic Maps"
[linkIntents]: https://developer.android.com/guide/components/intents-filters "Intents and Intent Filters"
[linkActivityResult]: https://developer.android.com/training/basics/intents/result "Getting a result from an activity"
[linkApiClass]: lib/src/main/java/app/organicmaps/api/OrganicMapsApi.java "OrganicMapsApi.java"
[linkMapRequest]: lib/src/main/java/app/organicmaps/api/MapRequest.java "MapRequest.java"
[linkCrosshair]: lib/src/main/java/app/organicmaps/api/CrosshairRequest.java "CrosshairRequest.java"
[linkPointClass]: lib/src/main/java/app/organicmaps/api/Point.java "Point.java"
[linkRespClass]: lib/src/main/java/app/organicmaps/api/PickPointResponse.java "PickPointResponse.java"
[linkPickPointMain]: sample-pick-point/src/main/java/app/organicmaps/api/sample/pick_point/MainActivity.java "sample-pick-point MainActivity.java"
