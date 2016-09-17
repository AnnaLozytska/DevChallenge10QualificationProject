package devchallenge.android.radiotplayer.model;

import android.net.Uri;

import devchallenge.android.radiotplayer.repository.modelspec.PodcastInfoRow;

public class PodcastInfoModel {
    private PodcastInfoRow podcastInfoRow;
    private boolean isSaved;
    private int state;
    private int currentPosition;
    private int totalDuration;

    public PodcastInfoModel(PodcastInfoRow podcastInfoRow) {
        this.podcastInfoRow = podcastInfoRow;
    }

    public void setPodcastInfoRow(PodcastInfoRow podcastInfoRow) {
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
        if (isSaved) {
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

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
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
}
