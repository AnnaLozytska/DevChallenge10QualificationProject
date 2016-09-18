package devchallenge.android.radiotplayer.util;

import android.util.Log;

import com.squareup.otto.Subscribe;

import java.util.Collections;
import java.util.List;

import devchallenge.android.radiotplayer.event.DownloadUpdateEvent;
import devchallenge.android.radiotplayer.event.EventManager;
import devchallenge.android.radiotplayer.event.PlayerUpdateEvent;
import devchallenge.android.radiotplayer.event.PodcastsSyncEvent;
import devchallenge.android.radiotplayer.model.PodcastInfoModel;
import devchallenge.android.radiotplayer.repository.PodcastsInfoProvider;
import devchallenge.android.radiotplayer.service.PlayerService;
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

    public PodcastInfoModel getItem(String title) {
        for (PodcastInfoModel item : queue) {
            if (item.getTitle().equals(title)) {
                return item;
            }
        }
        return null;
    }

    public boolean hasPodcasts() {
        return !Utils.isEmpty(queue);
    }

    public PodcastInfoModel getFirstItem() {
        if (!Utils.isEmpty(queue)) {
            return queue.get(0);
        } else {
            throw new IllegalStateException("Podcasts queue is empty");
        }
    }

    public PodcastInfoModel getNextItem(String itemTitle) {
        int itemIndex = queue.indexOf(getItem(itemTitle));
        if (itemIndex < queue.size() - 1) {
            return queue.get(itemIndex + 1);
        } else {
            throw new IllegalStateException(itemTitle + " doesn't have next");
        }
    }

    public PodcastInfoModel getPreviousItem(String itemTitle) {
        int itemIndex = queue.indexOf(getItem(itemTitle));
        if (itemIndex > 0) {
            return queue.get(itemIndex - 1);
        } else {
            throw new IllegalStateException(itemTitle + " doesn't have previous");
        }
    }

    public boolean hasNext(String itemTitle) {
        return queue.indexOf(getItem(itemTitle)) < queue.size() - 1;
    }

    public boolean hasPrevious(String itemTitle) {
        return queue.indexOf(getItem(itemTitle)) > 0;
    }

    @Subscribe
    public void onPlayerUpdateEvent(PlayerUpdateEvent update) {
        PodcastInfoModel updated = update.getPlayingPodcast();
        // if it is the same item
        if (mCurrentPlaying != null && updated.getTitle().equals(mCurrentPlaying.getTitle())) {
            if (mCurrentPlaying.getPlayingState() == updated.getPlayingState()) {
                // do nothing - its current position update
            } else {
                mCurrentPlaying.setPlayingState(updated.getPlayingState());
                mQueueUpdates.onNext(queue);
            }
        } else {
            if (mCurrentPlaying != null) {
                mCurrentPlaying.setPlayingState(PlayerService.STOPPED);
                mCurrentPlaying.setCurrentPosition(0);
            }
            mCurrentPlaying = getItem(updated.getTitle());
            mCurrentPlaying.setPlayingState(updated.getPlayingState());
            mCurrentPlaying.setCurrentPosition(updated.getCurrentPosition());
            mCurrentPlaying.setTotalDuration(updated.getTotalDuration());
            mQueueUpdates.onNext(queue);
        }
    }

    @Subscribe
    public void onDownloadUpdateEvent(DownloadUpdateEvent download) {
        for (PodcastInfoModel item : queue) {
            if (item.getTitle().equals(download.getLoadingItemTitle())) {
                item.setDownloadStatus(download.getStatus());
                mQueueUpdates.onNext(queue);
            }
        }
    }

    @Subscribe
    public void onSyncFinishedEvent(PodcastsSyncEvent sync) {
        if (sync.getStatus() == FINISHED) {
            requestPodcastsList();
        }
    }

    private void requestPodcastsList() {
        //TODO: DELETE AFTER TESTING:
        Log.d("---Test---", "Requesting podcasts");

        mProvider.getPodcasts()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Action1<List<PodcastInfoModel>>() {
                    @Override
                    public void call(List<PodcastInfoModel> podcastInfos) {
                        queue = podcastInfos;
                        Collections.sort(queue);
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
