package devchallenge.android.radiotplayer;

import android.app.Application;
import android.util.Log;

import devchallenge.android.radiotplayer.net.sync.SyncManager;

public class App extends Application {
    private static final String TAG = App.class.getSimpleName();

    private static volatile App sInstance;

    public static App getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        //TODO: DELETE AFTER TESTING:
        Log.d(TAG, "Launched app...");
        SyncManager.getInstance().syncFeed();
    }
}
