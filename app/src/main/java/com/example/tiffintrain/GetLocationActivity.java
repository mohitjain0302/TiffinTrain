package com.example.tiffintrain;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class GetLocationActivity extends AppCompatActivity {

    public static int REQUEST_CODE = 100;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_centre);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        getLastLocation();

    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(GetLocationActivity.this, Locale.getDefault());

                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                            double lati = addresses.get(0).getLatitude();
                            double longi = addresses.get(0).getLongitude();

                            TextView add = findViewById(R.id.address_field);
                            add.setText(addresses.get(0).getAddressLine(0));


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        else{
            askPermission();
        }
    }

    private void askPermission () {
        ActivityCompat.requestPermissions(GetLocationActivity.this,new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
    }

    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE){
            if(grantResults.length >0  && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }
        }
    }

}

      /*  mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(25.3407, 74.6313);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Bhilwara"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney,16f));

        mMap.addCircle(new CircleOptions().center(sydney).radius(100).fillColor(Color.GRAY).strokeColor(Color.BLACK));


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {

                mMap.addMarker(new MarkerOptions().position(latLng).title("user's location"));

                Geocoder geocoder = new Geocoder(MapsActivity.this);

                try{
                   ArrayList<Address> arradd =  (ArrayList<Address>) geocoder.getFromLocation(latLng.latitude, latLng.longitude,1);

                    Log.d("Location on map : ",arradd.get(0).getAddressLine(0));
                }
                catch (IOException e)
                {
                   e.printStackTrace();
                }


            }
        });

    */

