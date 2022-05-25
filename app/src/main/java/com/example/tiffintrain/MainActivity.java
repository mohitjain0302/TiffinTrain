package com.example.tiffintrain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

//    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView googleName = findViewById(R.id.google_name) ;
        TextView googleMail = findViewById(R.id.google_email) ;
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this) ;
        if(signInAccount!=null){
            googleName.setText(signInAccount.getDisplayName()) ;
            googleMail.setText(signInAccount.getEmail()) ;
        }

        Button signOutButton = findViewById(R.id.sign_out_button) ;
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut() ;
                startActivity(new Intent(MainActivity.this , StartActivity.class) );
            }
        });

    }
    private void signOut(){
        GoogleSignInOptions gso = new GoogleSignInOptions.
                Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                build();
        GoogleSignInClient googleSignInClient= GoogleSignIn.getClient(this,gso);
        googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FirebaseAuth.getInstance().signOut(); // very important if you are using firebase.
                    startActivity(new Intent(MainActivity.this , StartActivity.class) );
                }
            }
        });
    }

}