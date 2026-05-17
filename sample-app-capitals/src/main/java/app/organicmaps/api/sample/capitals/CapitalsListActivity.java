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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import app.organicmaps.api.Const;
import app.organicmaps.api.Point;
import app.organicmaps.api.MapRequest;

import java.util.ArrayList;

public class CapitalsListActivity extends AppCompatActivity
{
  private CityAdapter mCityAdapter;

  private final ActivityResultLauncher<Intent> mPickCity = registerForActivityResult(
      new ActivityResultContracts.StartActivityForResult(),
      result -> {
        if (result.getResultCode() != RESULT_OK || result.getData() == null)
          return;
        final Intent intent = new Intent(this, CityDetailsActivity.class);
        intent.putExtra(CityDetailsActivity.EXTRA_POINT, result.getData());
        startActivity(intent);
      });

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.capitals_list_activity);

    ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
      final Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
      v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
      return WindowInsetsCompat.CONSUMED;
    });

    mCityAdapter = new CityAdapter(this, City.CAPITALS);
    final ListView list = findViewById(android.R.id.list);
    list.setAdapter(mCityAdapter);
    list.setOnItemClickListener((parent, view, position, id) -> showCityOnOMMap(mCityAdapter.getItem(position)));
    list.setOnItemLongClickListener((parent, view, position, id) -> {
      final Point p = mCityAdapter.getItem(position).toPoint();
      final Intent omResult = new Intent()
          .putExtra(Const.EXTRA_POINT_ID, p.getId())
          .putExtra(Const.EXTRA_POINT_NAME, p.getName())
          .putExtra(Const.EXTRA_POINT_LAT, p.getLat())
          .putExtra(Const.EXTRA_POINT_LON, p.getLon());
      final Intent intent = new Intent(this, CityDetailsActivity.class);
      intent.putExtra(CityDetailsActivity.EXTRA_POINT, omResult);
      startActivity(intent);
      return true;
    });

    findViewById(R.id.btn_all).setOnClickListener(v -> showCityOnOMMap(City.CAPITALS));
  }

  private void showCityOnOMMap(City ... cities)
  {
    final ArrayList<Point> points = new ArrayList<>(cities.length);
    for (City city : cities)
      points.add(city.toPoint());

    final String title = cities.length == 1 ? cities[0].getName() : "Capitals of the World";
    final Intent intent = new MapRequest()
        .setPoints(points)
        .setAppName(title)
        .setPickPointMode(true)
        .toIntent();
    mPickCity.launch(intent);
  }

  private static class CityAdapter extends ArrayAdapter<City>
  {
    private final City[] data;

    public CityAdapter(Context context, City[] cities)
    {
      super(context, android.R.layout.simple_list_item_2, android.R.id.text1, cities);
      data = cities;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
      final View view = super.getView(position, convertView, parent);
      final TextView subText = view.findViewById(android.R.id.text2);
      final City city = data[position];
      subText.setText(String.format("%s/%s", city.getCountryCode(), city.getTimeZone()));
      return view;
    }
  }
}
