package devchallenge.android.radiotplayer.event;

import devchallenge.android.radiotplayer.model.PodcastInfoModel;

public class DownloadUpdateEvent extends Event {

    private PodcastInfoModel loadingItem;
    private int downloaded;
    private int totalSize;

    public DownloadUpdateEvent(PodcastInfoModel loadingItem) {
        this(loadingItem, 0, 0);
    }

    public DownloadUpdateEvent(PodcastInfoModel loadingItem, int downloaded, int totalSize) {
        this.loadingItem = loadingItem;
        this.downloaded = downloaded;
        this.totalSize = totalSize;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":"
                + " loading " + loadingItem.getTitle()
                + ", downloaded=" + downloaded
                + ", totalSize=" + totalSize;
    }
}
