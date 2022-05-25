package com.example.tiffintrain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity {

    private EditText new_email ;
    private EditText new_password ;
    private EditText confirm_password ;
    private EditText name ;
    private EditText age ;
    private Button registerCustomer ;
    private Button registerOwner ;
    private FirebaseAuth auth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        age = findViewById(R.id.age_field) ;
        new_email = findViewById(R.id.new_email_field) ;
        name = findViewById(R.id.name_field) ;
        new_password = findViewById(R.id.new_password_field) ;
        confirm_password = findViewById(R.id.confirm_password_field) ;
        registerCustomer = findViewById(R.id.register_customer_button);
        registerOwner = findViewById(R.id.register_owner_button) ;
        auth = FirebaseAuth.getInstance() ;

        registerCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_txt = new_email.getText().toString() ;
                String password_txt = new_password.getText().toString() ;
                if(TextUtils.isEmpty(email_txt) || TextUtils.isEmpty(password_txt))
                {
                    Toast.makeText(view.getContext() , "Empty Credentials" , Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(confirmPassword(new_password.getText().toString() , confirm_password.getText().toString())){
                        registerUser(email_txt , password_txt , name.getText().toString() , Integer.parseInt(age.getText().toString()) , 1) ;
                    }
                    else{
                        Toast.makeText(view.getContext() , "Passwords don't match ." , Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        registerOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_txt = new_email.getText().toString() ;
                String password_txt = new_password.getText().toString() ;
                if(TextUtils.isEmpty(email_txt) || TextUtils.isEmpty(password_txt))
                {
                    Toast.makeText(view.getContext() , "Empty Credentials" , Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(confirmPassword(new_password.getText().toString() , confirm_password.getText().toString())){
//                        registerUser(email_txt , password_txt , name.getText().toString() , Integer.parseInt(age.getText().toString()) , 2) ;
                        auth.createUserWithEmailAndPassword(email_txt , password_txt).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(SignUpActivity.this , "Registered successfully" , Toast.LENGTH_SHORT).show() ;
                                    User user = new User(name.getText().toString() ,Integer.parseInt(age.getText().toString())  , email_txt , 2) ;

                                    FirebaseFirestore.getInstance().collection("Users").document(email_txt).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            FirebaseAuth.getInstance().signOut();
                                            Intent intent = new Intent(SignUpActivity.this , CreateCentreActivity.class);
                                            intent.putExtra("key_email" , email_txt) ;
                                            startActivity(intent);
                                        }
                                    });
//                    FirebaseAuth.getInstance().signOut();
//                    startActivity(new Intent(SignUpActivity.this , StartActivity.class));
                                }
                                else{
                                    Toast.makeText(SignUpActivity.this , "Registration Failed . Try again" , Toast.LENGTH_SHORT).show() ;
                                }
                            }
                        }) ;
                    }
                    else{
                        Toast.makeText(view.getContext() , "Passwords don't match ." , Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private boolean confirmPassword(String password1 , String password2){
        if(password1.compareTo(password2)==0){
            return  true ;
        }
        return false ;
    }

    private void registerUser(String email, String password , String name , int age , int isOwner) {
        auth.createUserWithEmailAndPassword(email , password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SignUpActivity.this , "Registered successfully" , Toast.LENGTH_SHORT).show() ;
                    User user = new User(name , age , email , isOwner) ;

                    FirebaseFirestore.getInstance().collection("Users").document(email).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(SignUpActivity.this , StartActivity.class));
                        }
                    });
//                    FirebaseAuth.getInstance().signOut();
//                    startActivity(new Intent(SignUpActivity.this , StartActivity.class));
                }
                else{
                    Toast.makeText(SignUpActivity.this , "Registration Failed . Try again" , Toast.LENGTH_SHORT).show() ;
                }
            }
        }) ;
    }

}


