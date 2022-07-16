package app.organicmaps.api.sample.pick_point;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import app.organicmaps.api.CrosshairRequest;
import app.organicmaps.api.DownloadDialog;
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
      final Intent request = new CrosshairRequest().setAppName(getString(R.string.app_name))
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