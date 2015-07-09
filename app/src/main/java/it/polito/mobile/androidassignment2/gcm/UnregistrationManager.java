package it.polito.mobile.androidassignment2.gcm;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

public class UnregistrationManager {
    private Context context;
    private SharedPreferences sharedPreferences;
    private final static String TAG = "UnregistrationMan";

    public UnregistrationManager(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void unregisterGcm() {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        task.execute();
    }

    AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Log.d(TAG, "deleting gcm InstanceID");
                sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
                InstanceID.getInstance(context).deleteInstanceID();
            } catch (IOException e) {
                Log.e(TAG, "problem deregistering your gcm token");
            }
            return null;
        }
    };
}
