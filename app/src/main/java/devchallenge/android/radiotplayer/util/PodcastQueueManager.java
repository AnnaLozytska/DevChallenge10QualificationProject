package devchallenge.android.radiotplayer.util;

public class PodcastQueueManager {
    private static volatile PodcastQueueManager sInstance;

    public static PodcastQueueManager getInstance() {
        synchronized (PodcastQueueManager.class) {
            if (sInstance == null) {
                sInstance = new PodcastQueueManager();
            }
        }
        return sInstance;
    }

    private PodcastQueueManager() {
    }
}
