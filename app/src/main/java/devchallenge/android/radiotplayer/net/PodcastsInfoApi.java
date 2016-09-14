package devchallenge.android.radiotplayer.net;

import devchallenge.android.radiotplayer.net.responce.PodcastsInfoResponse;
import retrofit2.http.GET;

public interface PodcastsInfoApi {

    @GET("/radio-t")
    PodcastsInfoResponse getPodcasts();
}
