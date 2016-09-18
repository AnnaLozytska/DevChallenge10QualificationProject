package devchallenge.android.radiotplayer.net.sync.task;

import android.util.Log;

import java.io.InterruptedIOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import devchallenge.android.radiotplayer.event.EventManager;
import devchallenge.android.radiotplayer.event.PodcastsLoadedEvent;
import devchallenge.android.radiotplayer.event.PodcastsSyncEvent;
import devchallenge.android.radiotplayer.net.PodcastsInfoNetClient;
import devchallenge.android.radiotplayer.net.model.responce.PodcastsInfoResponse;
import retrofit.RetrofitError;

public class PodcastsDownloadTaskAsync {
    private static final String TAG = PodcastsDownloadTaskAsync.class.getSimpleName();

    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    // as Retrofit 1 doesn't have requests cancellation, this flag indicates if RetrofitEError
    // caused by InterruptedIOException should be handled as real error or intended execution cancellation
    private boolean isPlannedTermination;

    public Future execute() {
        return mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                EventManager eventManager = EventManager.getInstance();
                eventManager.postEvent(new PodcastsSyncEvent(PodcastsSyncEvent.Status.STARTED));
                Log.i(TAG, "Started async Podcasts info download...");
                PodcastsInfoResponse response = null;
                try{
                    response = PodcastsInfoNetClient.get().getPodcasts();
                    if (response != null) {
                        eventManager.postEvent(new PodcastsLoadedEvent(response.getPodcasts()));
                    } else {
                        postFailedSync(eventManager, "Empty response");
                    }
                } catch (RetrofitError error) {
                    if (error.getCause() instanceof InterruptedIOException && isPlannedTermination) {
                        eventManager.postEvent(new PodcastsSyncEvent(PodcastsSyncEvent.Status.CANCELLED));
                    } else {
                        Log.e(TAG, "Sync failed. " + error.toString(), error);
                        postFailedSync(eventManager, error.toString());
                    }
                } finally {
                    mExecutor.shutdown();
                }
            }
        });

    }

    private void postFailedSync(EventManager eventManager, String error) {
        PodcastsSyncEvent syncEvent = new PodcastsSyncEvent(PodcastsSyncEvent.Status.FAILED);
        syncEvent.setError(error);
        eventManager.postEvent(syncEvent);
    }

    public void cancel() {
        if (!mExecutor.isShutdown()) {
            isPlannedTermination = true;
            mExecutor.shutdownNow();
            Log.i(TAG, "Task is cancelled: " + isCancelled());
        }
    }

    public boolean isCancelled() {
        return mExecutor.isShutdown();
    }
}
