package it.polito.mobile.androidassignment2.gcm;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import it.polito.mobile.androidassignment2.businessLogic.RESTManager;

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
                Log.d(TAG, "deleting gcm InstanceID from device");
                sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
                InstanceID.getInstance(context).deleteInstanceID();

                try {
                    String token = sharedPreferences.getString(QuickstartPreferences.GCM_TOKEN, "");
                    if (!token.equals("")) {
                        RESTManager.send(RESTManager.DELETE, "gcm/" + token, null);
                        sharedPreferences.edit().remove(QuickstartPreferences.GCM_TOKEN).apply();
                        Log.d(TAG, "deleting gcm token from server");
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Couldn't delete token from server", e);
                }

            } catch (IOException e) {
                Log.e(TAG, "problem deregistering your gcm token");
            }
            return null;
        }
    };
}
