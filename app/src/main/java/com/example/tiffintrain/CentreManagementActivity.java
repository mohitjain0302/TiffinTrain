package com.example.tiffintrain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class CentreManagementActivity extends AppCompatActivity {

    FirebaseAuth mAuth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centre_management);

        mAuth = FirebaseAuth.getInstance() ;
        FirebaseUser user = mAuth.getCurrentUser();

        String email = user.getEmail();
        Log.d("hello", "onCreate: " + email);

        FirebaseFirestore.getInstance().collection("Tiffin Centres").document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if(documentSnapshot.exists()){
                    TiffinCentre currentCentre  = documentSnapshot.toObject(TiffinCentre.class) ;
                    Log.d("hello", "onCreate: " + currentCentre.getName());
                    TextView centreName = findViewById(R.id.tiffin_centre_name) ;
                    centreName.setText(currentCentre.getName());
                    TextView contactNo = findViewById(R.id.contact_no_id) ;
                    contactNo.setText(Integer.toString(currentCentre.getContactNo()));

                    TextView address = findViewById(R.id.address_id) ;
                    address.setText(currentCentre.getAddress());
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu , menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int item_id = item.getItemId() ;

        if(item_id == R.id.logout_button){
            GoogleSignInOptions gso = new GoogleSignInOptions.
                    Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                    build();
            GoogleSignInClient googleSignInClient= GoogleSignIn.getClient(this,gso);
            googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        FirebaseAuth.getInstance().signOut(); // very important if you are using firebase.
                        startActivity(new Intent(CentreManagementActivity.this , StartActivity.class) );
                    }
                }
            });
        }
        return true;
    }
}