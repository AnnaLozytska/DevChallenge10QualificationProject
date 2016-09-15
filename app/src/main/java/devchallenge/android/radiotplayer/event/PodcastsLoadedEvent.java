package devchallenge.android.radiotplayer.event;

import java.util.List;

import devchallenge.android.radiotplayer.net.model.PodcastItem;

public class PodcastsLoadedEvent extends Event {

    private List<PodcastItem> podcasts;

    public PodcastsLoadedEvent(List<PodcastItem> podcasts) {
        this.podcasts = podcasts;
    }

    public List<PodcastItem> getPodcasts() {
        return podcasts;
    }
}
