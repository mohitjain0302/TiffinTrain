package com.example.tiffintrain;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class StartActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private static int RC_SIGN_IN = 100;
    private FirebaseAuth auth ;
    SignInButton signInButton;
    FirebaseAuth mAuth;
    int flag = 0;
    EditText loginEmailField ;
    EditText loginPasswordField ;
    Button manualSignInButton ;
    private User currentUser ;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String currentUserEmail = user.getEmail();
            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(currentUserEmail);
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    currentUser = documentSnapshot.toObject(User.class) ;
                    if(currentUser!=null) {
                        if (currentUser.getIsOwner() == 2) {
                            startActivity(new Intent(StartActivity.this, CentreManagementActivity.class));
                        } else {
                            startActivity(new Intent(StartActivity.this, DisplayCentresActivity.class));
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        loginEmailField = findViewById(R.id.login_email_field) ;
        loginPasswordField = findViewById(R.id.login_password_field) ;
        manualSignInButton = findViewById(R.id.manaul_sign_in_button) ;
        auth = FirebaseAuth.getInstance() ;
        manualSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser(loginEmailField.getText().toString() , loginPasswordField.getText().toString()) ;
            }
        });


        signInButton = findViewById(R.id.sign_in_button);
        TextView buttonTextView = (TextView) signInButton.getChildAt(0);
        buttonTextView.setText("Sign In with Google");
        mAuth = FirebaseAuth.getInstance();
        createRequest();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    private void createRequest() {
//      Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            String email = user.getEmail();
                            String name = user.getDisplayName();

                            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(email);
                            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (!document.exists()) {
                                            User newUser = new User(name, email, 0);
                                            FirebaseFirestore.getInstance().collection("Users").document(email).set(newUser);
                                            startActivity(new Intent(getApplicationContext(), OwnerOptionActivity.class));
                                        } else {
                                            User user1 = document.toObject(User.class);
                                            if (user1.getIsOwner() == 1) {
                                                startActivity(new Intent(getApplicationContext(), DisplayCentresActivity.class));
                                            } else if (user1.getIsOwner() == 2) {
                                                startActivity(new Intent(getApplicationContext(), CentreManagementActivity.class));
                                            }
                                        }

                                    }
                                }
                            });

                            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                            DocumentReference docIdRef = rootRef.collection("yourCollection").document(email);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(StartActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }

    public void signUp(View view) {
        Intent intent = new Intent(StartActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    private void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email , password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(StartActivity.this , "Login Successful" , Toast.LENGTH_SHORT).show() ;
                FirebaseFirestore.getInstance().collection("Users").document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            User user1 = documentSnapshot.toObject(User.class) ;
                            if(user1.getIsOwner()==1){
                                finish();
                                startActivity(new Intent(StartActivity.this , DisplayCentresActivity.class));

                            }
                            else if(user1.getIsOwner()==2){
                                startActivity(new Intent(StartActivity.this , CentreManagementActivity.class));
//                                finish();
                            }
                        }
                    }
                });
            }
        }) ;
    }
}