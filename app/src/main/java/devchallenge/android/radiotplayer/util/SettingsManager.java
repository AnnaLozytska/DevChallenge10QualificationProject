package devchallenge.android.radiotplayer.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;

import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import java.util.concurrent.TimeUnit;

import devchallenge.android.radiotplayer.App;
import devchallenge.android.radiotplayer.R;
import devchallenge.android.radiotplayer.event.EventManager;
import devchallenge.android.radiotplayer.event.PodcastsSyncEvent;

public class SettingsManager {
    private static volatile SettingsManager sInstance;

    public static SettingsManager getInstance() {
        synchronized (SettingsManager.class) {
            if (sInstance == null) {
                if (sInstance == null) {
                    sInstance = new SettingsManager(App.getInstance().getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    private final Context mContext;
    private final Resources mResources;
    private volatile SharedPreferences mPreferences;

    private SettingsManager(Context appContext) {
        mContext = appContext;
        mResources = appContext.getResources();
        EventManager.getInstance().registerEventListener(this);
    }

    private SharedPreferences getPreferences() {
        if (mPreferences == null) {
            synchronized (this) {
                if (mPreferences == null) {
                    mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                }
            }
        }
        return mPreferences;
    }
    private String getKey(@StringRes int resId) {
        return mResources.getString(resId);
    }

    public int getPodcastsSyncInterval() {
        return (int) getPreferences()
                .getLong(getKey(R.string.pref_key_podcasts_sync_interval),
                        TimeUnit.HOURS.toSeconds(1));
    }

    public void setPodcastsSyncInterval() {
        //TBD
    }

    @Produce
    public PodcastsSyncEvent getLastSyncEvent() {
        PodcastsSyncEvent.Status lastStatus = PodcastsSyncEvent.Status.valueOf(getPreferences()
                .getString(getKey(R.string.pref_key_last_sync_status), PodcastsSyncEvent.Status.UNKNOWN.name()));
        PodcastsSyncEvent lastSyncEvent = new PodcastsSyncEvent(lastStatus);
        lastSyncEvent.setEventTimestamp(getPreferences().getLong(getKey(R.string.pref_key_last_sync_timestamp), 0L));
        lastSyncEvent.setHaveUpdates(getPreferences().getBoolean(getKey(R.string.pref_key_last_sync_have_updates), false));
        lastSyncEvent.setError(getPreferences().getString(getKey(R.string.pref_key_last_sync_error), null));
        lastSyncEvent.setPersisted(true);
        return lastSyncEvent;
    }

    @Subscribe
    public void setLastSyncEvent(PodcastsSyncEvent event) {
        if (!event.isPersisted()) { // e.g. is not produced by SettingManager
            getPreferences().edit()
                    .putLong(getKey(R.string.pref_key_last_sync_timestamp), event.getEventTimestamp())
                    .putString(getKey(R.string.pref_key_last_sync_status), event.getStatus().name())
                    .putBoolean(getKey(R.string.pref_key_last_sync_have_updates), event.haveUpdates())
                    .putString(getKey(R.string.pref_key_last_sync_error), event.getError())
                    .apply();
        }
    }
}
