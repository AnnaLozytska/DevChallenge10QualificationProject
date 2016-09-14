package devchallenge.android.radiotplayer.event;

public class PodcastsSyncEvent extends Event {

    private Status status;
    private boolean haveUpdates;
    private String error;

    public PodcastsSyncEvent(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public boolean haveUpdates() {
        return haveUpdates;
    }

    public void setHaveUpdates(boolean haveUpdates) {
        this.haveUpdates = haveUpdates;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return PodcastsSyncEvent.class.getSimpleName() + ": status=" + status.name()
                + ", haveUpdates=" + haveUpdates
                + ", error=" + error;
    }

    public enum Status {
        STARTED,
        FINISHED,
        CANCELLED,
        FAILED,
        UNKNOWN
    }
}
