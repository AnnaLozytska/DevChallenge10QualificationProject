package devchallenge.android.radiotplayer.event;

import java.util.List;

import devchallenge.android.radiotplayer.net.model.PodcastInfoNet;

public class PodcastsLoadedEvent extends Event {

    private List<PodcastInfoNet> podcasts;

    public PodcastsLoadedEvent(List<PodcastInfoNet> podcasts) {
        this.podcasts = podcasts;
    }

    public List<PodcastInfoNet> getPodcasts() {
        return podcasts;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + podcasts.size() + " podcasts loaded";
    }
}
