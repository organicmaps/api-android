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

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PickPointResponse
{
  private Point mPoint;
  private double mZoomLevel;

  private PickPointResponse() {}

  /**
   * Factory method to extract response from intent.
   *
   * @param intent an intent returned by Organic Maps when the user picked a point
   * @return parsed response, or {@code null} if {@code intent} has no extras
   */
  public static @Nullable PickPointResponse extractFromIntent(final @NonNull Intent intent)
  {
    final Bundle extras = intent.getExtras();
    if (extras == null)
      return null;
    final PickPointResponse response = new PickPointResponse();
    final double lat = extras.getDouble(Const.EXTRA_POINT_LAT);
    final double lon = extras.getDouble(Const.EXTRA_POINT_LON);
    final String name = extras.getString(Const.EXTRA_POINT_NAME);
    final String id = extras.getString(Const.EXTRA_POINT_ID);
    response.mPoint = new Point(lat, lon, name, id);
    response.mZoomLevel = extras.getDouble(Const.EXTRA_ZOOM_LEVEL);
    return response;
  }

  /**
   * @return selected point
   */
  public Point getPoint()
  {
    return mPoint;
  }

  /**
   * @return current zoom level
   */
  public double getZoomLevel()
  {
    return mZoomLevel;
  }

  @Override
  public String toString()
  {
    return "PointResponse [Point=" + mPoint + "]";
  }
}
