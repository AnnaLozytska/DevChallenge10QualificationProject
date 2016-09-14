package devchallenge.android.radiotplayer.util;

public class NotificationsManager {
    private static volatile NotificationsManager sInstance;

    public static NotificationsManager getInstance() {
        synchronized (NotificationsManager.class) {
            if (sInstance == null) {
                sInstance = new NotificationsManager();
            }
        }
        return sInstance;
    }

    private NotificationsManager() {
    }
}
