package com.example.tiffintrain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DisplayTheCentreActivity extends AppCompatActivity {

    TiffinCentre currentCentre ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_the_centre);

        currentCentre = (TiffinCentre) getIntent().getSerializableExtra("centre") ;
        TextView contact = findViewById(R.id.contact_no_id);
        TextView address = findViewById(R.id.address_id);
        TextView centre = findViewById(R.id.tiffin_centre_name);

        contact.setText(""+currentCentre.getContactNo());
        address.setText(currentCentre.getAddress());
        centre.setText(currentCentre.getName());

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+currentCentre.getContactNo()));
                startActivity(intent);
            }
        });
    }

    public void showMap(View view){
        double latitude = currentCentre.getCentre_latitude();
        double longitude  = currentCentre.getCentre_longitude() ;
        String strUri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude + " (" + "Label which you want" + ")";
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }
}