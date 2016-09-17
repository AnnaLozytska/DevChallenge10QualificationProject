package devchallenge.android.radiotplayer.repository;

import android.net.Uri;

import devchallenge.android.radiotplayer.model.PodcastInfoModel;

public class PersistentStorageManager {
    private static volatile PersistentStorageManager sInstance;

    public static PersistentStorageManager getInstance() {
        synchronized (PersistentStorageManager.class) {
            if (sInstance == null) {
                sInstance = new PersistentStorageManager();
            }
        }
        return sInstance;
    }

    private PersistentStorageManager() {

    }

    public DownloadStatus getItemStatus(PodcastInfoModel item) {
        // TODO
        return DownloadStatus.NOT_DOWNLOADED;
    }

    public Uri getItemLocalUri(PodcastInfoModel item) {
        // TODO: 17.09.16
        return null;
    }

    public enum DownloadStatus {
        NOT_DOWNLOADED,
        DOWNLOADING,
        DOWNLOADED,
        FAILED
    }
}
