package com.invincix.traahi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.onesignal.OneSignal;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class TraahiVolunteer extends AppCompatActivity {

    private TextView volunteerToolbarText, volunteerStatus, volunteerStatusShow;
    private Button volunteerStatusButton, nearbyVolunteers;
    private DatabaseReference volunteerDatabase, volMainDatabase,userDB;
    private String ownNumber, volunteerStat,picData, userChoosenTask;
    private HashMap volunteer;
    private StorageReference mStorage;
    private ProgressDialog uploadProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traahi_volunteer);



        Toolbar toolbar = (Toolbar) findViewById(R.id.volunteertoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //set toolbar text
        volunteerToolbarText = (TextView) findViewById(R.id.volunteertoolbartext);
        Typeface custom = Typeface.createFromAsset(getAssets(), "fonts/toolbarfont.ttf");
        volunteerToolbarText.setTypeface(custom);

        volunteerStatusShow = (TextView) findViewById(R.id.volunteerStatusShow);

        //Retrieve Data
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        volunteerStat = extras.getString("volunteerStatus");
        volunteerStatusButton = (Button) findViewById(R.id.volunteerStatusButton);
        if(volunteerStat.equals("Yes")){
            volunteerStatusShow.setText("You are a volunteer");
            volunteerStatusShow.setBackgroundColor(Color.parseColor("#339900"));
            volunteerStatusButton.setText("Opt out from Traahi Volunteer");
        }
        else{
            volunteerStatusShow.setText("You are not a volunteer");
            volunteerStatusShow.setBackgroundColor(Color.parseColor("#cf3025"));
            volunteerStatusButton.setText("Opt In for Traahi Volunteer");
        }


        volunteerStatus = (TextView) findViewById(R.id.volunteerStatus);
        volunteerStatus.setTypeface(custom);

        SharedPreferences sharedPrefContact = getSharedPreferences(LoginActivity.STORE_DATA_NAME, Context.MODE_PRIVATE);
        ownNumber = sharedPrefContact.getString("LOCAL_OWN_NUMBER", null);


        volunteerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(ownNumber).child("isaVolunteer");
        volMainDatabase = FirebaseDatabase.getInstance().getReference().child("Volunteers");
        userDB = FirebaseDatabase.getInstance().getReference().child("Users").child(ownNumber);

        mStorage = FirebaseStorage.getInstance().getReference().child("Profile Pictures").child(ownNumber);

        uploadProgress = new ProgressDialog(this);





        volunteerStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (isNetworkAvailable() && volunteer != null ) {
                    if (volunteerStat.equals("Yes")) {
                        volunteerDatabase.setValue("No");
                        volMainDatabase.child(ownNumber).removeValue();
                        volunteerStat = "No";
                        OneSignal.sendTag("isaVolunteer", "No");
                        volunteerStatusShow.setText("You are not a volunteer");
                        volunteerStatusShow.setBackgroundColor(Color.parseColor("#cf3025"));
                        volunteerStatusButton.setText("Opt In for Traahi Volunteer");
                    } else if (volunteerStat.equals("No")) {
                        volunteerDatabase.setValue("Yes");
                        volunteerStat = "Yes";
                        volMainDatabase.child(ownNumber).child("Profile").setValue(volunteer);
                        OneSignal.sendTag("isaVolunteer", "Yes");
                        volunteerStatusShow.setText("You are a volunteer");
                        if(picData == null){
                            Log.e("Pic Data","Null");
                            createDialogforProfilePic();
                        }
                        volunteerStatusShow.setBackgroundColor(Color.parseColor("#339900"));
                        volunteerStatusButton.setText("Opt out from Traahi Volunteer");
                    }
                }
                else{
                    Snackbar.make(findViewById(R.id.activity_traahi_volunteer), "No Internet Connection", Snackbar.LENGTH_SHORT)
                            .setAction("Ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                            .show();
                }
            }
        });
        nearbyVolunteers = (Button) findViewById(R.id.nearbyVolunteers);
        nearbyVolunteers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TraahiVolunteer.this, NearestVolunteers.class);
                startActivity(intent);
            }
        });



    }

    private void createDialogforProfilePic() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TraahiVolunteer.this);
        builder.setTitle("Set a Profile Picture");
        builder.setMessage("It is recommended to set a Profile Picture being a Volunteer");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                createDialogforIntent();

                dialog.cancel();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void createDialogforIntent() {
        final CharSequence[] items = { "Take Photo", "Choose from Gallery",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(TraahiVolunteer.this);
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
        if (resultCode == Activity.RESULT_OK && requestCode == 0) {

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
            else if(resultCode == Activity.RESULT_OK && requestCode == 1){
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




    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        volunteerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                volunteerStat = (String) dataSnapshot.getValue();
                if(volunteerStat!=null){
                    Log.e("data",volunteerStat);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        userDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                volunteer = (HashMap) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference db = userDB.child("Profile").child("picUrl");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                picData = (String) dataSnapshot.getValue();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
