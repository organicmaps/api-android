/*
 Copyright (c) 2026, Organic Maps OÜ. All rights reserved.
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
 */
package app.organicmaps.api.sample.capitals;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import app.organicmaps.api.PickPointResponse;
import app.organicmaps.api.Point;
import app.organicmaps.api.MapRequest;

public class CityDetailsActivity extends AppCompatActivity
{
  public static final String EXTRA_POINT = "point";
  private TextView mName;
  private TextView mAltNames;
  private TextView mCountry;

  private TextView mLat;
  private TextView mLon;
  private TextView mElev;

  private TextView mPopulation;
  private TextView mTimeZone;

  private City mCity;

  private final ActivityResultLauncher<Intent> mShowOnMap = registerForActivityResult(
      new ActivityResultContracts.StartActivityForResult(),
      result -> {
        if (result.getResultCode() != RESULT_OK || result.getData() == null)
          return;
        handleResponse(result.getData());
      });

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.city_details_activity);

    ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
      final Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
      v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
      return WindowInsetsCompat.CONSUMED;
    });

    mName = findViewById(R.id.name);
    mAltNames = findViewById(R.id.altNames);
    mCountry = findViewById(R.id.cCode);

    mLat = findViewById(R.id.lat);
    mLon = findViewById(R.id.lon);
    mElev = findViewById(R.id.elevation);

    mPopulation = findViewById(R.id.population);
    mTimeZone = findViewById(R.id.timeZone);

    findViewById(R.id.showOnMap).setOnClickListener(v -> {
      final Intent intent = new MapRequest()
          .addPoint(mCity.toPoint())
          .setAppName(getString(R.string.app_name))
          .setPickPointMode(true)
          .toIntent();
      mShowOnMap.launch(intent);
    });

    final Intent data = getIntent().getParcelableExtra(EXTRA_POINT);
    if (data != null)
      handleResponse(data);
  }

  private void handleResponse(final @NonNull Intent data)
  {
    final PickPointResponse response = PickPointResponse.extractFromIntent(data);
    if (response == null)
      return;
    final Point point = response.getPoint();
    mCity = City.fromPoint(point);

    if (mCity != null)
    {
      mName.setText(mCity.getName());
      mAltNames.setText(mCity.getAltNames());
      mCountry.setText(mCity.getCountryCode());

      mLat.setText(String.valueOf(mCity.getLat()));
      mLon.setText(String.valueOf(mCity.getLon()));
      final String level = mCity.getElevation() != -9999 ? String.valueOf(mCity.getElevation()) : "No Data";
      mElev.setText(level);

      final String population = mCity.getPopulation() != -1 ? String.valueOf(mCity.getPopulation()) : "No Data";
      mPopulation.setText(population);
      mTimeZone.setText(mCity.getTimeZone());
    }
  }
}
