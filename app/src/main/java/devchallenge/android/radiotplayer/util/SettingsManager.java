package devchallenge.android.radiotplayer.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;

import java.util.concurrent.TimeUnit;

import devchallenge.android.radiotplayer.App;
import devchallenge.android.radiotplayer.R;

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
                        TimeUnit.HOURS.toSeconds(24));
    }

    public void setPodcastsSyncInterval() {
        //TBD
    }
}
