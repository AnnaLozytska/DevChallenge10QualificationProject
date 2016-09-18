package devchallenge.android.radiotplayer.event;

import devchallenge.android.radiotplayer.util.PersistentStorageManager;

public class DownloadUpdateEvent extends Event {

    private String loadingItemTitle;
    private PersistentStorageManager.DownloadStatus status;
    private int downloaded;
    private int totalSize;

    public DownloadUpdateEvent(String loadingItemTitle, PersistentStorageManager.DownloadStatus status) {
        this(loadingItemTitle, status, 0, 0);
    }

    public DownloadUpdateEvent(String loadingItemTitle, PersistentStorageManager.DownloadStatus status,
                               int downloaded, int totalSize) {
        this.loadingItemTitle = loadingItemTitle;
        this.status = status;
        this.downloaded = downloaded;
        this.totalSize = totalSize;
    }

    public String getLoadingItemTitle() {
        return loadingItemTitle;
    }

    public PersistentStorageManager.DownloadStatus getStatus() {
        return status;
    }

    public int getDownloaded() {
        return downloaded;
    }

    public int getTotalSize() {
        return totalSize;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":"
                + " loading " + loadingItemTitle
                + ", status=" + status.name()
                + ", downloaded=" + downloaded
                + ", totalSize=" + totalSize;
    }
}
