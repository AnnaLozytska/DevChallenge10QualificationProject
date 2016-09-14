package devchallenge.android.radiotplayer.net.sync;

import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import devchallenge.android.radiotplayer.event.EventManager;
import devchallenge.android.radiotplayer.event.PodcastsSyncEvent;
import devchallenge.android.radiotplayer.net.PodcastsInfoNetClient;
import devchallenge.android.radiotplayer.net.responce.PodcastsInfoResponse;
import retrofit.RetrofitError;

import static devchallenge.android.radiotplayer.event.PodcastsSyncEvent.Status;

public class PodcastsSyncService extends JobService {
    private static final String TAG = PodcastsSyncService.class.getSimpleName();

    private EventManager mEventManager;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        if (mEventManager == null) {
            mEventManager = EventManager.getInstance();
        }
        mEventManager.postEvent(new PodcastsSyncEvent(Status.STARTED));
        Log.d(TAG, "Run Feed sync");
        PodcastsInfoResponse response = null;
        try{
            response = PodcastsInfoNetClient.get().getPodcasts();
        } catch (RetrofitError error) {
            Log.e(TAG, "Sync failed. " + error.toString(), error);
            PodcastsSyncEvent syncEvent = new PodcastsSyncEvent(Status.FAILED);
            syncEvent.setError(error.toString());
            mEventManager.postEvent(syncEvent);
        }
        return false; /* if work is still being done*/
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        mEventManager.postEvent(new PodcastsSyncEvent(Status.CANCELLED));
        return false; // true or false depending on if we need to reschedule
    }
}
