package devchallenge.android.radiotplayer.util;

import com.squareup.otto.Subscribe;

import java.util.Collections;
import java.util.List;

import devchallenge.android.radiotplayer.event.EventManager;
import devchallenge.android.radiotplayer.event.PlayerUpdateEvent;
import devchallenge.android.radiotplayer.event.PodcastsSyncEvent;
import devchallenge.android.radiotplayer.model.PodcastInfoModel;
import devchallenge.android.radiotplayer.repository.PodcastsInfoProvider;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import static devchallenge.android.radiotplayer.event.PodcastsSyncEvent.Status.FINISHED;
import static devchallenge.android.radiotplayer.util.PersistentStorageManager.DownloadStatus;

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
    private BehaviorSubject<List<PodcastInfoModel>> mQueueUpdates;
    private PodcastInfoModel mCurrentPlaying;
    private PodcastsInfoProvider mProvider;
    private PersistentStorageManager mStorageManager;
    private EventManager mEventManager;

    private PodcastQueueManager() {
        // using Rx BehaviorSubject instead of posting event creates faster and
        // more seamless one-way data transfer from storage to podcast queue consumers
        mQueueUpdates = BehaviorSubject.create(queue);
        mProvider = PodcastsInfoProvider.getInstance();
        mStorageManager = PersistentStorageManager.getInstance();
        mEventManager = EventManager.getInstance();
        mEventManager.registerEventListener(this);
        requestPodcastsList();
    }

    public Observable<List<PodcastInfoModel>> getPodcastQueueUpdates() {
        return mQueueUpdates;
    }

    @Subscribe
    public void onPlayerUpdateEvent(PlayerUpdateEvent update) {

    }

    @Subscribe
    public void onSyncFinishedEvent(PodcastsSyncEvent sync) {
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

    private void requestPodcastsList() {
        mProvider.getPodcasts()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Action1<List<PodcastInfoModel>>() {
                    @Override
                    public void call(List<PodcastInfoModel> podcastInfos) {
                        queue = podcastInfos;
                        updateItemsDownloadStatus();
                        updateCurrentlyPlaying(mCurrentPlaying);
                        mQueueUpdates.onNext(queue);
                    }
                });
    }

    private void updateItemsDownloadStatus() {
        for (PodcastInfoModel item : queue) {
            DownloadStatus itemStatus = mStorageManager.getItemStatus(item.getTitle());
            item.setDownloadStatus(itemStatus);
            if (itemStatus == DownloadStatus.DOWNLOADED) {
                item.setLocalAudioUri(mStorageManager.getItemLocalUri(item.getTitle()));
            }
        }
    }

    private void updateCurrentlyPlaying(PodcastInfoModel currentPlaying) {

    }
}
