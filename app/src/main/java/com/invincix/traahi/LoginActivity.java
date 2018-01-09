package com.invincix.traahi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import com.victor.loading.newton.NewtonCradleLoading;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {


    private TextView smsverify,termstext,otptext,phonetext,nametext,logintoolbartext;
    private CardView otpcardname,otpcardcreate,phonecard;
    private static final String TAG = "PhoneLogin";
    private boolean mVerificationInProgress = false;
    private String mVerificationId, userName;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private Button signin,createacc,createaccname;
    private DatabaseReference loginDatabase, contactDatabase;
    private EditText edittext_phone, edittext_otp, edittext_name;
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
        logintoolbartext = (TextView) findViewById(R.id.logintoolbartext) ;
        edittext_phone = (EditText) findViewById(R.id.editText_phone);
        edittext_otp = (EditText) findViewById(R.id.editText_otp);
        edittext_name = (EditText) findViewById(R.id.editText_name);
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

                loginDatabase.child(phone_number).child("Name").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String value = (String) dataSnapshot.getValue();

                        if (value!=null)
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


                loginDatabase.child(phone_number).child("Name").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String value = (String) dataSnapshot.getValue();

                        if (value != null) {
                            edittext_name.setText(value);
                            createacc.setText("Sign In");
                            newtonCradleLoading.setVisibility(View.GONE);
                        } else {

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

                logintoolbartext.setText("Verify");


            }
        };


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validation_number(edittext_phone.getText().toString())) {
                    if (isNetworkAvailable()) {
                        newtonCradleLoading.setVisibility(View.VISIBLE);
                        phone_number = edittext_phone.getText().toString();
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                edittext_phone.getText().toString(),
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


        });

        createaccname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validation_signup(edittext_name.getText().toString())) {
                    newtonCradleLoading.setVisibility(View.VISIBLE);
                    loginDatabase.child(phone_number).child("Name").setValue(edittext_name.getText().toString());
                    userName = edittext_name.getText().toString();
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

                            loginDatabase.child(phone_number).child("Name").setValue(edittext_name.getText().toString());
                            userName = edittext_name.getText().toString();
                            signInWithPhoneAuthCredential(credential);


                        }
                else{
                    newtonCradleLoading.setVisibility(View.GONE);

                }
            }
        });





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
        String data_name = edittext_name.getText().toString();
        String data_otp = edittext_otp.getText().toString();
        if (TextUtils.isEmpty(data_name)) {
            edittext_name.setError("Required");
            valid = false;
        } else {
            edittext_name.setError(null);
        }
        if (TextUtils.isEmpty(data_otp)) {
            edittext_otp.setError("Required");
            valid = false;
        } else {
            edittext_otp.setError(null);
        }
        if(data_name.length() > 0 && data_name.length() < 5 )
        {
            edittext_name.setError("Minimum 5 Characters");
            valid = false;
        }
        else{
            edittext_name.setError(null);
        }
        return valid;
    }

    private boolean validation_signup(String name) {
        boolean valid = true;


        if (name.length() < 4) {
            edittext_name.setError("Atleast 4 digits");
            valid = false;
        } else {
            edittext_name.setError(null);
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
