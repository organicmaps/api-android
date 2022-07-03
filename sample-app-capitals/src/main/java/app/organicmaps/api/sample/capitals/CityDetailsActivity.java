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
package app.organicmaps.api.sample.capitals;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;

import app.organicmaps.api.PickPointResponse;
import app.organicmaps.api.Point;
import app.organicmaps.api.MapRequest;

public class CityDetailsActivity extends Activity
{
  private static final int REQ_CODE_CITY = 1;
  public static String EXTRA_POINT = "point";
  private TextView mName;
  private TextView mAltNames;
  private TextView mCountry;

  private TextView mLat;
  private TextView mLon;
  private TextView mElev;

  private TextView mPopulation;
  private TextView mTimeZone;

  private City mCity;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.city_details_activity);

    mName = (TextView) findViewById(R.id.name);
    mAltNames = (TextView) findViewById(R.id.altNames);
    mCountry = (TextView) findViewById(R.id.cCode);

    mLat = (TextView) findViewById(R.id.lat);
    mLon = (TextView) findViewById(R.id.lon);
    mElev = (TextView) findViewById(R.id.elevation);

    mPopulation = (TextView) findViewById(R.id.population);
    mTimeZone = (TextView) findViewById(R.id.timeZone);

    findViewById(R.id.showOnMap).setOnClickListener(v -> {
      final Intent intent = new MapRequest()
          .addPoint(mCity.toPoint())
          .setTitle(getString(R.string.app_name))
          .toIntent();
      startActivityForResult(intent, REQ_CODE_CITY);
    });

    final Intent data = getIntent().getParcelableExtra(EXTRA_POINT);
    handleResponse(data);
  }

  private void handleResponse(final @NonNull Intent data)
  {
    final PickPointResponse response = PickPointResponse.extractFromIntent(data);
    final Point point = response.getPoint();
    mCity = City.fromPoint(point);

    if (mCity != null)
    {
      mName.setText(mCity.getName());
      mAltNames.setText(mCity.getAltNames());
      mCountry.setText(mCity.getCountryCode());

      mLat.setText(mCity.getLat() + "");
      mLon.setText(mCity.getLon() + "");
      final String evel = mCity.getElevation() != -9999 ? String.valueOf(mCity.getElevation()) : "No Data";
      mElev.setText(evel);

      final String popul = mCity.getPopulation() != -1 ? String.valueOf(mCity.getPopulation()) : "No Data";
      mPopulation.setText(popul);
      mTimeZone.setText(mCity.getTimeZone());
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode != REQ_CODE_CITY || resultCode != RESULT_OK)
      return;

    handleResponse(data);
  }
}