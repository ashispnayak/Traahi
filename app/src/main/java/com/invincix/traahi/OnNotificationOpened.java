package com.invincix.traahi;

/**
 * Created by Ashis on 1/6/2018.
 */

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;


public class OnNotificationOpened implements OneSignal.NotificationOpenedHandler {
    // This fires when a notification is opened by tapping on it.
    private Application context;
    public OnNotificationOpened(Application application) {
        this.context = application;
    }
    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        OSNotificationAction.ActionType actionType = result.action.type;
        JSONObject data = result.notification.payload.additionalData;
        String activityToBeOpened,lat,lng;
        //While sending a Push notification from OneSignal dashboard
        // you can send an addtional data named "activityToBeOpened" and retrieve the value of it and do necessary operation
        //If key is "activityToBeOpened" and value is "AnotherActivity", then when a user clicks
        //on the notification, AnotherActivity will be opened.
        //Else, if we have not set any additional data MainActivity is opened.
        if (data != null) {
            Log.e("Notification Data",String.valueOf(data));
            activityToBeOpened = data.optString("activity", null);
           lat = data.optString("lat",null);
            lng = data.optString("long",null);
            if (activityToBeOpened != null && activityToBeOpened.equals("VictimLocation")) {
                Intent intent = new Intent(context, VictimLocation.class);
                Bundle extras = new Bundle();
                extras.putString("lat",lat);
                extras.putString("lng",lng);
                intent.putExtras(extras);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
               context.startActivity(intent);
            } else {
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
               context.startActivity(intent);
            }

        }

        //If we send notification with action buttons we need to specidy the button id's and retrieve it to
        //do the necessary operation.
    }
}
