package com.invincix.traahi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.victor.loading.newton.NewtonCradleLoading;

import java.util.HashMap;
import java.util.List;

public class LoginActivity extends AppCompatActivity {


    private TextView smsverify,termstext,otptext,phonetext,nametext,logintoolbartext, goBack;
    private CardView otpcardname,otpcardcreate,phonecard;
    private static final String TAG = "PhoneLogin";
    private boolean mVerificationInProgress = false;
    private String mVerificationId, userName;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private Button signin,createacc,createaccname;
    private DatabaseReference loginDatabase, contactDatabase;
    private EditText edittext_phone, edittext_otp, edittext_firstName, edittext_lastName;
    private String phone_number;
    private PhoneAuthCredential cred;
    public static final String STORE_DATA_NAME = "MyPref";
    private NewtonCradleLoading newtonCradleLoading;
    private int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        newtonCradleLoading = (NewtonCradleLoading) findViewById(R.id.loginProgress);
        newtonCradleLoading.start();
        smsverify = (TextView) findViewById(R.id.SMS_Verify_text);
        termstext = (TextView) findViewById(R.id.termstext);
        otptext = (TextView) findViewById(R.id.textView_name_OTP);
        phonetext = (TextView) findViewById(R.id.textView_phone_LogIn);
        nametext = (TextView) findViewById(R.id.textView_name_LogIn);
        goBack = (TextView) findViewById(R.id.backButton);
        logintoolbartext = (TextView) findViewById(R.id.logintoolbartext) ;
        edittext_phone = (EditText) findViewById(R.id.editText_phone);
        edittext_otp = (EditText) findViewById(R.id.editText_otp);
        edittext_firstName = (EditText) findViewById(R.id.editText_firstName);
        edittext_lastName = (EditText) findViewById(R.id.editText_lastName);
        otpcardname = (CardView) findViewById(R.id.otpcardname);
        otpcardcreate = (CardView) findViewById(R.id.otpcardcreate);
        phonecard = (CardView) findViewById(R.id.phonecard);
        signin = (Button) findViewById(R.id.signin_button);
        createacc = (Button) findViewById(R.id.createaccbutton);
        createaccname = (Button) findViewById(R.id.createaccbuttonname);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        otptext.setTypeface(typeface);
        nametext.setTypeface(typeface);
        phonetext.setTypeface(typeface);
        goBack.setTypeface(typeface);
        otptext.setText("\uf084");
        phonetext.setText("\uf10b");




        int col = Color.parseColor("#339900");
        newtonCradleLoading.setLoadingColor(col);
        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted( PhoneAuthCredential credential) {
                 Log.d(TAG, "onVerificationCompleted:" + credential);
                mVerificationInProgress = false;
                cred = credential;
                phonecard.setVisibility(View.GONE);
                smsverify.setVisibility(View.GONE);
                goBack.setVisibility(View.VISIBLE);

                loginDatabase.child(phone_number).child("Profile").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String fvalue = (String) dataSnapshot.child("firstName").getValue();
                        String lvalue = (String) dataSnapshot.child("lastName").getValue();
                        String value = fvalue+" "+lvalue;

                        if (fvalue!=null && lvalue != null)
                        {

                            newtonCradleLoading.setVisibility(View.VISIBLE);
                            Log.e("Name: ",value);
                            userName = value;
                            signInWithPhoneAuthCredential(cred);
                        }
                        else{
                            edittext_otp.setText("Number Verified");
                            edittext_otp.setEnabled(false);
                            otpcardname.setVisibility(View.VISIBLE);
                            createaccname.setVisibility(View.VISIBLE);
                            createacc.setVisibility(View.GONE);
                            otpcardcreate.setVisibility(View.VISIBLE);
                            createacc.setVisibility(View.GONE);
                            termstext.setVisibility(View.VISIBLE);
                            logintoolbartext.setText("Set Your Name");

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                Toast.makeText(LoginActivity.this,"Phone Verification Complete", Toast.LENGTH_SHORT).show();
                Log.e("on verification", " c");


            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                 Log.w(TAG, "onVerificationFailed", e);
                Toast.makeText(LoginActivity.this,"Verification Failed",Toast.LENGTH_SHORT).show();

                newtonCradleLoading.setVisibility(View.GONE);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(LoginActivity.this,"InValid Phone Number",Toast.LENGTH_SHORT).show();
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                }

            }

