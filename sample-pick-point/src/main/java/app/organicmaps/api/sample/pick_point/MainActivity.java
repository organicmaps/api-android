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

package app.organicmaps.api.sample.pick_point;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import app.organicmaps.api.DownloadDialog;
import app.organicmaps.api.MapRequest;
import app.organicmaps.api.PickPointResponse;
import app.organicmaps.api.Point;

public class MainActivity extends AppCompatActivity
{
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final ActivityResultLauncher<Intent> pickPoint = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> onPointSelected(result.getResultCode(), result.getData()));

    findViewById(R.id.pick_point).setOnClickListener(v -> {
      final Intent request = new MapRequest()
          .setTitle(getString(R.string.app_name))
          .setPickPointMode(true)
          .toIntent();

      if (getApplicationContext().getPackageManager().resolveActivity(request, 0) == null)
      {
        new DownloadDialog(this).show();
        return;
      }

      pickPoint.launch(request);
    });
  }

  protected void onPointSelected(int resultCode, Intent data)
  {
    if (resultCode == RESULT_CANCELED)
    {
      Toast.makeText(this, getString(R.string.cancelled), Toast.LENGTH_LONG).show();
      return;
    }
    else if (resultCode != RESULT_OK)
    {
      throw new AssertionError("Unsupported resultCode: " + resultCode);
    }

    final PickPointResponse response = PickPointResponse.extractFromIntent(data);
    final Point point = response.getPoint();

    final String message = getString(R.string.result, point.getLat(), point.getLon(), point.getId(), point.getName(), response.getZoomLevel());
    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
  }
}