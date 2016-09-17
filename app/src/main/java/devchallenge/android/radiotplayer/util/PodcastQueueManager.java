package devchallenge.android.radiotplayer.util;

import com.squareup.otto.Subscribe;

import java.util.Collections;
import java.util.List;

import devchallenge.android.radiotplayer.event.EventManager;
import devchallenge.android.radiotplayer.event.PlayerUpdateEvent;
import devchallenge.android.radiotplayer.event.PodcastsSyncEvent;
import devchallenge.android.radiotplayer.model.PodcastInfoModel;
import devchallenge.android.radiotplayer.repository.PodcastsInfoProvider;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static devchallenge.android.radiotplayer.event.PodcastsSyncEvent.Status.FINISHED;

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

    private List<PodcastInfoModel> queue = Collections.emptyList();
    private PodcastInfoModel mCurrentPlaying;
    private EventManager mEventManager;

    private PodcastQueueManager() {
        mEventManager = EventManager.getInstance();
        mEventManager.registerEventListener(this);
        requestPodcastsList();
    }

    private void requestPodcastsList() {
        PodcastsInfoProvider.getInstance()
                .getPodcasts()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Action1<List<PodcastInfoModel>>() {
                    @Override
                    public void call(List<PodcastInfoModel> podcastInfos) {
                        queue = podcastInfos;
                        updateItemsDownloadStatus();
                        updateCurrentlyPlayingInList(mCurrentPlaying);
                    }
                });
    }

    private void updateItemsDownloadStatus() {

    }

    private void updateCurrentlyPlayingInList(PodcastInfoModel currentPlaying) {

    }

    @Subscribe
    public void onPlayerUpdateEvent(PlayerUpdateEvent update) {

    }

    @Subscribe
    public void onSyncFinished(PodcastsSyncEvent sync) {
        if (sync.getStatus() == FINISHED && sync.haveUpdates()) {
            requestPodcastsList();
        }
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
