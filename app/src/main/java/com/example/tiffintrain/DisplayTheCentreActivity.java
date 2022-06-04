package com.example.tiffintrain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DisplayTheCentreActivity extends AppCompatActivity {

    TiffinCentre currentCentre;
    private LinearLayout centreMenusListLayout;
    private String tiffinCentreUId;
    private ArrayList<String> menuUIds;
    private TiffinCentre currentTiffinCentre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_the_centre);

        currentCentre = (TiffinCentre) getIntent().getSerializableExtra("centre");
        TextView contact = findViewById(R.id.contact_no_id);
        TextView address = findViewById(R.id.address_id);
        TextView centre = findViewById(R.id.tiffin_centre_name);
        ImageView tiffin_centre_image = findViewById(R.id.tiffin_centre_image);

        centreMenusListLayout = findViewById(R.id.centre_menus_list_layout);

        contact.setText("" + currentCentre.getContactNo());
        address.setText(currentCentre.getAddress());
        centre.setText(currentCentre.getName());
        tiffinCentreUId = currentCentre.getEmail();

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Tiffin Centres").document(tiffinCentreUId);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentTiffinCentre = documentSnapshot.toObject(TiffinCentre.class);

                if(currentTiffinCentre.getMenuUIds() != null) {
                    if (currentTiffinCentre.getMenuUIds().isEmpty()) {
                        Log.d("Uid", "Going In If");
                    } else {
                        Log.d("Uid", "Going In Else");
                        ArrayList<com.example.tiffintrain.Menu> menus = new ArrayList<>();

                        menus.clear();

                        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Menus");
                        Log.d("Uid", "Till here");
                        menuUIds = currentTiffinCentre.getMenuUIds();

                        for (int i = 0; i < menuUIds.size(); i++) {
                            String currentMenuUId = menuUIds.get(i);
                            collectionReference.document(currentMenuUId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    com.example.tiffintrain.Menu currentMenu = documentSnapshot.toObject(com.example.tiffintrain.Menu.class);
                                    LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View listItemView = vi.inflate(R.layout.menu_list_item, null);

                                    TextView menuName = (TextView) listItemView.findViewById(R.id.menu_name_text_view);
                                    menuName.setText(currentMenu.getMenuName());

                                    TextView menuRate = (TextView) listItemView.findViewById(R.id.menu_rate_text_view);
                                    menuRate.setText("Rs " + Integer.toString(currentMenu.getMenuRate()) + "/-");

                                    ArrayList<String> menuItems = currentMenu.getMenuItems();
                                    String str = "";
                                    for (int i = 0; i < menuItems.size(); i++) {
                                        str += menuItems.get(i);
                                        str += ",";
                                    }

                                    TextView displayItemsTextView = listItemView.findViewById(R.id.display_items_text_view);
                                    displayItemsTextView.setText(str.substring(0, str.length() - 1));

                                    centreMenusListLayout.addView(listItemView);
                                }
                            });

                            if (currentCentre.getMyTiffinCentreImageUrl() != null) {
                                Picasso.with(DisplayTheCentreActivity.this).load(currentCentre.getMyTiffinCentreImageUrl()).fit().centerCrop().into(tiffin_centre_image);
                            }
                        }
                    }
                }
            }
        });


        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + currentCentre.getContactNo()));
                startActivity(intent);
            }
        });

    }


    public void showMap(View view) {
        double latitude = currentCentre.getCentre_latitude();
        double longitude = currentCentre.getCentre_longitude();
        String strUri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude + " (" + "Label which you want" + ")";
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }
}