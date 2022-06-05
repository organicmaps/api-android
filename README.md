# Organic Maps Android API: Getting Started

## Introduction

Organic Maps Android API (hereinafter referred to as *"API Library"* or just *"library"*)
provides interface for client application to perform next tasks:

* Show one or more points on offline map of [Organic Maps app][linkOM]
* Come back to the client application after selecting specific point on the map, by sending [PendingIntent][linkPIntent] with point data when user asks for more information by pressing "More Info" button in Organic Maps app
* Map screen branding : your application's icon and name (or custom title) will be placed at the top.

Thus, you can provide **two way communication between your application and Organic Maps**,
using Organic Maps to show points of interest (POI) and providing more information in your app.

Please refer to [sample application][linkSampleSource] for demo.

## Prerequisites

It is supposed that you are familiar with Android Development.
You should be familiar with concept of [Intents][linkIntents], [library projects][linkLibProj], and [PendingIntents][linkPIntent] (recommended) as well.

Organic Maps works from *Android SDK version 21 (Android 5)* and above

## Integration
First step is to clone [repository][linkRepo] or download it as an archive.

When your are done you find two folders: *lib* and *sample-app-capitals*. First one is a library project that you should add to your project.
You don't need any additional permissions in your AndroidManifest.xml to use API library, so you can write real code straight away, calling for different `OrganicMapsApi` methods (more details below).

## Classes Overview and HOW TO
Core classes you will work with are:

* [app.organicmaps.api.OrganicMapsApi][linkApiClass] - static class with methods such as `showPointOnMap(Activity, double, double, String)` etc.
* [app.organicmaps.api.OMPoint][linkPointClass] - model of POI, includes lat, lon, name, id, and style data.
* [app.organicmaps.api.OMResponse][linkRespClass] - helps you to extract response from Organic Maps by applying `OMResponse.extractFromIntent(Intent)` to Intent. Contains OMPoint data.

### Show Points on the Map

The simplest usage:

    public class MyPerfectActivity extends Activity {
    ...

      void showSomethingOnTheMap(SomeDomainObject arg)
      {
        // Do some work, create lat, lon, and name for point
        final double lat = ...;
        final double lon = ...;
        final String name = ...;
        // Ask Organic Maps to show the point
        OrganicMapsApi.showPointOnMap(this, lat, lon, name);
      }
    ...

    }

For multiple points use [OMPoint][linkPointClass] class:

    void showMultiplePoints(List<SomeDomainObject> list)
    {
      // Convert objects to MMWPoints
      final OMPoint[] points = new OMPoint[list.length];
      for (int i = 0; i < list.size; i++)
      {
        // Get lat, lon, and name from object and assign it to new MMWPoint
        points[i] = new OMPoint(lat, lon, name);
      }
      // Show all point on the map, you could also provide some title
      OrganicMapsApi.showPointsOnMap(this, "Look at my points, my points are amazing!", points);
    }


### Ask Organic Maps to Call my App

We support PendingIntent interaction (just like Android native
NotificationManager does). You should specify ID for each point to
distinguish it later, and PentingIntent that Organic Maps will send back to
your application when user press "More Info" button :

    // Here is how to pass points with ID ant PendingIntent
    void showMultiplePointsWithPendingIntent(List<SomeDomainObject> list, PendingIntent pendingIntent)
    {
      // Convert objects to OMPoints
      final OMPoint[] points = new OMPoint[list.length];
      for (int i = 0; i < list.size; i++)
      {
        //                                      ||
        //                                      ||
        //                                      \/
        //         Now you should specify string ID for each point
        points[i] = new OMPoint(lat, lon, name, id);
      }
      // Show all points on the map, you could also provide some title
      OrganicMapsApi.showPointsOnMap(this, "This title says that user should choose some point", pendingIntent, points);
    }

    //Code below shows general way to extract response data
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Handle intent you specified with PandingIntent
        // Now it has additional information (OMPoint).
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
      super.onNewIntent(intent);
      // if defined your activity as "SingleTop"- you should use onNewIntent callback
      handleIntent(intent);
    }

    void handleIntent(Intent intent)
    {
      // Apply OMResponse extraction method to intent
      final OMResponse mwmResponse = OMResponse.extractFromIntent(this, intent);
      // Here is your point that user selected
      final OMPoint point = mwmResponse.getPoint();
      // Now, for instance you can do some work depending on point id
      processUserInteraction(point.getId());
    }

## FAQ

#### How should I detect if user has Organic Maps installed?
`OrganicMapsApi.isOrganicMapsInstalled(Context)` will return `true` if user has *Lite* or *Pro* version that supports API call installed.

#### Which versions of Organic Maps support API calls?
All versions since 2.4.0 and above support API calls.

#### What will happen if I call for `OrganicMapsApi.showPoint()` but Organic Maps application is not installed?
Nothing serious. API library will show simple dialog with gentle offer to download Organic Maps. You can see how it looks like below.

![Please install us](site/images/dlg.png)

## Sample Code and Application

* [Sample Application at Google Play][linkSampleGooglePlay]
* [Sample Application Source Code][linkSampleSource]

-------------------------------------------------------------------------------
## API Code License
Copyright (c) 2019, MY.COM B.V.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

[linkOM]: https://organicmaps.app/ "Organic Maps"
[linkPIntent]: http://developer.android.com/reference/android/app/PendingIntent.html "PendingIntent"
[linkRepo]: https://github.com/mapswithme/api-android "GitHub Repository"
[linkLibProj]: http://developer.android.com/tools/projects/index.html#LibraryProjects "Android Library Project"
[linkIntents]: http://developer.android.com/guide/components/intents-filters.html "Intents and Intent Filters"
[linkApiClass]: lib/src/com/mapswithme/maps/api/OrganicMapsApi.java "OrganicMapsApi.java"
[linkPointClass]: lib/src/com/mapswithme/maps/api/OMPoint.java "OMPoint.java"
[linkRespClass]: lib/src/com/mapswithme/maps/api/OMResponse.java  "OMResponse.java"
[linkSampleSource]: https://github.com/mapswithme/api-android/tree/master/sample-app-capitals "Api Source Code"
[linkSampleGooglePlay]: http://play.google.com/store/apps/details?id=com.mapswithme.capitals "Api Demo .apk"
[linkTravelGuides]: http://www.guidewithme.com
