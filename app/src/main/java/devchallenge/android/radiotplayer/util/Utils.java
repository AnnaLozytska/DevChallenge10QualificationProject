package devchallenge.android.radiotplayer.util;

import android.content.Context;
import android.os.Looper;
import android.text.format.DateUtils;

import java.util.Collection;
import java.util.Date;

import devchallenge.android.radiotplayer.R;

public class Utils {

    private static class MainThreadReferenceHolder {
        static final Thread MAIN_THREAD = getMainThread();

        private static Thread getMainThread() {
            try {
                return Looper.getMainLooper().getThread();
            } catch (RuntimeException ex) {
                if (ex.getMessage() != null && ex.getMessage().contains("not mocked")) {
                    return null;
                }
                throw ex;
            }
        }
    }

    public static final boolean isRunningOnMainThread() {
        return Thread.currentThread() == MainThreadReferenceHolder.MAIN_THREAD;
    }

    public static final boolean isEmpty(Collection<?> list) {
        return list == null || list.size() == 0;
    }

    public static String formatTime(Context context, Date date) {
        long dateInMillis = date.getTime();
        long justNow = System.currentTimeMillis() - DateUtils.MINUTE_IN_MILLIS;

        if (dateInMillis >= justNow) {
            return context.getResources().getString(R.string.just_now);
        } else {
            return getRelativeTime(date);
        }
    }

    private static String getRelativeTime(Date date) {
        return DateUtils.getRelativeTimeSpanString(date.getTime(), System.currentTimeMillis(),
               DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
    }

}
