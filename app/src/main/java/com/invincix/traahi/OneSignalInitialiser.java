package com.invincix.traahi;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.karumi.dexter.Dexter;
import com.onesignal.OneSignal;

/**
 * Created by Ashis on 1/7/2018.
 */

public class OneSignalInitialiser extends Application {
    @Override
    public void onCreate() {
        super.onCreate();


        //MyNotificationOpenedHandler : This will be called when a notification is tapped on.
        //MyNotificationReceivedHandler : This will be called when a notification is received while your app is running.
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .setNotificationOpenedHandler(new OnNotificationOpened(this))
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
