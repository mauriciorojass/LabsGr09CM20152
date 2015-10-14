package co.edu.udea.cmovil.gr09.yamba;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.thenewcircle.yamba.client.YambaClient;
import com.thenewcircle.yamba.client.YambaClientException;
import com.thenewcircle.yamba.client.YambaStatus;

import java.util.List;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class RefreshService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    //public static final String ACTION_FOO = "co.edu.udea.cmovil.gr09.yamba.action.FOO";
    //public static final String ACTION_BAZ = "co.edu.udea.cmovil.gr09.yamba.action.BAZ";

    // TODO: Rename parameters
    //public static final String EXTRA_PARAM1 = "co.edu.udea.cmovil.gr09.yamba.extra.PARAM1";
    //public static final String EXTRA_PARAM2 = "co.edu.udea.cmovil.gr09.yamba.extra.PARAM2";

    private static final String TAG = RefreshService.class.getSimpleName();
    boolean isEmpty;

    public RefreshService() {
        super("RefreshService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreated");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.d(TAG, "onStarted");
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(this); //*
            final String username = prefs.getString("username", ""); //*
            final String password = prefs.getString("password", ""); //*

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                isEmpty = true;
                return;
            } //Verificar que no hayan campos vacíos

            DbHelper dbHelper = new DbHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            YambaClient cloud = new YambaClient(username, password); /*Se crea un nuevo
    cliente yamba*/
            try {
                List<YambaStatus> timeline = cloud.getTimeline(20); /* Obtener linea de
       tiempo, los últimos 20 estados*/
                for (YambaStatus status : timeline) { //
                    Log.d(TAG,
                            String.format("%s: %s", status.getUser(), status.getMessage())); //Imprimir estados en consola
                    values.clear();
                    values.put(StatusContract.Column.ID, status.getId());
                    values.put(StatusContract.Column.USER, status.getUser());
                    values.put(StatusContract.Column.MESSAGE, status.getMessage());
                    values.put(StatusContract.Column.CREATED_AT, status.getCreatedAt().getTime());
                    db.insertWithOnConflict(StatusContract.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                }
            } catch (YambaClientException e) { //
                Log.e(TAG, "Failed to fetch the timeline", e);
                e.printStackTrace();
            }
            return;
        }

       /* if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isEmpty) {
            Toast.makeText(this, "Please update your username and password", Toast.LENGTH_SHORT).show();
            isEmpty = false;
        }
        Log.d(TAG, "onDestroyed");
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */

    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
