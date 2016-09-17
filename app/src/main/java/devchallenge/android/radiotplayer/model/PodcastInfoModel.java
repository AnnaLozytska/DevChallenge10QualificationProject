package devchallenge.android.radiotplayer.model;

import android.net.Uri;

import devchallenge.android.radiotplayer.repository.modelspec.PodcastInfoRow;


public class PodcastInfoModel {
    public static final int NOT_DOWNLOADED = 0;
    public static final int DOWNLOADING = 1;
    public static final int DOWNLOADED = 2;
    public static final int FAILED = 3;

    private PodcastInfoRow podcastInfoRow;
    private int downloaded;
    /**
     * Represents states of {@link devchallenge.android.radiotplayer.service.PlayerService}
     * if this item is currently being played, or 0 otherwise.
     */
    private int playingState;
    private int currentPosition;
    private int totalDuration;

    public PodcastInfoModel(String title) {
        podcastInfoRow = new PodcastInfoRow();
        podcastInfoRow.setTitle(title);
    }

    public PodcastInfoModel(PodcastInfoRow podcastInfoRow) {
        this.podcastInfoRow = podcastInfoRow;
    }

    public String getTitle() {
        return podcastInfoRow.getTitle();
    }

    public long getPublishedTimestamp() {
        return podcastInfoRow.getPublishedTimestamp();
    }

    public String getDescription() {
        return podcastInfoRow.getDescription();
    }

    public String getSummary() {
        return podcastInfoRow.getSummary();
    }

    public Uri getAudioUri() {
        if (downloaded == DOWNLOADED) {
            // return local uri
        }
        return Uri.parse(podcastInfoRow.getAudioUrl());
    }

    public Uri getImageUri() {
        // if is cached, return local uri
        return Uri.parse(podcastInfoRow.getAudioUrl());
    }

    public long getFileSize() {
        return podcastInfoRow.getFileSize();
    }

    public int getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(int downloadState) {
        downloaded = downloadState;
    }

    public int getPlayingState() {
        return playingState;
    }

    public void setPlayingState(int playingState) {
        this.playingState = playingState;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public int getTotalDuration() {
        if (totalDuration == 0) {
            totalDuration = podcastInfoRow.getLength();
        }
        return totalDuration;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": "
                + "title=" + getTitle()
                + ", playingState=" + getPlayingState()
                + ", currentPosition=" + getCurrentPosition()
                + ", totalDuration" + getTotalDuration()
                + ", getDownloaded=" + getDownloaded()
                + ", pubTimestamp=" + getPublishedTimestamp()
                + ", summary=" + getSummary()
                + ", description" + getDescription().substring(0, 100) // description is too long to be displayed in logs
                + ", audioUri=" + getAudioUri()
                + ", imageUri=" + getImageUri()
                + ", fileSize=" + getFileSize();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof PodcastInfoModel)) {
            return false;
        }
        PodcastInfoModel model = (PodcastInfoModel) obj;
        return podcastInfoRow.equals(model.podcastInfoRow)
                && downloaded == model.getDownloaded()
                && playingState == model.getPlayingState()
                && currentPosition == model.getCurrentPosition()
                && totalDuration == model.getTotalDuration();
    }
}
