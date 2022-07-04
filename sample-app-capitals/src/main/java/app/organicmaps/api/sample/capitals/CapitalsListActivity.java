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

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import app.organicmaps.api.Point;
import app.organicmaps.api.MapRequest;

import java.util.ArrayList;

public class CapitalsListActivity extends ListActivity
{
  private static final int REQ_CODE_CITY = 1;

  CityAdapter mCityAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.capitals_list_activity);

    mCityAdapter = new CityAdapter(this, City.CAPITALS);
    setListAdapter(mCityAdapter);

    findViewById(R.id.btn_all).setOnClickListener(v -> showCityOnOMMap(City.CAPITALS));
  }


  @Override
  protected void onListItemClick(ListView l, View v, int position, long id)
  {
    showCityOnOMMap(mCityAdapter.getItem(position));
  }

  private void showCityOnOMMap(City ... cities)
  {
    final ArrayList<Point> points = new ArrayList<>(cities.length);
    for (City city : cities)
      points.add(city.toPoint());

    final String title = cities.length == 1 ? cities[0].getName() : "Capitals of the World";
    final Intent intent = new MapRequest()
        .setPoints(points)
        .setTitle(title)
        .toIntent();
    this.startActivityForResult(intent, REQ_CODE_CITY);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode != REQ_CODE_CITY || resultCode != RESULT_OK)
      return;

    final Intent intent = new Intent(this, CityDetailsActivity.class);
    intent.putExtra(CityDetailsActivity.EXTRA_POINT, data);
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
