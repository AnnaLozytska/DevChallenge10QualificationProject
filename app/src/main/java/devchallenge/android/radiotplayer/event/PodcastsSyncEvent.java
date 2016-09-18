package devchallenge.android.radiotplayer.event;

public class PodcastsSyncEvent extends Event {

    private long eventTimestamp;
    private Status status;
    private String error;
    /**
     * Indicates if this event represents real updates from sync process
     * or is provided by {@link devchallenge.android.radiotplayer.util.SettingsManager} as last persisted event
     */
    private boolean isPersisted = false;

    public PodcastsSyncEvent(Status status) {
        this.status = status;
        eventTimestamp = System.currentTimeMillis();
    }

    public long getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(long eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isPersisted() {
        return isPersisted;
    }

    public void setPersisted(boolean persisted) {
        isPersisted = persisted;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": status=" + status.name()
                + ", error=" + error
                + ", isPersisted=" + isPersisted;
    }

    public enum Status {
        STARTED,
        FINISHED,
        CANCELLED,
        FAILED,
        UNKNOWN
    }
}
