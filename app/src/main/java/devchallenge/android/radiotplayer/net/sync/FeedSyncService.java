package devchallenge.android.radiotplayer.net.sync;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class FeedSyncService extends JobService {

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        // Begin some async work
        /*asyncTask = new AsyncTask<Object, Object, Object>() {
            protected Object doInBackground(Object... objects) {
                *//* do some work *//*
            }

            protected void onPostExecute(Object result) {
                jobFinished(job, false *//* no need to reschedule, we're done *//*);
            }
        };*/
        return true; /* if work is still being done*/
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false; // true or false depending on if wee need to reschedule
    }
}
