package com.invincix.traahi;

/**
 * Created by Ashis on 10/4/2017.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ContactsAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<String> contactnames;
    private final ArrayList<String> contactnumbers;
    private final int contactimage;
    LayoutInflater layoutInflater;
    Typeface font;


    public ContactsAdapter(Context mContext, ArrayList<String> contactnames, ArrayList<String> contactnumbers, int contactimage) {
        this.mContext = mContext;
        this.contactnames = contactnames;
        this.contactnumbers = contactnumbers;
        this.contactimage = contactimage;
        font = Typeface.createFromAsset(mContext.getAssets(), "fonts/fontawesome-webfont.ttf");

    }

    // 2
    @Override
    public int getCount() {
        return contactnames.size();

    }

    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 4
    @Override
    public Object getItem(int position) {
        return null;
    }



    // 5
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;
        if (convertView == null) {

            gridView = new View(mContext);

            // get layout from grid_item.xml ( Defined Below )

            gridView = inflater.inflate( R.layout.contactresource , null);

            // set value into textview

            TextView contactname = (TextView) gridView
                    .findViewById(R.id.textview_contact_name);
            TextView contactnumber=(TextView) gridView.findViewById(R.id.textview_contact_number);


            Log.e(String.valueOf(position),"hiii");
            contactname.setText(contactnames.get(position));
            contactnumber.setText(contactnumbers.get(position));


            // set image based on selected text

            TextView textView = (TextView) gridView
                    .findViewById(R.id.imageview_contact);
            textView.setTypeface(font);

            String value = contactnames.get(position);

            if (value!=null) {

                textView.setText("\uf007");

            }
        } else {

            gridView = (View) convertView;
        }

        return gridView;





    }

}