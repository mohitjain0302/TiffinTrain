package com.example.tiffintrain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DisplayCentresActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_centres);

        ArrayList<TiffinCentre> centres = new ArrayList<>();

        CentreAdapter adapter = new CentreAdapter(this,centres);
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);



        centres.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("Tiffin Centres") ;
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                centres.clear();
                for(QueryDocumentSnapshot snapshot1 : value){
                    TiffinCentre centre = snapshot1.toObject(TiffinCentre.class) ;
                    centres.add(centre) ;
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                setContentView(R.layout.activity_display_the_centre);
                TiffinCentre currentCentre = centres.get(position) ;
                Intent intent = new Intent(DisplayCentresActivity.this , DisplayTheCentreActivity.class) ;
                intent.putExtra("centre" , currentCentre) ;
                startActivity(intent);
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
                        startActivity(new Intent(DisplayCentresActivity.this , StartActivity.class) );
                    }
                }
            });
        }
        return true;
    }
}