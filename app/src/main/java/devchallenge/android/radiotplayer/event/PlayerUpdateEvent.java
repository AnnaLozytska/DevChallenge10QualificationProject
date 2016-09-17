package devchallenge.android.radiotplayer.event;

import devchallenge.android.radiotplayer.model.PodcastInfoModel;

public class PlayerUpdateEvent extends Event {

    private PodcastInfoModel playingPodcast;

    public PlayerUpdateEvent(PodcastInfoModel playingPodcast) {
        this.playingPodcast = playingPodcast;
    }

    @Override
    public String toString() {
        return getClass().toString() + ": " + playingPodcast.toString();
    }
}
