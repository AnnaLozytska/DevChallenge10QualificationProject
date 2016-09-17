package devchallenge.android.radiotplayer.repository;

public class AudioProvider {
    private static volatile AudioProvider sInstance;

    public static AudioProvider getInstance() {
        synchronized (AudioProvider.class) {
            if (sInstance == null) {
                sInstance = new AudioProvider();
            }
        }
        return sInstance;
    }

    private PersistentStorageManager mStorageManager;

    private AudioProvider() {
    }
}
