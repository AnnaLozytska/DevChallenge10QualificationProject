package devchallenge.android.radiotplayer.net.model.responce;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

import devchallenge.android.radiotplayer.net.model.PodcastInfoNet;

@Root(strict = false)
public class PodcastsInfoResponse {

    @Element(name = "channel")
    private Channel channel;

    public List<PodcastInfoNet> getPodcasts() {
        return channel.getPodcastItems();
    }

    @Root(strict = false)
    public static class Channel {
        @ElementList(inline = true)
        private List<PodcastInfoNet> podcastItems;

        public List<PodcastInfoNet> getPodcastItems() {
            return podcastItems;
        }
    }
}
