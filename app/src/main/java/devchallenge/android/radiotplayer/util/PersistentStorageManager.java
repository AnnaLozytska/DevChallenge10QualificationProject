package devchallenge.android.radiotplayer.util;

import android.net.Uri;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import devchallenge.android.radiotplayer.event.DownloadUpdateEvent;
import devchallenge.android.radiotplayer.event.EventManager;
import devchallenge.android.radiotplayer.model.PodcastInfoModel;

public class PersistentStorageManager {
    private static volatile PersistentStorageManager sInstance;
    private static int MAX_SIMULTANIOUS_DOWNLOADS = 5;
    private static String STORAGE_DIR = Environment.getExternalStorageDirectory() + "/audio";

    public static PersistentStorageManager getInstance() {
        synchronized (PersistentStorageManager.class) {
            if (sInstance == null) {
                sInstance = new PersistentStorageManager();
            }
        }
        return sInstance;
    }

    private ThreadPoolExecutor mExecutor;
    private EventManager mEventManager;

    private PersistentStorageManager() {
        mExecutor = new ThreadPoolExecutor(0, MAX_SIMULTANIOUS_DOWNLOADS,
                1, TimeUnit.MINUTES,
                new LinkedBlockingQueue<Runnable>());
        mEventManager = EventManager.getInstance();
    }

    public void downloadItemAsync(final PodcastInfoModel item) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                File dir = new File(STORAGE_DIR);
                dir.mkdirs();

                HttpURLConnection urlConnection = null;
                try {
                    URL audioUrl = new URL(item.getAudioUri().getPath());
                    urlConnection = (HttpURLConnection) audioUrl.openConnection();
                    urlConnection.setRequestMethod("GET");

                    BufferedInputStream bis = new BufferedInputStream(urlConnection.getInputStream());
                    int totalSize = urlConnection.getContentLength();
                    int current = 0;

                    item.setDownloadStatus(DownloadStatus.DOWNLOADING);
                    mEventManager.postEvent(new DownloadUpdateEvent(item, current, totalSize));

                    File audioFile = new File(dir, item.getTitle() + ".mp3");
                    FileOutputStream fileOutput = new FileOutputStream(audioFile);
                    int downloadedSize = 0;
                    while ((current = bis.read()) != -1) {
                        fileOutput.write(current);
                        downloadedSize += current;
                        // send updates only on each Mb to avoid redundant work related to posting
                        // and handling this event
                        if (downloadedSize % (1024 * 1024) == 0) {
                            mEventManager.postEvent(new DownloadUpdateEvent(item, downloadedSize, totalSize));
                        }
                    }
                    fileOutput.flush();
                    fileOutput.close();

                    item.setDownloadStatus(DownloadStatus.DOWNLOADED);
                    mEventManager.postEvent(new DownloadUpdateEvent(item));
                } catch (IOException e) {
                    e.printStackTrace();
                    item.setDownloadStatus(DownloadStatus.FAILED);
                    mEventManager.postEvent(new DownloadUpdateEvent(item));
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }
        });
    }


    public DownloadStatus getItemStatus(PodcastInfoModel item) {
        File file = new File(STORAGE_DIR, item.getTitle() + ".mp3" );
        if (file.exists()) {
            return DownloadStatus.DOWNLOADED;
        }
        return DownloadStatus.NOT_DOWNLOADED;
    }

    /**
     * Returns Uri of local podcast audio copy
     * @param item
     * @return
     * @throws FileNotFoundException if podcast hasn't been save. So make sure to check it beforehand
     *  by calling {@link #getItemStatus(PodcastInfoModel)}
     */
    public Uri getItemLocalUri(PodcastInfoModel item) throws FileNotFoundException {
        File file = new File(STORAGE_DIR, item.getTitle() + ".mp3" );
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        return Uri.fromFile(file);
    }

    public enum DownloadStatus {
        NOT_DOWNLOADED,
        DOWNLOADING,
        DOWNLOADED,
        FAILED
    }
}
