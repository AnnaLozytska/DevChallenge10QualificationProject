package devchallenge.android.radiotplayer;

import android.app.Application;

import devchallenge.android.radiotplayer.net.sync.SyncManager;
import devchallenge.android.radiotplayer.repository.PodcastsInfoProvider;
import devchallenge.android.radiotplayer.util.SettingsManager;

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
        PodcastsInfoProvider.getInstance().startManualSync();
        int syncInterval = SettingsManager.getInstance().getPodcastsSyncInterval();
        SyncManager.getInstance().schedulePodcastsSync(syncInterval);

    }
}
