package devchallenge.android.radiotplayer.model;

import android.net.Uri;

import devchallenge.android.radiotplayer.repository.modelspec.PodcastInfoRow;

import static devchallenge.android.radiotplayer.util.PersistentStorageManager.DownloadStatus;


public class PodcastInfoModel {

    private PodcastInfoRow podcastInfoRow;
    private DownloadStatus downloadStatus;
    private Uri audioUri;
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
        if (audioUri == null) {
            audioUri = Uri.parse(podcastInfoRow.getAudioUrl());
        }
        return audioUri;
    }

    public Uri getImageUri() {
        if (podcastInfoRow.getImageUrl() != null) {
            return Uri.parse(podcastInfoRow.getImageUrl());
        }
        return null;
    }

    public long getFileSize() {
        return podcastInfoRow.getFileSize();
    }

    public DownloadStatus getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(DownloadStatus downloadState) {
        this.downloadStatus = downloadState;
    }

    public void setLocalAudioUri(Uri localUri) {
        audioUri = localUri;
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
                + ", totalDuration=" + getTotalDuration()
                + ", downloadStatus=" + getDownloadStatus()
                + ", pubTimestamp=" + getPublishedTimestamp()
                + ", audioUri=" + getAudioUri()
                + ", fileSize=" + getFileSize()
                + ", imageUri=" + getImageUri()
                + ", summary=" + getSummary()
                + ", description=" + getDescription().substring(0, 100); // description is too long to be displayed in logs;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof PodcastInfoModel)) {
            return false;
        }
        PodcastInfoModel model = (PodcastInfoModel) obj;
        return podcastInfoRow.equals(model.podcastInfoRow)
                && downloadStatus == model.getDownloadStatus()
                && playingState == model.getPlayingState()
                && currentPosition == model.getCurrentPosition()
                && totalDuration == model.getTotalDuration();
    }
}
