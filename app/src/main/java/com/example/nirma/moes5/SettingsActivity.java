package com.example.nirma.moes5;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

/*
    this class, handled user setting
 */
public class SettingsActivity extends AppCompatActivity
{
    //UI
    private CircleImageView profileImage;
    private TextView userName;
    private TextView userStatus;
    private Button changeProfileImage;
    private Button changeUserStatus;
    private ProgressDialog loadingBar;

    //Firebase DB
    private StorageReference storeProfileImageStorageRef;
    private DatabaseReference getUserDataReference;
    private FirebaseAuth mAuth;
    Bitmap thumb_bitmap = null;
    private StorageReference thumbImageRef;

    public static SettingsActivity settingsActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //create layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        settingsActivity=this;

        //Set firebase
        mAuth = FirebaseAuth.getInstance();
        //get current user id
        String online_user_id = mAuth.getCurrentUser().getUid();
        //get data
        getUserDataReference = FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_id);
        getUserDataReference.keepSynced(true);

        //get profile image
        storeProfileImageStorageRef = FirebaseStorage.getInstance().getReference().child("profile_image");

        thumbImageRef = FirebaseStorage.getInstance().getReference().child("Thumb_Images");

        //Get UI
        profileImage = findViewById(R.id.profile_image);
        userName = findViewById(R.id.username);
        userStatus = findViewById(R.id.userstatus);
        changeProfileImage = findViewById(R.id.change_image_button);
        changeUserStatus = findViewById(R.id.change_status_button);
        loadingBar = new ProgressDialog(this);

        getUserDataReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                //get data user from firebase
                String name = dataSnapshot.child("user_name").getValue().toString();
                String status = dataSnapshot.child("user_status").getValue().toString();
                final String image = dataSnapshot.child("user_image").getValue().toString();
                String thumb_image = dataSnapshot.child("user_thumb_image").getValue().toString();

                userName.setText(name); //ubah nama user
                userStatus.setText(status); //ubah status user

                //if not default profile image
                if(!image.equals("default_profile"))
                {
                    //load image offline
                    Picasso.get()
                            .load(image)
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_profile)
                            .into(profileImage, new Callback() {
                                @Override
                                public void onSuccess()
                                {

                                }

                                @Override
                                public void onError(Exception e)
                                {
                                    Picasso.get()
                                            .load(image)
                                            .placeholder(R.drawable.default_profile)
                                            .into(profileImage);
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        //when click change image
        changeProfileImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //crop circle image
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);
            }
        });

        //when click change status
        changeUserStatus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //get old staust
                String old_status = userStatus.getText().toString();
                //change activity to status
                Intent statusIntent = new Intent(SettingsActivity.this,StatusActivity.class);
                statusIntent.putExtra("user_status", old_status);
                startActivity(statusIntent);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                //Set UI
                loadingBar.setTitle("Mengubah gambar");
                loadingBar.setMessage("Mohon tunggu, sedang dalam proses");
                loadingBar.show();

                Uri resultUri = result.getUri();

                File thumb_filePathUri = new File(resultUri.getPath());

                try
                {
                    //compress image
                    thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(50)
                            .compressToBitmap(thumb_filePathUri);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                ByteArrayOutputStream byteArrayOutputStreamStream = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStreamStream);
                final byte[] thumb_byte = byteArrayOutputStreamStream.toByteArray();

                //get user id
                String user_id = mAuth.getCurrentUser().getUid();
                //set to firebase
                StorageReference filePath = storeProfileImageStorageRef.child(user_id + ".jpg");
                final StorageReference thumb_filePath = thumbImageRef.child(user_id + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(SettingsActivity.this, "Menyimpan gambar...", Toast.LENGTH_LONG).show();

                            final String downloadUrl = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumb_filePath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task)
                                {
                                    String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();
                                    if (task.isSuccessful())
                                    {
                                        Map update_user_data = new HashMap();
                                        update_user_data.put("user_image",downloadUrl);
                                        update_user_data.put("user_thumb_image", thumb_downloadUrl);

                                        getUserDataReference.updateChildren(update_user_data).addOnCompleteListener(new OnCompleteListener<Void>()
                                        {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                Toast.makeText(SettingsActivity.this, "Gambar berhasil diubah", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(SettingsActivity.this, "Maaf, ada kesalahan saat mengubah gambar", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
            }
        }
    }
}
