/******************************************************************************
   Copyright (c) 2022, Organic Maps OÜ. All rights reserved.

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
 ******************************************************************************/
package app.organicmaps.api;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;


public final class OrganicMapsApi
{

  /**
   * Most detailed level, buildings and trees are seen.
   */
  public static final double ZOOM_MAX = 19;
  /**
   * Least detailed level, continents are seen.
   */
  public static final double ZOOM_MIN = 1;


  public static void showOrganicMapsUrl(Activity caller, PendingIntent pendingIntent, double zoomLevel, String url)
  {
    final Uri uri = Uri.parse(url);
    final String latlon[] = uri.getQueryParameter("ll").split(",");
    final double lat = Double.parseDouble(latlon[0]);
    final double lon = Double.parseDouble(latlon[1]);
    final String name = uri.getQueryParameter("n");
    final String id = uri.getQueryParameter("id");

    showPointsOnMap(caller, name, zoomLevel, pendingIntent, new OMPoint(lat, lon, name, id));
  }

  public static void sendRequest(Activity caller, MwmRequest request)
  {
    final Intent mwmIntent = request.toIntent(caller);

    if (isOrganicMapsInstalled(caller))
    {
      // Match activity for intent
      final ActivityInfo aInfo = caller.getPackageManager().resolveActivity(mwmIntent, 0).activityInfo;
      mwmIntent.setClassName(aInfo.packageName, aInfo.name);
      caller.startActivity(mwmIntent);
    }
    else
      (new DownloadOrganicMapsDialog(caller)).show();
  }

  /**
   * Shows single point on the map.
   *
   * @param caller
   * @param lat
   * @param lon
   * @param name
   */
  public static void showPointOnMap(Activity caller, double lat, double lon, String name)
  {
    showPointsOnMap(caller, (String) null, (PendingIntent) null, new OMPoint(lat, lon, name));
  }

  /**
   * Shows single point on the map using specified zoom level in range from
   * {@link OrganicMapsApi#ZOOM_MIN} to {@link OrganicMapsApi#ZOOM_MAX}.
   *
   * @param caller
   * @param lat
   * @param lon
   * @param name
   * @param zoomLevel
   */
  public static void showPointOnMap(Activity caller, double lat, double lon, String name, double zoomLevel)
  {
    showPointsOnMap(caller, (String) null, zoomLevel, (PendingIntent) null, new OMPoint(lat, lon, name));
  }

  /**
   * Shows set of points on the map.
   *
   * @param caller
   * @param title
   * @param points
   */
  public static void showPointsOnMap(Activity caller, String title, OMPoint... points)
  {
    showPointsOnMap(caller, title, null, points);
  }

  /**
   * Shows set of points on the maps and allows OrganicMapsApplication to send
   * {@link PendingIntent} provided by client application.
   *
   * @param caller
   * @param title
   * @param pendingIntent
   * @param points
   */
  public static void showPointsOnMap(Activity caller, String title, PendingIntent pendingIntent, OMPoint... points)
  {
    showPointsOnMap(caller, title, -1, pendingIntent, points);
  }

  private static void showPointsOnMap(Activity caller, String title, double zoomLevel, PendingIntent pendingIntent,
      OMPoint... points)
  {
    final MwmRequest request = new MwmRequest()
                                    .setTitle(title)
                                    .setZoomLevel(zoomLevel)
                                    .setPendingIntent(pendingIntent)
                                    .setPoints(points);
    sendRequest(caller, request);
  }

  public static void pickPoint(Activity caller, String title, PendingIntent pi)
  {
    final MwmRequest request = new MwmRequest()
                                    .setTitle(title)
                                    .setPickPointMode(true)
                                    .setPendingIntent(pi);
    sendRequest(caller, request);
  }

  /**
   * Detects if any version (Lite, Pro) of Organic Maps, which supports API calls
   * are installed on the device.
   *
   * @param context
   * @return
   */
  public static boolean isOrganicMapsInstalled(Context context)
  {
    final Intent intent = new Intent(Const.ACTION_OM_REQUEST);
    return context.getPackageManager().resolveActivity(intent, 0) != null;
  }
}
