package com.example.tiffintrain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class ViewAndEditMenuActivity extends AppCompatActivity {

    private LinearLayout centreManagementHomeButton ;
    private LinearLayout centreManagementMenuButton ;
    private LinearLayout menuListLayout ;
    private String currentUserEmail ;
    private ArrayList<String> menuUIds ;
    private TiffinCentre currentTiffinCentre ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_and_edit_menu);

        Intent intent = getIntent();
        currentUserEmail = intent.getStringExtra("key_current_user_email");

        menuListLayout = findViewById(R.id.menu_list_layout) ;

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Tiffin Centres").document(currentUserEmail);
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


                                    if(currentMenu!=null) {
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

                                        menuListLayout.addView(listItemView);
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });






        centreManagementMenuButton =  findViewById(R.id.centre_management_menu_button);
        centreManagementMenuButton.setBackgroundColor(Color.parseColor("#F57C00"));

        centreManagementHomeButton = findViewById(R.id.centre_management_home_button);
        centreManagementHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ViewAndEditMenuActivity.this , CentreManagementActivity.class));
            }
        });
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_menu_option , menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int item_id = item.getItemId() ;

        if(item_id == R.id.create_menu_button){
            Intent intent = new Intent(ViewAndEditMenuActivity.this , CreateMenuActivity.class);
            intent.putExtra("key_current_user_email" , currentUserEmail) ;
            startActivity(intent);
        }
        return true;
    }
}