package com.invincix.traahi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import static com.invincix.traahi.MainActivity.STORE_DATA;

public class ProfileActivity extends AppCompatActivity {

    private TextView profileToolbarText;
    private ImageView profPic;
    private Button changeProfPic, updateProf, logout;
    private DatabaseReference profileDatabase,volMainDatabase,userDB;
    private EditText firstNameEdit, lastNameEdit, phoneNumber;
    private String ownNumber, firstName, lastName, picUrl, userChoosenTask,first_name,last_name;
    private ProgressDialog uploadProgress;
    private StorageReference mStorage;
    final Context context = this;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Setting the Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.profiletoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        assert toolbar != null;
        toolbar.bringToFront();

        //set toolbar text
        profileToolbarText = (TextView) findViewById(R.id.profiletoolbartext) ;
        Typeface custom = Typeface.createFromAsset(getAssets(), "fonts/toolbarfont.ttf");
        profileToolbarText.setTypeface(custom);

        uploadProgress = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();



        SharedPreferences sharedPrefContact = getSharedPreferences(LoginActivity.STORE_DATA_NAME, Context.MODE_PRIVATE);
        ownNumber = sharedPrefContact.getString("LOCAL_OWN_NUMBER", null);
        profileDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(ownNumber).child("Profile");
        volMainDatabase = FirebaseDatabase.getInstance().getReference().child("Volunteers");
        userDB = FirebaseDatabase.getInstance().getReference().child("Users").child(ownNumber);
        mStorage = FirebaseStorage.getInstance().getReference().child("Profile Pictures").child(ownNumber);


        profPic = (ImageView) findViewById(R.id.profPic);
        firstNameEdit = (EditText)findViewById(R.id.firstNameEdit);
        lastNameEdit = (EditText)findViewById(R.id.lastNameEdit);
        phoneNumber = (EditText)findViewById(R.id.phoneNumberEdit);
        changeProfPic = (Button)findViewById(R.id.changeProfPic);
        updateProf = (Button) findViewById(R.id.updateButton);
        logout = (Button) findViewById(R.id.logoutButton);


        phoneNumber.setText(ownNumber);
        phoneNumber.setEnabled(false);

        profileDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap value = (HashMap) dataSnapshot.getValue();
                if(value!=null){
                    firstName = (String) dataSnapshot.child("firstName").getValue();
                    lastName = (String) dataSnapshot.child("lastName").getValue();
                    picUrl = (String) dataSnapshot.child("picUrl").getValue();

                    if(picUrl!=null) {
                        Picasso.with(getApplicationContext()).load(picUrl).into(profPic);
                    }

                    firstNameEdit.setText(firstName);
                    lastNameEdit.setText(lastName);



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        changeProfPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialogforProfChange();
            }
        });

        updateProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validationOfProfile()){
                    profileDatabase.child("firstName").setValue(first_name);
                    profileDatabase.child("lastName").setValue(last_name);
                    Toast.makeText(getApplicationContext(),"Update Success",Toast.LENGTH_SHORT).show();

                }

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(context);
                }
                builder.setTitle("Logout User")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences sharedPref = getSharedPreferences(MainActivity.STORE_DATA, Context.MODE_PRIVATE);
                                SharedPreferences.Editor edit = sharedPref.edit();
                                edit.clear();
                                edit.apply();
                                mAuth.signOut();

                                Intent loginIntent = new Intent(ProfileActivity.this,LoginActivity.class);
                                startActivity(loginIntent);
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });





    }
    private void createDialogforProfChange() {
        final CharSequence[] items = { "Take Photo", "Choose from Gallery",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    userChoosenTask="Take Photo";
                    cameraIntent();
                } else if (items[item].equals("Choose from Gallery")) {
                    userChoosenTask="Choose from Gallery";
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"),0);
    }
    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if ( requestCode == 0 && resultCode == Activity.RESULT_OK) {
                uploadProgress.setMessage("Uploading File...");
                uploadProgress.show();
                Uri uri = data.getData();
                mStorage.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(),"Upload Success",Toast.LENGTH_LONG).show();
                        uploadProgress.dismiss();
                        String url = taskSnapshot.getDownloadUrl().toString();
                        volMainDatabase.child(ownNumber).child("Profile").child("Profile").child("picUrl").setValue(url);
                        userDB.child("Profile").child("picUrl").setValue(url);


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Upload Failed, Please Try Again",Toast.LENGTH_LONG).show();
                        uploadProgress.dismiss();
                    }
                });
            }
            if(requestCode == 1 && resultCode == Activity.RESULT_OK){
                uploadProgress.setMessage("Uploading File...");
                uploadProgress.show();
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpeg");
                Uri thumbNail = Uri.fromFile(destination);
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mStorage.putFile(thumbNail).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(),"Upload Success",Toast.LENGTH_LONG).show();
                        uploadProgress.dismiss();
                        String url = taskSnapshot.getDownloadUrl().toString();
                        volMainDatabase.child(ownNumber).child("Profile").child("Profile").child("picUrl").setValue(url);
                        userDB.child("Profile").child("picUrl").setValue(url);


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Upload Failed, Please Try Again",Toast.LENGTH_LONG).show();
                        uploadProgress.dismiss();
                    }
                });

            }
        }


    private boolean validationOfProfile() {
        boolean valid = true;
         first_name = firstNameEdit.getText().toString();
         last_name = lastNameEdit.getText().toString();
        if (TextUtils.isEmpty(first_name)) {
            firstNameEdit.setError("Required");
            valid = false;
        } else {
           firstNameEdit.setError(null);
        }
        if (TextUtils.isEmpty(last_name)) {
            lastNameEdit.setError("Required");
            valid = false;
        } else {
            lastNameEdit.setError(null);
        }

        return valid;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