            @Override
            public void onCodeSent(final String verificationId,
                                   final PhoneAuthProvider.ForceResendingToken token) {


                loginDatabase.child(phone_number).child("Profile").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String fvalue = (String) dataSnapshot.child("firstName").getValue();
                        String lvalue = (String) dataSnapshot.child("lastName").getValue();


                        if (fvalue != null && lvalue != null) {
                            edittext_firstName.setText(fvalue);
                            edittext_lastName.setText(lvalue);
                            createacc.setText("Sign In");

                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                Toast.makeText(LoginActivity.this,"Verification code has been send on your number",Toast.LENGTH_SHORT).show();
                // Save verification ID and resending token so we can use them later
                phonecard.setVisibility(View.GONE);
                otpcardcreate.setVisibility(View.VISIBLE);
                otpcardname.setVisibility(View.VISIBLE);
                smsverify.setVisibility(View.GONE);
                termstext.setVisibility(View.VISIBLE);
                mVerificationId = verificationId;
                mResendToken = token;
                createacc.setVisibility(View.VISIBLE);
                newtonCradleLoading.setVisibility(View.GONE);
                goBack.setVisibility(View.VISIBLE);

                logintoolbartext.setText("Verify");


            }
        };


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissions();


                }


        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phonecard.setVisibility(View.VISIBLE);
                otpcardcreate.setVisibility(View.GONE);
                otpcardname.setVisibility(View.GONE);
                smsverify.setVisibility(View.VISIBLE);
                termstext.setVisibility(View.GONE);
                createacc.setVisibility(View.GONE);
                createaccname.setVisibility(View.GONE);
                newtonCradleLoading.setVisibility(View.GONE);
                goBack.setVisibility(View.GONE);

                logintoolbartext.setText("Verify");

            }
        });

        createaccname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validation_signup(edittext_firstName.getText().toString(),edittext_lastName.getText().toString())) {
                    newtonCradleLoading.setVisibility(View.VISIBLE);
                    loginDatabase.child(phone_number).child("Profile").child("firstName").setValue(edittext_firstName.getText().toString());
                    loginDatabase.child(phone_number).child("Profile").child("lastName").setValue(edittext_lastName.getText().toString());
                    userName = edittext_firstName.getText().toString()+" "+edittext_lastName.getText().toString();
                    signInWithPhoneAuthCredential(cred);
                }
                else{
                    newtonCradleLoading.setVisibility(View.GONE);

                }

            }
        });

        createacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(validation_signup_name()) {
                    newtonCradleLoading.setVisibility(View.VISIBLE);
                    final PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, edittext_otp.getText().toString());

                            loginDatabase.child(phone_number).child("Profile").child("firstName").setValue(edittext_firstName.getText().toString());
                    loginDatabase.child(phone_number).child("Profile").child("lastName").setValue(edittext_lastName.getText().toString());
                    userName = edittext_firstName.getText().toString()+" "+edittext_lastName.getText().toString();
                            signInWithPhoneAuthCredential(credential);

                        }
                else{
                    newtonCradleLoading.setVisibility(View.GONE);

                }
            }
        });





    }

    private  void checkPermissions() {

        Dexter.withActivity(this)
                .withPermissions(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_CONTACTS,
                        android.Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            if(validation_number(edittext_phone.getText().toString())) {
                                if (isNetworkAvailable()) {
                                    newtonCradleLoading.setVisibility(View.VISIBLE);
                                    phone_number = edittext_phone.getText().toString();
                                    String firebase_num = "+91"+phone_number;
                                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                            firebase_num,
                                            60,
                                            java.util.concurrent.TimeUnit.SECONDS,
                                            LoginActivity.this,
                                            mCallbacks);
                                } else {
                                    Snackbar.make(findViewById(R.id.loginLayout), "No Internet Connection", Snackbar.LENGTH_LONG)
                                            .setAction("OK", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {


                                                }
                                            })
                                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                            .show();
                                }
                            }
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Need Permissions for Traahi");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
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

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private boolean validation_number(String data_valid_number) {
        boolean valid = true;
        if (data_valid_number.length() != 10) {
            edittext_phone.setError("Invalid Number");
            valid = false;


        } else {
        }



        return valid;
    }





    public  boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean validation_signup_name() {
        boolean valid = true;
        String data_fname = edittext_firstName.getText().toString();
        String data_lname = edittext_lastName.getText().toString();
        String data_otp = edittext_otp.getText().toString();
        if (TextUtils.isEmpty(data_fname)) {
            edittext_firstName.setError("Required");
            valid = false;
        } else {
            edittext_firstName.setError(null);
        }
        if (TextUtils.isEmpty(data_lname)) {
            edittext_lastName.setError("Required");
            valid = false;
        } else {
            edittext_lastName.setError(null);
        }
        if (TextUtils.isEmpty(data_otp)) {
            edittext_otp.setError("Required");
            valid = false;
        } else {
            edittext_otp.setError(null);
        }
        if(data_fname.length() < 3 )
        {
            edittext_firstName.setError("Minimum 3 Characters");
            valid = false;
        }
        else{
            edittext_firstName.setError(null);
        }
        if(data_lname.length() < 2 )
        {
            edittext_lastName.setError("Minimum 2 Characters");
            valid = false;
        }
        else{
            edittext_lastName.setError(null);
        }
        return valid;
    }

    private boolean validation_signup(String fname,String lname) {
        boolean valid = true;


        if (fname.length() < 3) {
            edittext_firstName.setError("Atleast 3 digits");
            valid = false;
        } else {
            edittext_firstName.setError(null);
        }
        if (lname.length() < 2) {
            edittext_firstName.setError("Atleast 2 digits");
            valid = false;
        } else {
            edittext_lastName.setError(null);
        }
        return valid;
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            newtonCradleLoading.setVisibility(View.VISIBLE);
                            // Log.d(TAG, "signInWithCredential:success");
                            final SharedPreferences sharedPref = getSharedPreferences(STORE_DATA_NAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("LOCAL_NAME", userName);
                            editor.putString("LOCAL_OWN_NUMBER",phone_number);
                            editor.apply();

                            final SharedPreferences sharedPrefContact = getSharedPreferences(MainActivity.STORE_DATA, Context.MODE_PRIVATE);

                                counter = -1;
                                contactDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(phone_number).child("Contacts");
                                contactDatabase.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        HashMap<String, String> value = (HashMap<String, String>) dataSnapshot.getValue();
                                        if (value != null) {
                                            for (HashMap.Entry<String, String> entry : value.entrySet()) {
                                                counter++;
                                                Log.e(entry.getKey(), "Names");
                                                Log.e(entry.getValue(), "Numbers");
                                                SharedPreferences.Editor editor = sharedPrefContact.edit();
                                                editor.putString("LOCAL_CONTACT_NAME_" + String.valueOf(counter), entry.getKey());
                                                editor.putString("LOCAL_PHONE_NUMBER_" + String.valueOf(counter), entry.getValue());

                                                editor.apply();

                                            }
                                            SharedPreferences.Editor editorCount = sharedPrefContact.edit();
                                            editorCount.putInt("CONTACT_NUMBER", counter);
                                            editorCount.apply();

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            loginDatabase.child(phone_number).child("isaVolunteer").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String value = (String) dataSnapshot.getValue();
                                    if(value == null){
                                        loginDatabase.child(phone_number).child("isaVolunteer").setValue("No");

                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            finish();
                            Toast.makeText(LoginActivity.this,"Verification Done",Toast.LENGTH_SHORT).show();


                        } else {
                             Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                newtonCradleLoading.setVisibility(View.GONE);

                            }
                        }
                    }
                });
    }
}
