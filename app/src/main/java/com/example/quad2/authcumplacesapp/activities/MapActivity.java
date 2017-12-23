package com.example.quad2.authcumplacesapp.activities;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.example.quad2.authcumplacesapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener {

  private static final int REQUEST_LOCATION = 10;
  protected GoogleApiClient mGoogleApiClient;
  protected Location mCurrentLocation;
  protected Location mStartLocation;
  protected Location mStopLoction;
  protected LocationRequest mLocationRequest;
  private GoogleMap mMap;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map);
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
    buildGoogleApiClient();
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (mGoogleApiClient.isConnected()) {
      stopLocationUpdates();
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
    mGoogleApiClient.connect();
  }

  @Override
  protected void onStop() {
    mGoogleApiClient.disconnect();
    super.onStop();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions,
      int[] grantResults) {
    switch (requestCode) {
      case REQUEST_LOCATION: {
        if (grantResults != null && grantResults.length > 0) {
          if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
            mMap.setMyLocationEnabled(true);
          } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
              requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                  REQUEST_LOCATION);
            }
          }
        }
      }
    }
  }

  protected synchronized void buildGoogleApiClient() {
    Log.i("log1", "Building GoogleApiClient");
    mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(LocationServices.API)
        .build();

    createLocationRequest();
  }

  protected void createLocationRequest() {
    mLocationRequest = new LocationRequest();
    mLocationRequest.setInterval(10000);
    mLocationRequest.setFastestInterval(10000);
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
        .addLocationRequest(mLocationRequest);
    builder.setAlwaysShow(true); //this is the key ingredient
    PendingResult<LocationSettingsResult> result =
        LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
    locationEnabler(result);
  }

  private void locationEnabler(PendingResult<LocationSettingsResult> result) {
    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
      @Override
      public void onResult(LocationSettingsResult result) {
        final Status status = result.getStatus();
        switch (status.getStatusCode()) {
          case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
            try {
              // Show the dialog by calling startResolutionForResult(),
              // and check the result in onActivityResult().
              status.startResolutionForResult(MapActivity.this, REQUEST_LOCATION);
              buildGoogleApiClient();
            } catch (IntentSender.SendIntentException e) {
              // Ignore the error.
            }
            break;
        }
      }
    });
  }

  protected void startLocationUpdates() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
          100);
    } else {
      LocationServices.FusedLocationApi.requestLocationUpdates(
          mGoogleApiClient, mLocationRequest, this);
    }
  }

  protected void stopLocationUpdates() {
    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
  }

  @Override
  public void onConnected(@Nullable Bundle bundle) {
    if (mCurrentLocation == null) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
          && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
          != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
            100);
      } else {
        if (LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient) == null) {
          startLocationUpdates();
          mMap.setMyLocationEnabled(true);
        } else {
          mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
          if (mCurrentLocation != null) {
            mMap.setMyLocationEnabled(true);
            Log.d("saveTest", "***" + mCurrentLocation);

          }
        }
      }
    }
    startLocationUpdates();

  }

  @Override
  public void onConnectionSuspended(int i) {
    mGoogleApiClient.connect();
  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    Log.i("logConnecFail",
        "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
  }

  @Override
  public void onLocationChanged(Location location) {
    mCurrentLocation = location;
    if (mCurrentLocation != null) {
      Log.d("locChanged", "***" + mCurrentLocation);
    }
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    if (mCurrentLocation != null) {
      LatLng test = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
      mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(test, 13));
      mMap.addMarker(new MarkerOptions()
          .title("Bangalore")
          .position(test));
    }

  }
}
