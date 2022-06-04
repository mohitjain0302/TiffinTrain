package com.example.tiffintrain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class CentreManagementActivity extends AppCompatActivity {

    private StorageReference mStorageReference;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;

    private LinearLayout centreManagementHomeButton ;
    private LinearLayout centreManagementMenuButton ;


    private String currentUserEmail ;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centre_management);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        currentUserEmail = user.getEmail();

        centreManagementHomeButton =  findViewById(R.id.centre_management_home_button);
        centreManagementHomeButton.setBackgroundColor(Color.parseColor("#F57C00"));

        centreManagementMenuButton = findViewById(R.id.centre_management_menu_button);
        centreManagementMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CentreManagementActivity.this , ViewAndEditMenuActivity.class);
                intent.putExtra("key_current_user_email" , currentUserEmail) ;
                startActivity(intent);
            }
        });



        FirebaseFirestore.getInstance().collection("Tiffin Centres").document(currentUserEmail).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()) {
                    TiffinCentre currentCentre = documentSnapshot.toObject(TiffinCentre.class);
                    Log.d("hello", "onCreate: " + currentCentre.getName());
                    TextView centreName = findViewById(R.id.tiffin_centre_name);
                    centreName.setText(currentCentre.getName());
                    TextView contactNo = findViewById(R.id.contact_no_id);
                    contactNo.setText(Integer.toString(currentCentre.getContactNo()));

                    TextView address = findViewById(R.id.address_id);
                    address.setText(currentCentre.getAddress());

                    ImageView myTiffinCentreImage = findViewById(R.id.myTiffinCentreImage);
                    if(currentCentre.getMyTiffinCentreImageUrl()!=null){
                        Picasso.with(CentreManagementActivity.this).load(currentCentre.getMyTiffinCentreImageUrl()).fit().centerCrop().into(myTiffinCentreImage);
                    }

                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int item_id = item.getItemId();

        if (item_id == R.id.logout_button) {
            GoogleSignInOptions gso = new GoogleSignInOptions.
                    Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                    build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
            googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        FirebaseAuth.getInstance().signOut(); // very important if you are using firebase.
                        startActivity(new Intent(CentreManagementActivity.this, StartActivity.class));
                    }
                }
            });
        }
        return true;
    }

    public void chooseTiffinCentreImage(View view) {
        openFileChooser();
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            uploadFile();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(){
        if(mImageUri != null){
            mStorageReference = FirebaseStorage.getInstance().getReference("tiffin_centre_images");
            StorageReference fileReference = mStorageReference.child(System.currentTimeMillis()+"."+getFileExtension(mImageUri));
            fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUrl = uri ;
                            FirebaseFirestore.getInstance().collection("Tiffin Centres").document(currentUserEmail).update("myTiffinCentreImageUrl", downloadUrl) ;
                        }
                    });
                    Toast.makeText(CentreManagementActivity.this , "Image Updated Successfully" , Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CentreManagementActivity.this , e.getMessage() , Toast.LENGTH_SHORT).show();
                }
            }) ;
        }
        else
        {
            Toast.makeText(CentreManagementActivity.this,"No Image Selected" , Toast.LENGTH_SHORT).show();
        }
    }
}