package devchallenge.android.radiotplayer.net.sync;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import devchallenge.android.radiotplayer.event.EventManager;
import devchallenge.android.radiotplayer.event.PodcastsSyncEvent;
import devchallenge.android.radiotplayer.net.sync.task.PodcastsDownloadTaskAsync;

public class PodcastsSyncService extends JobService {
    private static final String TAG = PodcastsSyncService.class.getSimpleName();

    private PodcastsDownloadTaskAsync downloadTask;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        downloadTask = new PodcastsDownloadTaskAsync();
        downloadTask.execute();
        return true; /* work is still being done*/
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if (downloadTask != null) {
            downloadTask.cancel();
        }
        EventManager.getInstance().postEvent(new PodcastsSyncEvent(PodcastsSyncEvent.Status.CANCELLED));
        return true; // true or false depending on if we need to reschedule
    }
}
