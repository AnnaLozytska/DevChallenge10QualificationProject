package devchallenge.android.radiotplayer.net;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.SimpleXMLConverter;

public final class PodcastsInfoNetClient {
    private static volatile PodcastsInfoApi sApi;

    public static final String API_BASE_URL = "http://feeds.rucast.net";

    public static PodcastsInfoApi get() {
        synchronized (PodcastsInfoNetClient.class) {
            if (sApi == null) {
                RestAdapter.Builder builder = new RestAdapter.Builder()
                        .setEndpoint(API_BASE_URL)
                        .setClient(new OkClient())
                        .setConverter(new SimpleXMLConverter())
                        .setLogLevel(RestAdapter.LogLevel.FULL);
                RestAdapter restAdapter = builder.build();
                sApi = restAdapter.create(PodcastsInfoApi.class);
            }
        }
        return sApi;
    }
}
