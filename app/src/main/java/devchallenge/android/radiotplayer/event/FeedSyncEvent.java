package devchallenge.android.radiotplayer.event;

public class FeedSyncEvent extends Event {

    private Status status;
    private boolean haveUpdates;

    public FeedSyncEvent(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public boolean hasUpdates() {
        return haveUpdates;
    }

    public void setHaveUpdates(boolean haveUpdates) {
        this.haveUpdates = haveUpdates;
    }

    public enum Status {
        STARTED,
        FINISHED,
        CANCELLED,
        FAILED,
        UNKNOWN
    }
}
