package devchallenge.android.radiotplayer.event;

public class DownloadCommandEvent extends Event {

    private String title;
    private Command command;

    public DownloadCommandEvent(String title, Command command) {
        this.title = title;
        this.command = command;
    }

    public String getTitle() {
        return title;
    }

    public Command getCommand() {
        return command;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":"
                + " title=" + title
                + ", command=" + command.name();
    }

    public enum Command {
        DOWNLOAD,
        CANCEL,
        DELETE
    }
}
