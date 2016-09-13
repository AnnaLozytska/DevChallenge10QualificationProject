package devchallenge.android.radiotplayer.net;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public final class PodcastsInfoNetClient {
    private static volatile PodcastsInfoApi sApi;

    public static final String API_BASE_URL = "http://feeds.rucast.net";

    public static PodcastsInfoApi get() {
        synchronized (PodcastsInfoNetClient.class) {
            if (sApi == null) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(API_BASE_URL)
                        .client(new OkHttpClient())
                        .addConverterFactory(SimpleXmlConverterFactory.create())
                        .build();
                sApi = retrofit.create(PodcastsInfoApi.class);
            }
        }
        return sApi;
    }
}
