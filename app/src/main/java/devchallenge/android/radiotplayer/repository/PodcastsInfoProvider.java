package devchallenge.android.radiotplayer.repository;

import com.squareup.otto.Subscribe;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.data.SquidDatabase;
import com.yahoo.squidb.sql.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

import devchallenge.android.radiotplayer.event.EventManager;
import devchallenge.android.radiotplayer.event.PodcastsLoadedEvent;
import devchallenge.android.radiotplayer.event.PodcastsSyncEvent;
import devchallenge.android.radiotplayer.model.PodcastInfoModel;
import devchallenge.android.radiotplayer.net.model.PodcastInfoNet;
import devchallenge.android.radiotplayer.net.sync.SyncManager;
import devchallenge.android.radiotplayer.repository.modelspec.PodcastInfoRow;
import devchallenge.android.radiotplayer.util.Utils;
import rx.Single;
import rx.SingleSubscriber;

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

    private final SquidDatabase mDatabase;
    private EventManager mEventManager;
    private SyncManager mSyncManager;

    private PodcastsInfoProvider() {
        mDatabase = Storage.getInstance();
        mSyncManager = SyncManager.getInstance();
        mEventManager = EventManager.getInstance();
        mEventManager.registerEventListener(this);
    }

    public Single<List<PodcastInfoModel>> getPodcasts() {
        return Single.create(new Single.OnSubscribe<List<PodcastInfoModel>>() {
            @Override
            public void call(SingleSubscriber<? super List<PodcastInfoModel>> singleSubscriber) {
                List<PodcastInfoModel> podcastInfoModels;
                Query selectAll = Query.select();
                SquidCursor<PodcastInfoRow> cursor = mDatabase.query(PodcastInfoRow.class, selectAll);

                if (cursor.moveToFirst()) {
                    podcastInfoModels = new ArrayList<>(cursor.getCount());
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        PodcastInfoRow row = new PodcastInfoRow();
                        row.readPropertiesFromCursor(cursor);
                        podcastInfoModels.add(modelFromRow(row));
                    }
                } else {
                    podcastInfoModels = Collections.emptyList();
                }
                singleSubscriber.onSuccess(podcastInfoModels);
            }
        });
    }

    public void startManualSync() {
        mSyncManager.syncPodcastsManualy();
    }

    public void cancelSync() {
        mSyncManager.cancelCurrentPodcastsSync();
    }

    @Subscribe
    public void onPodcastsLoaded(final PodcastsLoadedEvent event) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                for (PodcastInfoNet netModel : event.getPodcasts()) {
                    PodcastInfoRow syncPodcast = rowFromNetModel(netModel);

                    Query selectByTitle = Query.select().where(PodcastInfoRow.TITLE.eq(syncPodcast.getTitle()));
                    SquidCursor<PodcastInfoRow> cursor = mDatabase.query(PodcastInfoRow.class, selectByTitle);

                    if (cursor.moveToFirst()) {
                        PodcastInfoRow podcastInDb = new PodcastInfoRow();
                        podcastInDb.readPropertiesFromCursor(cursor);
                        // We set same id to ensure that only difference of info values will be checked:
                        syncPodcast.setId(podcastInDb.getId());

                        if (!syncPodcast.equals(podcastInDb)) {
                            mDatabase.persist(syncPodcast);
                        }
                    } else {
                        mDatabase.persist(syncPodcast);
                    }
                }

                PodcastsSyncEvent syncEvent = new PodcastsSyncEvent(PodcastsSyncEvent.Status.FINISHED);
                mEventManager.postEvent(syncEvent);
            }
        });
    }

    // use models convertion to loose coupling between network, storage and business layers

    private static PodcastInfoRow rowFromNetModel(PodcastInfoNet netModel) {
        PodcastInfoRow row = new PodcastInfoRow();
        row.setTitle(netModel.getTitle());
        row.setPublishedTimestamp(Utils.convertToTimestamp(netModel.getPublishedDate()));
        row.setDescription(netModel.getDescription());
        row.setSummary(netModel.getSummary());
        row.setAudioUrl(netModel.getAudioUrl());
        row.setImageUrl(netModel.getImageUrl());
        row.setLength(netModel.getLength());
        row.setFileSize(netModel.getFileSize());
        return row;
    }

    private static PodcastInfoModel modelFromRow(PodcastInfoRow row) {
        return new PodcastInfoModel(row);
    }
}
