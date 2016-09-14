package devchallenge.android.radiotplayer.net.responce;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

import devchallenge.android.radiotplayer.model.PodcastInfoModel;

@Root(name = "channel")
public class PodcastsInfoResponse {

    @ElementList(inline=true)
    private List<PodcastInfoModel> podcastInfoModels;
}
