package devchallenge.android.radiotplayer.util;

import java.util.List;

import devchallenge.android.radiotplayer.event.EventManager;
import devchallenge.android.radiotplayer.model.PodcastInfoModel;

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

    private List<PodcastInfoModel> queue;
    private EventManager mEventManager;

    private PodcastQueueManager() {
        mEventManager = EventManager.getInstance();
    }

    public PodcastInfoModel getNextItem(PodcastInfoModel item) {
        //TODO handle empty list
        throw new UnsupportedOperationException("TBD");
    }

    public PodcastInfoModel getPreviousItem(PodcastInfoModel item) {
        throw new UnsupportedOperationException("TBD");
    }

    public boolean hasNext(PodcastInfoModel item) {
        throw new UnsupportedOperationException("TBD");
    }

    public boolean hasPresious(PodcastInfoModel item) {
        throw new UnsupportedOperationException("TBD");
    }
}
