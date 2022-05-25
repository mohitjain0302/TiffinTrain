package com.example.tiffintrain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class OwnerOptionActivity extends AppCompatActivity {

    FirebaseAuth mAuth ;
    GoogleSignInAccount signInAccount ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth  = FirebaseAuth.getInstance() ;
        setContentView(R.layout.activity_owner_option);
        signInAccount = GoogleSignIn.getLastSignedInAccount(this) ;
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.radio_customer:
                if (checked){
                    FirebaseFirestore.getInstance().collection("Users").document(signInAccount.getEmail()).update("isOwner" , 1) ;
                    startActivity(new Intent(OwnerOptionActivity.this , DisplayCentresActivity.class));
                    break;
                }

            case R.id.radio_owner:
                if (checked){
                    FirebaseFirestore.getInstance().collection("Users").document(signInAccount.getEmail()).update("isOwner" , 2) ;
                    Intent intent = new Intent(OwnerOptionActivity.this , CreateCentreActivity.class);
                    intent.putExtra("key_email" , mAuth.getCurrentUser().getEmail()) ;
                    startActivity(intent);

                    break;
                }

        }
    }
}