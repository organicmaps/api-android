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

import android.content.Context;
import android.content.Intent;

public class OMResponse
{
  private OMPoint mPoint;
  private double   mZoomLevel;

  /**
   *
   * @return point, for which user requested more information in Organic Maps application.
   */
  public OMPoint getPoint()     { return mPoint; }
  public boolean  hasPoint()     { return mPoint != null; }
  public double   getZoomLevel() { return mZoomLevel; }

  @Override
  public String toString()
  {
    return "OMResponse [SelectedPoint=" + mPoint + "]";
  }

  /**
   * Factory method to extract response data from intent.
   *
   * @param context
   * @param intent
   * @return
   */
  public static OMResponse extractFromIntent(Context context, Intent intent)
  {
    final OMResponse response = new OMResponse();
    // parse point
    final double lat = intent.getDoubleExtra(Const.EXTRA_OM_RESPONSE_POINT_LAT, INVALID_LL);
    final double lon = intent.getDoubleExtra(Const.EXTRA_OM_RESPONSE_POINT_LON, INVALID_LL);
    final String name = intent.getStringExtra(Const.EXTRA_OM_RESPONSE_POINT_NAME);
    final String id = intent.getStringExtra(Const.EXTRA_OM_RESPONSE_POINT_ID);

    // parse additional info
    response.mZoomLevel = intent.getDoubleExtra(Const.EXTRA_OM_RESPONSE_ZOOM, 9);

    if (lat != INVALID_LL && lon != INVALID_LL)
      response.mPoint = new OMPoint(lat, lon, name, id);
    else
      response.mPoint = null;

    return response;
  }

  private final static double INVALID_LL =  Double.MIN_VALUE;

  private OMResponse() {}
}
