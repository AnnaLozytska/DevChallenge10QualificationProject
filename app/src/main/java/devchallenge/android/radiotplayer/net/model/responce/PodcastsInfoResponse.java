package devchallenge.android.radiotplayer.net.model.responce;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

import devchallenge.android.radiotplayer.net.model.PodcastItem;

@Root(strict = false)
public class PodcastsInfoResponse {

    @Element(name = "channel")
    private Channel channel;

    public List<PodcastItem> getPodcasts() {
        return channel.getPodcastItems();
    }

    @Root(strict = false)
    public static class Channel {
        @ElementList(inline = true)
        private List<PodcastItem> podcastItems;

        public List<PodcastItem> getPodcastItems() {
            return podcastItems;
        }
    }
}
