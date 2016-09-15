package devchallenge.android.radiotplayer.net.sync.task;

import android.os.AsyncTask;
import android.util.Log;

import devchallenge.android.radiotplayer.event.EventManager;
import devchallenge.android.radiotplayer.event.PodcastsLoadedEvent;
import devchallenge.android.radiotplayer.event.PodcastsSyncEvent;
import devchallenge.android.radiotplayer.net.PodcastsInfoNetClient;
import devchallenge.android.radiotplayer.net.model.responce.PodcastsInfoResponse;
import retrofit.RetrofitError;

// FIXME: can't be cancelled, as well as Retrofit request :( Think of better implementation
public class PodcastsDownloadTask extends AsyncTask<Void, Void, Void>{
    private static final String TAG = PodcastsDownloadTask.class.getSimpleName();

    @Override
    protected Void doInBackground(Void... voids) {
        EventManager eventManager = EventManager.getInstance();
        eventManager.postEvent(new PodcastsSyncEvent(PodcastsSyncEvent.Status.STARTED));
        Log.i(TAG, "Started Podcasts info download...");
        PodcastsInfoResponse response = null;
        try{
            response = PodcastsInfoNetClient.get().getPodcasts();
            if (response != null) {
                eventManager.postEvent(new PodcastsLoadedEvent(response.getPodcasts()));
            } else {
                postFailedSync(eventManager, "Empty response");
            }
        } catch (RetrofitError error) {
            Log.e(TAG, "Sync failed. " + error.toString(), error);
            postFailedSync(eventManager, error.toString());
        }
        return null;
    }

    private void postFailedSync(EventManager eventManager, String error) {
        PodcastsSyncEvent syncEvent = new PodcastsSyncEvent(PodcastsSyncEvent.Status.FAILED);
        syncEvent.setError(error);
        eventManager.postEvent(syncEvent);
    }
}
