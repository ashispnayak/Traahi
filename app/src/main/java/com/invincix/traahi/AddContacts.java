package com.invincix.traahi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AddContacts extends AppCompatActivity {

    private TextView toolbarText;
    public ArrayList<String>  contactnames =new ArrayList<String>();
    public ArrayList<String> contactnumbers=new ArrayList<String>();
    private static final int PICK_CONTACT = 1;
    private Uri uriContact;
    private DatabaseReference contactDatabase;
    public String data_contact_name,data_contact_number, ownNumber;
    private String contactID,Rcontactname,Rcontactnumber;
    int counter=0;
    public static int contactimage= R.drawable.ic_contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final SharedPreferences sharedPref = getSharedPreferences(MainActivity.STORE_DATA, Context.MODE_PRIVATE);
        SharedPreferences sharedPrefs = getSharedPreferences(LoginActivity.STORE_DATA_NAME, Context.MODE_PRIVATE);
        ownNumber = sharedPrefs.getString("LOCAL_OWN_NUMBER", null);
        contactDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(ownNumber).child("Contacts");

        counter= (sharedPref.getInt("CONTACT_NUMBER",-1));
        Log.e(String.valueOf(counter),"counter value begin");


        for(int i=0;i<=counter;i++) {
            contactnumbers.add(sharedPref.getString("LOCAL_PHONE_NUMBER_"+String.valueOf(i), null));
            contactnames.add(sharedPref.getString("LOCAL_CONTACT_NAME_"+String.valueOf(i), null));
            Log.e(contactnames.get(i),"name1");

        }


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //add contact button
        Button addcontactsbutton=(Button)findViewById(R.id.addcontactsbutton);
        addcontactsbutton.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                    if(counter==7) {
                                                        Toast.makeText(getApplicationContext(), "Only 8 contacts Allowed", Toast.LENGTH_SHORT).show();

                                                    }
                                                     else {

                                                        LayoutInflater layoutInflater = LayoutInflater.from(AddContacts.this);
                                                        View addcontactView = layoutInflater.inflate(R.layout.add_contact_manual, null);
                                                        final AlertDialog alertD = new AlertDialog.Builder(AddContacts.this).create();
                                                        Button retrieve=(Button) addcontactView.findViewById(R.id.retrieveadd);
                                                        Button manual=(Button) addcontactView.findViewById(R.id.manualadd);
                                                        retrieve.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                alertD.dismiss();
                                                                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                                                                startActivityForResult(intent, PICK_CONTACT);



                                                            }
                                                        });
                                                        manual.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                AlertDialog.Builder builder = new AlertDialog.Builder(AddContacts.this);
                                                                LayoutInflater inflater = (LayoutInflater) AddContacts.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                                                builder.setView(R.layout.add_contact_dialog);
                                                                builder.setView(inflater.inflate(R.layout.add_contact_dialog, null));
                                                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        alertD.dismiss();

                                                                        AlertDialog aDialog = (AlertDialog) dialog;
                                                                        EditText contactname = (EditText) aDialog.findViewById(R.id.contactname);
                                                                        EditText contactnumber = (EditText) aDialog.findViewById(R.id.contactnumber);
                                                                        counter++;
                                                                        data_contact_name = contactname.getText().toString();
                                                                        data_contact_number = contactnumber.getText().toString();
                                                                        if(validation_of_data(data_contact_name,data_contact_number)) {
                                                                            SharedPreferences sharedPref = getSharedPreferences(MainActivity.STORE_DATA, Context.MODE_PRIVATE);
                                                                            SharedPreferences.Editor editor = sharedPref.edit();
                                                                            editor.putString("LOCAL_CONTACT_NAME_" + String.valueOf(counter), data_contact_name);
                                                                            editor.putString("LOCAL_PHONE_NUMBER_" + String.valueOf(counter), data_contact_number);
                                                                            editor.putInt("CONTACT_NUMBER", counter);
                                                                            editor.apply();
                                                                            contactDatabase.child(data_contact_name).setValue(data_contact_number);

                                                                            contactnames.add(data_contact_name);
                                                                            contactnumbers.add(data_contact_number);
                                                                            //gridview
                                                                            final GridView gridView = (GridView) findViewById(R.id.grid);
                                                                            final ContactsAdapter contactsAdapter = new ContactsAdapter(AddContacts.this, contactnames, contactnumbers, contactimage);
                                                                            gridView.setAdapter(contactsAdapter);



                                                                        }
                                                                        else{

                                                                        }




                                                                        Log.e(String.valueOf(counter), "counter value");
                                                                    }

                                                                });

                                                                Log.e("size", String.valueOf(contactnames.size()));


                                                                builder.show();

                                                            }
                                                        });
                                                        alertD.setView(addcontactView);

                                                        alertD.show();



                                                    }

                                                 }

                                             });

        Log.e("size", String.valueOf(contactnames.size()));





        //set toolbar text
        toolbarText = (TextView) findViewById(R.id.toolbartext);
        Typeface custom = Typeface.createFromAsset(getAssets(), "fonts/toolbarfont.ttf");
        toolbarText.setTypeface(custom);

        //gridview
        final GridView gridView = (GridView)findViewById(R.id.grid);
        final ContactsAdapter contactsAdapter = new ContactsAdapter(this, contactnames,contactnumbers,contactimage);
        gridView.setAdapter(contactsAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, final int position, long id) {
Log.e("Postition: ", String.valueOf(position));

                final int a = position;
                final int b = contactnames.size();
                PopupMenu popupMenu = new PopupMenu(AddContacts.this,view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_grid,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.action_edit:

                                AlertDialog.Builder builder = new AlertDialog.Builder(AddContacts.this);
                                LayoutInflater inflater = (LayoutInflater) AddContacts.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                builder.setView(R.layout.edit_contact_dialog);
                                builder.setView(inflater.inflate(R.layout.edit_contact_dialog, null));
                                builder.setPositiveButton("Save Edit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        AlertDialog aDialog = (AlertDialog) dialog;
                                        EditText contactname = (EditText) aDialog.findViewById(R.id.contactname);
                                        EditText contactnumber = (EditText) aDialog.findViewById(R.id.contactnumber);
                                        data_contact_name = contactname.getText().toString();
                                        data_contact_number = contactnumber.getText().toString();
                                        if (validation_of_data(data_contact_name, data_contact_number)) {
                                            SharedPreferences sharedPref = getSharedPreferences(MainActivity.STORE_DATA, Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            editor.putString("LOCAL_CONTACT_NAME_" + String.valueOf(position), data_contact_name);
                                            editor.putString("LOCAL_PHONE_NUMBER_" + String.valueOf(position), data_contact_number);
                                            editor.apply();
                                            contactDatabase.child(contactnames.get(position)).removeValue();
                                            contactDatabase.child(data_contact_name).setValue(data_contact_number);
                                            contactnames.set(position,data_contact_name);
                                            contactnumbers.set(position,data_contact_number);
                                            final GridView gridView = (GridView) findViewById(R.id.grid);
                                            final ContactsAdapter contactsAdapter = new ContactsAdapter(AddContacts.this, contactnames, contactnumbers, contactimage);
                                            gridView.setAdapter(contactsAdapter);

                                        }
                                    }
                                });
                                builder.show();
                                break;


                            case R.id.action_delete:
                                contactDatabase.child(contactnames.get(position)).removeValue();
                                contactnames.remove(position);
                                contactnumbers.remove(position);
                                final GridView gridView = (GridView) findViewById(R.id.grid);
                                final ContactsAdapter contactsAdapter = new ContactsAdapter(AddContacts.this, contactnames, contactnumbers, contactimage);
                                gridView.setAdapter(contactsAdapter);
                                counter--;
                                SharedPreferences sharedPref = getSharedPreferences(MainActivity.STORE_DATA, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                for(int i=a;i<b;i++)
                                {
                                 String phno=(sharedPref.getString("LOCAL_PHONE_NUMBER_"+String.valueOf(i+1), null));
                                    String contname=(sharedPref.getString("LOCAL_CONTACT_NAME_"+String.valueOf(i+1), null));
                                    editor.putString("LOCAL_PHONE_NUMBER_"+String.valueOf(i),phno);
                                    editor.putString("LOCAL_CONTACT_NAME_"+String.valueOf(i),contname);


                                }

                                editor.putInt("CONTACT_NUMBER", counter);
                                editor.apply();


                                break;
                            default:
                                break;



                        }
                        return false;
                    }
                });
                popupMenu.show();



            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {

            uriContact = data.getData();

            retrieveContactName();
            retrieveContactNumber();
            counter++;
            SharedPreferences sharedPref = getSharedPreferences(MainActivity.STORE_DATA, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("LOCAL_CONTACT_NAME_" + String.valueOf(counter), Rcontactname);
            editor.putString("LOCAL_PHONE_NUMBER_" + String.valueOf(counter), Rcontactnumber);
            editor.putInt("CONTACT_NUMBER", counter);
            editor.apply();
            contactDatabase.child(Rcontactname).setValue(Rcontactnumber);
            contactnames.add(Rcontactname);
            contactnumbers.add(Rcontactnumber);
            //gridview
            final GridView gridView = (GridView) findViewById(R.id.grid);
            final ContactsAdapter contactsAdapter = new ContactsAdapter(AddContacts.this, contactnames, contactnumbers, contactimage);
            gridView.setAdapter(contactsAdapter);



        }
    }

    private void retrieveContactNumber() {


        // getting contacts ID
        Cursor cursorID = getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {

            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }

        cursorID.close();

        Log.d("", "Contact ID: " + contactID);

        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contactID},
                null);


        if (cursorPhone.moveToFirst()) {
            Rcontactnumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Log.e("before", "Contact Phone Number: " + Rcontactnumber);

            if(Rcontactnumber.startsWith("+91"))
            {
                Rcontactnumber=Rcontactnumber.substring(3);
            }
            Rcontactnumber=Rcontactnumber.replaceAll(" ","");

        }


        cursorPhone.close();

        Log.e("after", "Contact Phone Number: " + Rcontactnumber);
    }

    private void retrieveContactName() {


        // querying contact data store
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);

        if (cursor.moveToFirst()) {

            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.

            Rcontactname = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        cursor.close();

        Log.d("", "Contact Name: " + Rcontactname);

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private boolean validation_of_data(String data_valid_name,String data_valid_number) {

        boolean valid = true;




        if (TextUtils.isEmpty(data_valid_name)){
            valid = false;
            Toast.makeText(getApplicationContext(), "Contact Name Cant be empty", Toast.LENGTH_LONG).show();
        } else {
        }

        if (data_valid_number.length() != 10) {
            valid = false;
            showMessageDialog("Error","Invalid Number");

        } else {
        }



        return valid;

    }
    private void showMessageDialog(String title, String message) {
        android.support.v7.app.AlertDialog ad = new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .create();
        ad.show();

    }


}
