package devchallenge.android.radiotplayer.net;

import devchallenge.android.radiotplayer.net.model.responce.PodcastsInfoResponse;
import retrofit.http.GET;

public interface PodcastsInfoApi {

    @GET("/radio-t")
    PodcastsInfoResponse getPodcasts();
}
