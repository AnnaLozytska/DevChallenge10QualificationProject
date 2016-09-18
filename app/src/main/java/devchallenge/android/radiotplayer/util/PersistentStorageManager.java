package devchallenge.android.radiotplayer.util;

import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.squareup.otto.Subscribe;

import java.io.BufferedInputStream;
import java.io.File;
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

import devchallenge.android.radiotplayer.event.DownloadCommandEvent;
import devchallenge.android.radiotplayer.event.DownloadUpdateEvent;
import devchallenge.android.radiotplayer.event.EventManager;

public class PersistentStorageManager {
    private static volatile PersistentStorageManager sInstance;
    private static int MAX_SIMULTANIOUS_DOWNLOADS = 5;
    private static final String STORAGE_DIR = "/radiot/audio";
    private static final String STORAGE_FULL_PATH =
            Environment.getExternalStorageDirectory().getAbsolutePath() + STORAGE_DIR;

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
        mExecutor = new ThreadPoolExecutor(MAX_SIMULTANIOUS_DOWNLOADS, MAX_SIMULTANIOUS_DOWNLOADS,
                1, TimeUnit.MINUTES,
                new LinkedBlockingQueue<Runnable>());
        mEventManager = EventManager.getInstance();
        mEventManager.registerEventListener(this);
    }

    public DownloadStatus getItemStatus(String itemTitle) {
        //TODO: DELETE AFTER TESTING:
        Log.d("---Test---", "Checking status for " + itemTitle);
        if (downloadingItems.containsKey(itemTitle)) {
            return DownloadStatus.DOWNLOADING;
        }

        File file = new File(STORAGE_FULL_PATH, removeSpaces(itemTitle) + ".mp3");
        if (file.exists()) {
            // check if file is completely downloaded
            int itemFileSize = PodcastQueueManager.getInstance().getItem(itemTitle).getFileSize();
            if (file.getTotalSpace() >= itemFileSize) {
                return DownloadStatus.DOWNLOADED;
            } else {
                // file is incomplete, download should be restarted
                file.delete();
                return DownloadStatus.NOT_DOWNLOADED;
            }
        }

        return DownloadStatus.NOT_DOWNLOADED;
    }

    /**
     * Returns Uri of local podcast audio copy
     *
     * @param itemTitle
     * @return
     */
    public Uri getItemLocalUri(String itemTitle) {
        File file = new File(STORAGE_FULL_PATH, removeSpaces(itemTitle) + ".mp3");
        if (file.exists()) {
            return Uri.fromFile(file);
        }
        return null;
    }

    @Subscribe
    public void onDownloadCommand(DownloadCommandEvent commandEvent) {
        String itemTitle = commandEvent.getTitle();

        //TODO: DELETE AFTER TESTING:
        Log.d("---Test---", "Received command " + commandEvent.getCommand() + " for title " + itemTitle);
        Log.d("---Test---", "Downloading items: " + downloadingItems.size());

        switch (commandEvent.getCommand()) {
            case DOWNLOAD:
                downloadItemAsync(itemTitle, PodcastQueueManager.getInstance().getItem(itemTitle).getAudioUri().toString());
                break;
            case CANCEL:
                boolean cancelled = cancelDownload(itemTitle);
                if (cancelled) {
                    mEventManager.postEvent(new DownloadUpdateEvent(itemTitle, DownloadStatus.NOT_DOWNLOADED));
                }
                break;
            case DELETE:
                deleteItem(itemTitle);
        }
    }

    private void downloadItemAsync(final String itemTitle, String downloadUrl) {
        //TODO: DELETE AFTER TESTING:
        Log.d("---Test---", "Starting download " + downloadUrl);
        final Future future = mExecutor.submit(getItemDownloadRunnable(itemTitle, downloadUrl));
        downloadingItems.put(itemTitle, future);
    }

    @NonNull
    private Runnable getItemDownloadRunnable(final String itemTitle, final String downloadUrl) {
        return new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                File audioFile = null;

                mEventManager.postEvent(
                        new DownloadUpdateEvent(itemTitle, DownloadStatus.DOWNLOADING));
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

                    File dir = new File(Environment.getExternalStorageDirectory(), STORAGE_DIR);
                    dir.mkdirs();
                    audioFile = new File(dir, removeSpaces(itemTitle) + ".mp3");

                    //TODO: DELETE AFTER TESTING:
                    Log.d("---Test---", "Creating file " + audioFile.getAbsolutePath());

                    if (audioFile.exists()) {
                        if (audioFile.getTotalSpace() >= totalSize) {
                            // file is already fully downloaded
                            mEventManager.postEvent(new DownloadUpdateEvent(itemTitle, DownloadStatus.DOWNLOADED));
                        } else {
                            // file is incomplete, restart download
                            audioFile.delete();
                            audioFile.createNewFile();
                        }
                    } else {
                        audioFile.createNewFile();
                    }

                    FileOutputStream fileOutput = new FileOutputStream(audioFile);
                    byte data[] = new byte[1024];

                    int downloadedSize = 0;
                    int downloadedMb = 1;
                    while ((current = bis.read(data)) != -1) {
                        fileOutput.write(data, 0, current);
                        downloadedSize+=current;
                        // send updates only on each Mb to avoid redundant work related to posting
                        // and handling this event
                        if ((downloadedSize / (1024 * 1024)) > downloadedMb) {
                            downloadedMb++;
                            mEventManager.postEvent(
                                    new DownloadUpdateEvent(itemTitle, DownloadStatus.DOWNLOADING,
                                            downloadedSize, totalSize));
                        }
                    }
                    fileOutput.flush();
                    fileOutput.close();
                    bis.close();

                    mEventManager.postEvent(
                            new DownloadUpdateEvent(itemTitle, DownloadStatus.DOWNLOADED));
                } catch (IOException e) {
                    e.printStackTrace();
                    if (audioFile != null) {
                        audioFile.delete();
                    }
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

    private boolean cancelDownload(String itemTitle) {
        //TODO: DELETE AFTER TESTING:
        Log.d("---Test---", "Cancelling " + itemTitle);

        if (downloadingItems.containsKey(itemTitle)) {

            Future itemFuture = downloadingItems.get(itemTitle);

            //TODO: DELETE AFTER TESTING:
            Log.d("---Test---", "Found in downloads. Future result isDone=" + itemFuture.isDone());

            if (itemFuture.isDone() || itemFuture.isCancelled()) {
                // download is already finished. Cleanup list if for some reason it is still there
                downloadingItems.remove(itemTitle);
                return true;
            } else {
                boolean cancelled = itemFuture.cancel(true);
                if (cancelled) {
                    File file = new File(STORAGE_FULL_PATH, removeSpaces(itemTitle));
                    if (file.exists()) {
                        file.delete();
                    }
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return true;
        }
    }

    private void deleteItem(String itemTitle) {
        //TODO: DELETE AFTER TESTING:
        Log.d("---Test---", "Deleting file " + itemTitle + ".mp3");

        boolean deleted = true;
        File file = new File(STORAGE_FULL_PATH, removeSpaces(itemTitle) + ".mp3");
        if (file.exists()) {
            deleted = file.delete();
            //TODO: DELETE AFTER TESTING:
        }
        Log.d("---Test---", "File deleted = " + deleted);
        if (deleted) {
            mEventManager.postEvent(new DownloadUpdateEvent(itemTitle, DownloadStatus.DELETED));
        }
    }

    private static String removeSpaces(String string) {
        return string.replace(" ", "");
    }

    public enum DownloadStatus {
        NOT_DOWNLOADED,
        DOWNLOADING,
        DOWNLOADED,
        FAILED,
        DELETED
    }
}
