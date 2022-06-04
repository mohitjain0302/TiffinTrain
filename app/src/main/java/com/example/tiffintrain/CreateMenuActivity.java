package com.example.tiffintrain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CreateMenuActivity extends AppCompatActivity {

    private String currentUserEmail;
    private String menuUId;

    private TiffinCentre currentTiffinCentre;
    private ArrayList<String> menuUIds;
    private EditText menuNameField;
    private EditText menuRateField;

    private Button addItemButton;
    private Button submitMenuButton;

    private LinearLayout addItemsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_menu);

        Intent intent = getIntent();
        currentUserEmail = intent.getStringExtra("key_current_user_email");

        menuUIds = new ArrayList<>() ;
        menuNameField = findViewById(R.id.menu_name);
        menuRateField = findViewById(R.id.menu_rate);

        addItemsLayout = findViewById(R.id.add_items_layout);
        addItemButton = findViewById(R.id.add_item_button);
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editTextView = new EditText(CreateMenuActivity.this);
                editTextView.setHint("Item_name");
                addItemsLayout.addView(editTextView);
            }
        });

        FirebaseFirestore.getInstance().collection("Tiffin Centres").document(currentUserEmail).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentTiffinCentre = documentSnapshot.toObject(TiffinCentre.class);
            }
        });

        submitMenuButton = findViewById(R.id.submit_menu_button);
        submitMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitMenu();
            }
        });
    }

    private void submitMenu() {
        String menuName = menuNameField.getText().toString();
        int menuRate = Integer.parseInt(menuRateField.getText().toString());
        ArrayList<String> menuItems = new ArrayList<>();
        int noOfItems = addItemsLayout.getChildCount();
        for (int i = 0; i < noOfItems; i++) {
            EditText currentEditTextField = (EditText) addItemsLayout.getChildAt(i);
            menuItems.add(currentEditTextField.getText().toString());
        }
        Menu menu = new Menu(menuName, menuRate, menuItems);
        FirebaseFirestore.getInstance().collection("Menus").add(menu).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                menuUId = documentReference.getId();

                if(currentTiffinCentre.getMenuUIds() == null) {
                    menuUIds.add(menuUId);
                    FirebaseFirestore.getInstance().collection("Tiffin Centres").document(currentUserEmail).update("menuUIds" , menuUIds);
                    Toast.makeText(CreateMenuActivity.this , "Menu added Successfully" , Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateMenuActivity.this , ViewAndEditMenuActivity.class);
                    intent.putExtra("key_current_user_email" , currentUserEmail) ;
                    startActivity(intent);
                } else {
                    menuUIds = currentTiffinCentre.getMenuUIds() ;
                    menuUIds.add(menuUId);
                    FirebaseFirestore.getInstance().collection("Tiffin Centres").document(currentUserEmail).update("menuUIds" , menuUIds);
                    Toast.makeText(CreateMenuActivity.this , "Menu added Successfully" , Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(CreateMenuActivity.this , ViewAndEditMenuActivity.class);
                    intent.putExtra("key_current_user_email" , currentUserEmail) ;
                    startActivity(intent);
                }

            }
        });
    }

}
