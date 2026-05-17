/*
 Copyright (c) 2026, Organic Maps OÜ. All rights reserved.

 Redistribution and use in source and binary forms, with or without modification,
 are permitted provided that the following conditions are met:

 Redistributions of source code must retain the above copyright notice, this list
 of conditions and the following disclaimer. Redistributions in binary form must
 reproduce the above copyright notice, this list of conditions and the following
 disclaimer in the documentation and/or other materials provided with the
 distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 OF SUCH DAMAGE.
 */
package app.organicmaps.api;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.util.ArrayList;

public final class OrganicMapsApi
{
  static final String PACKAGE_NAME_RELEASE = "app.organicmaps";
  static final String PACKAGE_NAME_BETA = "app.organicmaps.beta";
  static final String PACKAGE_NAME_DEBUG = "app.organicmaps.debug";
  static final String PACKAGE_NAME_WEB = "app.organicmaps.web";

  private OrganicMapsApi() {}

  public static void showPointOnMap(final Activity activity, final double lat, final double lon, final String name)
  {
    final ArrayList<Point> points = new ArrayList<>(1);
    points.add(new Point(lat, lon, name));
    showPointsOnMap(activity, name, points);
  }

  public static void showPointsOnMap(final Activity activity, final String name, final ArrayList<Point> points)
  {
    final Intent intent = new MapRequest()
        .setPoints(points)
        .setAppName(name)
        .toIntent();
    sendRequest(activity, intent);
  }

  public static void sendRequest(final Activity caller, final Intent intent)
  {
    if (canHandleOrganicMapsIntents(caller))
      caller.startActivity(intent);
    else
      new DownloadDialog(caller).show();
  }

  /**
   * Detects if any handler for OrganicMaps intents is installed on the device.
   */
  public static boolean canHandleOrganicMapsIntents(final Context context)
  {
    final ComponentName c = new MapRequest().toIntent().resolveActivity(context.getPackageManager());
    return c != null;
  }

  /**
   * Detects if one of the known OrganicMaps packages is installed.
   */
  public static boolean isOrganicMapsPackageInstalled(final Context context)
  {
    final PackageManager pm = context.getPackageManager();
    return pm.getLaunchIntentForPackage(PACKAGE_NAME_RELEASE) != null
        || pm.getLaunchIntentForPackage(PACKAGE_NAME_BETA) != null
        || pm.getLaunchIntentForPackage(PACKAGE_NAME_DEBUG) != null
        || pm.getLaunchIntentForPackage(PACKAGE_NAME_WEB) != null;
  }
}
