package devchallenge.android.radiotplayer.repository;

import android.util.Log;

import com.squareup.otto.Subscribe;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.data.SquidDatabase;
import com.yahoo.squidb.sql.Query;

import java.util.concurrent.Executors;

import devchallenge.android.radiotplayer.event.EventManager;
import devchallenge.android.radiotplayer.event.PodcastsLoadedEvent;
import devchallenge.android.radiotplayer.event.PodcastsSyncEvent;
import devchallenge.android.radiotplayer.net.model.PodcastInfoNet;
import devchallenge.android.radiotplayer.net.sync.SyncManager;
import devchallenge.android.radiotplayer.repository.modelspec.PodcastInfoRow;
import devchallenge.android.radiotplayer.util.Utils;

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

    @Subscribe
    public void onPodcastsLoaded(final PodcastsLoadedEvent event) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                //TODO: DELETE AFTER TESTING:
                Log.d("---Test---", "Received loaded data in provider...");

                boolean haveUpdates;

                for (PodcastInfoNet netModel : event.getPodcasts()) {
                    PodcastInfoRow syncRow = rowFromNetModel(netModel);
                    Query selectByTitle = Query.select().where(PodcastInfoRow.TITLE.eq(syncRow.getTitle()));
                    SquidCursor<PodcastInfoRow> cursor = mDatabase.query(PodcastInfoRow.class, selectByTitle);
                    if (cursor.moveToFirst()) {
                        PodcastInfoRow rowInDb = new PodcastInfoRow();
                        rowInDb.readPropertiesFromCursor(cursor);
                        if (!syncRow.equals(rowInDb)) {

                        }
                    } else {

                    }
                }

                PodcastsSyncEvent syncEvent = new PodcastsSyncEvent(PodcastsSyncEvent.Status.FINISHED);
                mEventManager.postEvent(syncEvent);

            }
        });
    }

    public void startManualSync() {
        mSyncManager.syncPodcastsManualy();
    }

    public void cancelSync() {
        mSyncManager.cancelCurrentPodcastsSync();
    }

    private static PodcastInfoRow rowFromNetModel(PodcastInfoNet netModel) {
        PodcastInfoRow row = new PodcastInfoRow();
        row.setTitle(netModel.getTitle());
        row.setPublishedDate(Utils.convertToTimestamp(netModel.getPublishedDate()));
        row.setDescription(netModel.getDescription());
        row.setSummary(netModel.getSummary());
        row.setAudioUrl(netModel.getAudioUrl());
        row.setImageUrl(netModel.getImageUrl());
        row.setLength(netModel.getLength());
        row.setFileSize(netModel.getFileSize());
        return row;
    }
}
