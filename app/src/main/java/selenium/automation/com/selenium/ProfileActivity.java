package selenium.automation.com.selenium;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity  {

    private static final int CHOOSE_IMAGE = 101 ;
    private EditText etDisplayName;
    private ImageView ivPhoto;
    private Button btSave,btSignOut;
    private Uri urlProfileImage;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private String profileImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etDisplayName = (EditText)findViewById(R.id.etDisplayName);
        ivPhoto     =   (ImageView)findViewById(R.id.imageViewPhoto);

        btSave  = (Button)findViewById(R.id.btSave);
        btSignOut = (Button) findViewById(R.id.btSignOut);

        progressBar = (ProgressBar)findViewById(R.id.progressBar2);
        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChooser();
            }
        });

        loadUserInformation();

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                progressDialog.show();
//                progressDialog.setMessage("Saving Profile Pic....");
                progressBar.setVisibility(View.VISIBLE);
                saveUserInformation();
            }
        });


        btSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()==null){
            finish();
            Intent intent = new Intent(this,MainActivity.class);
        }
    }

    private void loadUserInformation() {

        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null) {
            if(user.getDisplayName()!=null) {
                String Name = user.getDisplayName();
                etDisplayName.setText(Name);
            }
            if(user.getPhotoUrl()!=null) {
                String PhotoUrl = user.getPhotoUrl().toString();
                Glide.with(this).load(PhotoUrl).into(ivPhoto);
            }
        }


    }



    private void signOut() {
        mAuth.signOut();
        startActivity(new Intent(ProfileActivity.this,MainActivity.class));
    }

    private void saveUserInformation() {
        String displayName = etDisplayName.getText().toString().trim();
        if(displayName.isEmpty()){
            etDisplayName.setError("Name is required");
            etDisplayName.requestFocus();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();

        if( user!=null && profileImageUrl !=null){
            UserProfileChangeRequest profile =  new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .setPhotoUri(Uri.parse(profileImageUrl)).build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
//                    progressDialog.cancel();
                    progressBar.setVisibility(View.GONE);
                    if(task.isSuccessful()){
                        Toast.makeText(ProfileActivity.this,"Profile updated",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(ProfileActivity.this,"Could not Upload profile",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_IMAGE && resultCode==RESULT_OK && data != null && data.getData()!=null){
           urlProfileImage =  data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),urlProfileImage);
                ivPhoto.setImageBitmap(bitmap);
                Toast.makeText(ProfileActivity.this, "profile image picked", Toast.LENGTH_SHORT).show();
                uploadImageToFireBaseStorage();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }



    private void uploadImageToFireBaseStorage(){
        StorageReference profileReference = FirebaseStorage.getInstance().getReference("profilePics/"+System.currentTimeMillis()+".jpg");
        if(urlProfileImage!=null){
            profileReference.putFile(urlProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.GONE);
                     profileImageUrl = taskSnapshot.getDownloadUrl().toString();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProfileActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();


                }
            });
        }
    }

    private void showImageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Profile Image"),CHOOSE_IMAGE);
    }
}
