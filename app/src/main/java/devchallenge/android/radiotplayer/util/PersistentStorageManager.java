package devchallenge.android.radiotplayer.util;

import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import devchallenge.android.radiotplayer.event.DownloadUpdateEvent;
import devchallenge.android.radiotplayer.event.EventManager;

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

    private Map<String, Future> downloadingItems;
    private ThreadPoolExecutor mExecutor;
    private EventManager mEventManager;

    private PersistentStorageManager() {
        downloadingItems = new HashMap<>();
        mExecutor = new ThreadPoolExecutor(0, MAX_SIMULTANIOUS_DOWNLOADS,
                1, TimeUnit.MINUTES,
                new LinkedBlockingQueue<Runnable>());
        mEventManager = EventManager.getInstance();
    }

    public void downloadItemAsync(final String itemTitle, String downloadUrl) {
        final Future future = mExecutor.submit(getItemDownloadRunnable(itemTitle, downloadUrl));
        downloadingItems.put(itemTitle, future);
    }

    @NonNull
    private Runnable getItemDownloadRunnable(final String itemTitle, final String downloadUrl) {
        return new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    URL audioUrl = new URL(downloadUrl);
                    urlConnection = (HttpURLConnection) audioUrl.openConnection();
                    urlConnection.setRequestMethod("GET");

                    BufferedInputStream bis = new BufferedInputStream(urlConnection.getInputStream());
                    int totalSize = urlConnection.getContentLength();
                    int current = 0;

                    mEventManager.postEvent(
                            new DownloadUpdateEvent(itemTitle, DownloadStatus.DOWNLOADING,
                                    current, totalSize));

                    File dir = new File(STORAGE_DIR);
                    dir.mkdirs();
                    File audioFile = new File(dir, itemTitle + ".mp3");
                    FileOutputStream fileOutput = new FileOutputStream(audioFile);
                    int downloadedSize = 0;
                    while ((current = bis.read()) != -1) {
                        fileOutput.write(current);
                        downloadedSize += current;
                        // send updates only on each Mb to avoid redundant work related to posting
                        // and handling this event
                        if (downloadedSize % (1024 * 1024) == 0) {
                            mEventManager.postEvent(
                                    new DownloadUpdateEvent(itemTitle, DownloadStatus.DOWNLOADING,
                                            downloadedSize, totalSize));
                        }
                    }
                    fileOutput.flush();
                    fileOutput.close();

                    mEventManager.postEvent(
                            new DownloadUpdateEvent(itemTitle, DownloadStatus.DOWNLOADED));
                } catch (IOException e) {
                    e.printStackTrace();
                    mEventManager.postEvent(
                            new DownloadUpdateEvent(itemTitle, DownloadStatus.FAILED));
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    downloadingItems.remove(itemTitle);
                }
            }
        };
    }

    public boolean cancelDownload(String itemTitle) {
        if (downloadingItems.containsKey(itemTitle)) {
            Future itemFuture = downloadingItems.get(itemTitle);
            if (itemFuture.isDone() || itemFuture.isCancelled()) {
                // cleanup list if for some reason it is still there
                downloadingItems.remove(itemTitle);
                return true;
            } else {
                return itemFuture.cancel(true);
            }
        } else {
            return true;
        }
    }

    public DownloadStatus getItemStatus(String itemTitle) {
        if (downloadingItems.containsKey(itemTitle)) {
            return DownloadStatus.DOWNLOADING;
        }

        File file = new File(STORAGE_DIR, itemTitle + ".mp3" );
        if (file.exists()) {
            return DownloadStatus.DOWNLOADED;
        }

        return DownloadStatus.NOT_DOWNLOADED;
    }

    /**
     * Returns Uri of local podcast audio copy
     * @param itemTitle
     * @return
     * @throws FileNotFoundException if podcast hasn't been save. So make sure to check it beforehand
     *  by calling {@link #getItemStatus(String)}
     */
    public Uri getItemLocalUri(String itemTitle) throws FileNotFoundException {
        File file = new File(STORAGE_DIR, itemTitle + ".mp3" );
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
