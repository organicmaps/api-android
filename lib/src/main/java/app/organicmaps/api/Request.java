/******************************************************************************
 Copyright (c) 2022, Organic Maps OÜ. All rights reserved.
 Copyright (c) 2013, MapsWithMe GmbH. All rights reserved.

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

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class Request
{

  // **
  private List<Point> mPoints = new ArrayList<>();
  private PendingIntent mPendingIntent;
  private String mTitle;
  private double mZoomLevel = 1;
  private boolean mReturnOnBalloonClick;
  private boolean mPickPoint = false;
  private String mCustomButtonName = "";
  // **

  private static StringBuilder createMwmUrl(Context context, String title, double zoomLevel, List<Point> points)
  {
    final StringBuilder urlBuilder = new StringBuilder("om://map?");
    // version
    urlBuilder.append("v=").append(Const.API_VERSION).append("&");
    // back url, always not null
    urlBuilder.append("backurl=").append(getCallbackAction(context)).append("&");
    // title
    appendIfNotNull(urlBuilder, "appname", title);
    // zoom
    appendIfNotNull(urlBuilder, "z", isValidZoomLevel(zoomLevel) ? String.valueOf(zoomLevel) : null);

    // points
    for (final Point point : points)
    {
      if (point != null)
      {
        urlBuilder.append("ll=").append(String.format(Locale.US, "%f,%f&", point.getLat(), point.getLon()));

        appendIfNotNull(urlBuilder, "n", point.getName());
        appendIfNotNull(urlBuilder, "id", point.getId());
        appendIfNotNull(urlBuilder, "s", point.getStyleForUrl());
      }
    }

    return urlBuilder;
  }

  private static String getCallbackAction(Context context)
  {
    return Const.CALLBACK_PREFIX + context.getPackageName();
  }

  private static Intent addCommonExtras(Context context, Intent intent)
  {
    intent.putExtra(Const.EXTRA_CALLER_APP_INFO, context.getApplicationInfo());
    intent.putExtra(Const.EXTRA_API_VERSION, Const.API_VERSION);

    return intent;
  }

  private static StringBuilder appendIfNotNull(StringBuilder builder, String key, String value)
  {
    if (value != null)
      builder.append(key).append("=").append(Uri.encode(value)).append("&");

    return builder;
  }

  private static boolean isValidZoomLevel(double zoom)
  {
    return zoom >= Api.ZOOM_MIN && zoom <= Api.ZOOM_MAX;
  }

  public Request setCustomButtonName(String buttonName)
  {
    mCustomButtonName = buttonName != null ? buttonName : "";
    return this;
  }

  public Request setTitle(String title)
  {
    mTitle = title;
    return this;
  }

  public Request setPickPointMode(boolean pickPoint)
  {
    mPickPoint = pickPoint;
    return this;
  }

  public Request addPoint(Point point)
  {
    mPoints.add(point);
    return this;
  }

  public Request addPoint(double lat, double lon, String name, String id)
  {
    return addPoint(new Point(lat, lon, name, id));
  }

  public Request setPoints(Collection<Point> points)
  {
    mPoints = new ArrayList<Point>(points);
    return this;
  }

  // Below are utilities from OrganicMapsApi because we are not "Feature Envy"

  public Request setReturnOnBalloonClick(boolean doReturn)
  {
    mReturnOnBalloonClick = doReturn;
    return this;
  }

  public Request setZoomLevel(double zoomLevel)
  {
    mZoomLevel = zoomLevel;
    return this;
  }

  public Request setPendingIntent(PendingIntent pi)
  {
    mPendingIntent = pi;
    return this;
  }

  public Intent toIntent(Context context)
  {
    final Intent mwmIntent = new Intent(Const.ACTION_OM_REQUEST);

    // url
    final String mwmUrl = createMwmUrl(context, mTitle, mZoomLevel, mPoints).toString();
    mwmIntent.putExtra(Const.EXTRA_URL, mwmUrl);
    // title
    mwmIntent.putExtra(Const.EXTRA_TITLE, mTitle);
    // more
    mwmIntent.putExtra(Const.EXTRA_RETURN_ON_BALLOON_CLICK, mReturnOnBalloonClick);
    // pick point
    mwmIntent.putExtra(Const.EXTRA_PICK_POINT, mPickPoint);
    // custom button name
    mwmIntent.putExtra(Const.EXTRA_CUSTOM_BUTTON_NAME, mCustomButtonName);

    final boolean hasIntent = mPendingIntent != null;
    mwmIntent.putExtra(Const.EXTRA_HAS_PENDING_INTENT, hasIntent);
    if (hasIntent)
      mwmIntent.putExtra(Const.EXTRA_CALLER_PENDING_INTENT, mPendingIntent);

    addCommonExtras(context, mwmIntent);

    return mwmIntent;
  }

  /**
   * @Hidden This method is internal only.
   * Used for compatibility.
   */
  Request setPoints(Point[] points)
  {
    return setPoints(Arrays.asList(points));
  }

}
