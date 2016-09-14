package devchallenge.android.radiotplayer.repository;

public class PodcastsInfoProvider {
    private static volatile PodcastsInfoProvider sInstance;

    public static PodcastsInfoProvider getInstance() {
        synchronized (PodcastsInfoProvider.class) {
            if (sInstance == null) {
                sInstance = new PodcastsInfoProvider();
            }
        }
        return sInstance;
    }
}
