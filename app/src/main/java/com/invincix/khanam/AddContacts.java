package com.invincix.khanam;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AddContacts extends AppCompatActivity {

    private TextView toolbarText;
    public ArrayList<String>  contactnames =new ArrayList<String>();
    public ArrayList<String> contactnumbers=new ArrayList<String>();
    public Set<String> setname = new HashSet<String>();
    public Set<String> setnumber = new HashSet<String>();

    public String data_contact_name,data_contact_number;
    int counter=0;
    public static int contactimage= R.drawable.ic_contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SharedPreferences sharedPref = getSharedPreferences(MainActivity.STORE_DATA, Context.MODE_PRIVATE);

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

                                                        AlertDialog.Builder builder = new AlertDialog.Builder(AddContacts.this);
                                                        LayoutInflater inflater = (LayoutInflater) AddContacts.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                                        builder.setView(R.layout.add_contact_dialog);
                                                        builder.setView(inflater.inflate(R.layout.add_contact_dialog, null));
                                                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

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

                PopupMenu popupMenu=new PopupMenu(AddContacts.this,view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_grid,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.action_edit:

                                AlertDialog.Builder builder = new AlertDialog.Builder(AddContacts.this);
                                LayoutInflater inflater = (LayoutInflater) AddContacts.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                builder.setView(R.layout.edit_contact_dialog);
                                builder.setView(inflater.inflate(R.layout.add_contact_dialog, null));
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
                                contactnames.remove(position);
                                contactnumbers.remove(position);
                                final GridView gridView = (GridView) findViewById(R.id.grid);
                                final ContactsAdapter contactsAdapter = new ContactsAdapter(AddContacts.this, contactnames, contactnumbers, contactimage);
                                gridView.setAdapter(contactsAdapter);
                                counter--;
                                SharedPreferences sharedPref = getSharedPreferences(MainActivity.STORE_DATA, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private boolean validation_of_data(String data_valid_name,String data_valid_number) {

        boolean valid = true;

        EditText contactname = (EditText) findViewById(R.id.contactname);
        EditText contactnumber = (EditText)findViewById(R.id.contactnumber);


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
