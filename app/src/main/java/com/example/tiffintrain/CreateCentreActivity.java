package com.example.tiffintrain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CreateCentreActivity extends AppCompatActivity {

    String default_email;
    EditText centreName, address, pinCode, contactNo;
    public static int REQUEST_CODE = 100;
    FusedLocationProviderClient fusedLocationProviderClient;
    double lati, longi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_centre);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Intent intent = getIntent();
        default_email = intent.getStringExtra("key_email");

        EditText emailEditText = findViewById(R.id.email_field);
        emailEditText.setText(default_email);

        ImageView getLocationButton = findViewById(R.id.get_location_button);
        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CreateCentreActivity.this, "Current Location Detected", Toast.LENGTH_SHORT).show();
//                Intent intent  = new Intent(CreateCentreActivity.this , GetLocationActivity.class) ;
                getLastLocation();
//                startActivity(intent);
            }
        });
    }

    public void addTiffinCentre(View view) {
        centreName = findViewById(R.id.centre_name_field);
        address = findViewById(R.id.address_field);
        pinCode = findViewById(R.id.pincode_field);
        contactNo = findViewById(R.id.contact_field);
        TiffinCentre centre = new TiffinCentre(centreName.getText().toString(), default_email, address.getText().toString(), Integer.parseInt(pinCode.getText().toString()), Integer.parseInt(contactNo.getText().toString()), lati, longi);
        FirebaseFirestore.getInstance().collection("Tiffin Centres").document(default_email).set(centre).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(CreateCentreActivity.this, "Tiffin Centre added Successfully", Toast.LENGTH_SHORT).show();
//                    signOut();
                    startActivity(new Intent(CreateCentreActivity.this, StartActivity.class));
                }
            }
        });
    }


    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(CreateCentreActivity.this, Locale.getDefault());

                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                            lati = addresses.get(0).getLatitude();
                            longi = addresses.get(0).getLongitude();

                            TextView add = findViewById(R.id.address_field);
                            add.setText(addresses.get(0).getAddressLine(0));


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            askPermission();
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(CreateCentreActivity.this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }
}