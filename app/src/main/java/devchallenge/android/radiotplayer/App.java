package devchallenge.android.radiotplayer;

import android.app.Application;

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
    }
}
