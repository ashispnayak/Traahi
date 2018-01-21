package com.invincix.traahi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CreateNews extends AppCompatActivity {
    private String phoneNumber,  userChoosenTask;
    private TextView newsToolbarText;
    private ImageView postImg;
    private Button changePostImg,submitPost;
    private ProgressDialog uploadProg;
    private EditText postDesc;
    private StorageReference mStorage;
    private Uri mainUri;
    private DatabaseReference submissionDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_news);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        phoneNumber = extras.getString("Number");

        uploadProg = new ProgressDialog(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.newsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        assert toolbar != null;
        toolbar.bringToFront();

        newsToolbarText = (TextView) findViewById(R.id.newstoolbartext);
        Typeface custom = Typeface.createFromAsset(getAssets(), "fonts/toolbarfont.ttf");
        newsToolbarText.setTypeface(custom);

        postImg = (ImageView) findViewById(R.id.postImg);
        postDesc = (EditText) findViewById(R.id.pstDescription);

        mStorage = FirebaseStorage.getInstance().getReference().child("Submissions").child(phoneNumber);
        submissionDB = FirebaseDatabase.getInstance().getReference().child("Submissions").child(phoneNumber);


        changePostImg = (Button) findViewById(R.id.changePostImg);
        changePostImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialogforPostImage();
            }
        });

        submitPost = (Button) findViewById(R.id.submitPost);
        submitPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (postImg.getDrawable() != null && postDesc.getText() != null) {
                    uploadProg.setMessage("Uploading...");
                    uploadProg.show();

                    mStorage.putFile(mainUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String url = taskSnapshot.getDownloadUrl().toString();
                            submissionDB.child("picUrl").setValue(url);
                            submissionDB.child("postDesc").setValue(postDesc.getText().toString());

                            Toast.makeText(getApplicationContext(), "Upload Success", Toast.LENGTH_LONG).show();
                            uploadProg.dismiss();


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Upload Failed, Please Try Again", Toast.LENGTH_LONG).show();
                            uploadProg.dismiss();
                        }
                    });
                }
                else{
                    if(postImg.getDrawable() == null){
                        Toast.makeText(getApplicationContext(),"No image provided...",Toast.LENGTH_SHORT).show();
                    }
                    else if(postDesc.getText() == null){
                        postDesc.setError("Required");
                    }
                }
            }



            {

            }
        });
    }



    private void createDialogforPostImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Gallery",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateNews.this);
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

            Uri uri = data.getData();
            postImg.setImageURI(null);
            postImg.setImageURI(uri);
            mainUri = uri;


        }
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){

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

            postImg.setImageBitmap(thumbnail);
            mainUri = thumbNail;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
