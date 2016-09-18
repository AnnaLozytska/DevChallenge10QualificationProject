package devchallenge.android.radiotplayer.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

import java.io.IOException;

import devchallenge.android.radiotplayer.event.EventManager;
import devchallenge.android.radiotplayer.event.PlayerUpdateEvent;
import devchallenge.android.radiotplayer.model.PodcastInfoModel;
import devchallenge.android.radiotplayer.util.PodcastQueueManager;


public class PlayerService extends Service implements AudioManager.OnAudioFocusChangeListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener {

    // Player's states:
    public static final int NONE = 0;
    public static final int BUFFERING = 1;
    public static final int PLAYING = 2;
    public static final int PAUSED = 3;
    public static final int COMPLETED = 4;
    public static final int STOPPED = 5;
    public static final int ERROR = -1;

    private static final String WIFI_LOCK = "radio_t_lock";

    private MediaPlayer mMediaPlayer;
    private int mState = NONE;
    private PodcastQueueManager mQueueManager;
    private PodcastInfoModel mCurrentPodcast;

    private AudioManager mAudioManager;
    private int mAudioFocus = AudioManager.AUDIOFOCUS_LOSS;
    private boolean mPausedOnAudioFocusLoss;

    private WifiManager.WifiLock mWifiLock;
    private EventManager mEventmanager;
    private Binder mBinder = new PlayerBinder();

    public static void bind(Context context, ServiceConnection connection) {
        Intent intent = new Intent(context, PlayerService.class);
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        this.mWifiLock = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, WIFI_LOCK);
        mQueueManager = PodcastQueueManager.getInstance();
        mEventmanager = EventManager.getInstance();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public PodcastInfoModel getCurrentPodcast() {
        return mCurrentPodcast;
    }

    public void play() {
        if (mCurrentPodcast == null) {
            mCurrentPodcast = mQueueManager.getFirstItem();
        }
        switch (mCurrentPodcast.getPlayingState()) {
            case PLAYING:
                break;
            case PAUSED:
            case COMPLETED:
                mMediaPlayer.start();
                break;
            case BUFFERING:
                // do nothing - audio is already loading and will start automatically
                break;
            default:
                relaxResources(true);
                requestAudioFocusIfNeeded();
                createMediaPlayerIfNeeded();
                updateStateAndNotify(BUFFERING);
                try {
                    mMediaPlayer.setDataSource(getApplicationContext(), mCurrentPodcast.getAudioUri());
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mMediaPlayer.prepareAsync();
                    mWifiLock.acquire();
                } catch (IOException ex) {
                    //TODO handle better
                    updateStateAndNotify(ERROR);
                }
        }
    }

    public void play(PodcastInfoModel podcast) {
        if (mCurrentPodcast != null && mCurrentPodcast.getTitle().equals(podcast.getTitle())) {
            play();
        } else {
            mCurrentPodcast = podcast;
            play();
        }
    }

    public void pause() {
        if (isPlaying()) {
            mMediaPlayer.pause();
        }
        relaxResources(false);
        releaseAudioFocus();
        updateStateAndNotify(PAUSED);
    }

    public void stop() {
        updateStateAndNotify(STOPPED);
        mCurrentPodcast = null;
        releaseAudioFocus();
        relaxResources(true);
        if (mWifiLock.isHeld()) {
            mWifiLock.release();
        }
    }

    public void next() {
        PodcastInfoModel next;
        if (mCurrentPodcast == null) {
            next = mQueueManager.getNextItem(mQueueManager.getFirstItem().toString());
        } else {
            next = mQueueManager.getNextItem(mCurrentPodcast.getTitle());
        }
        play(next);
    }

    public void previous() {
        PodcastInfoModel previous;
        if (mCurrentPodcast != null && mQueueManager.hasPrevious(mCurrentPodcast.getTitle())) {
            previous = mQueueManager.getNextItem(mQueueManager.getFirstItem().toString());
            play(previous);
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mMediaPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {

    }

    private void createMediaPlayerIfNeeded() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setWakeMode(getApplicationContext(),
                    PowerManager.PARTIAL_WAKE_LOCK);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnSeekCompleteListener(this);
        } else {
            mMediaPlayer.reset();
        }
    }

    private void relaxResources(boolean releaseMediaPlayer) {
        if (releaseMediaPlayer && mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mWifiLock.isHeld()) {
            mWifiLock.release();
        }
    }

    private void updateStateAndNotify(int newState) {
        mCurrentPodcast.setPlayingState(newState);
        mEventmanager.postEvent(new PlayerUpdateEvent(mCurrentPodcast));
    }

    private void requestAudioFocusIfNeeded() {
        if (mAudioFocus != AudioManager.AUDIOFOCUS_GAIN) {
            mAudioFocus = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
        }
    }

    private void releaseAudioFocus() {
        if (canPlay()) {
            mAudioFocus = mAudioManager.abandonAudioFocus(this);
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        mAudioFocus = focusChange;
        configMediaPlayerState();
    }

    private void configMediaPlayerState() {
        //TODO handle duck state
        if (!canPlay() && isPlaying()) {
            pause();
            mPausedOnAudioFocusLoss = true;
        } else {
            if (mState == PAUSED && mPausedOnAudioFocusLoss) {
                mMediaPlayer.start();
                updateStateAndNotify(PLAYING);
                mPausedOnAudioFocusLoss = false;
            }
        }
    }

    private boolean canPlay() {
        return mAudioFocus == AudioManager.AUDIOFOCUS_GAIN
                || mAudioFocus == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
                || mAudioFocus == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
                || mAudioFocus == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK
                || haveExclusiveAudioFocus();
    }

    private boolean haveExclusiveAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return mAudioFocus == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE;
        } else {
            return true;
        }
    }

    public boolean isPlaying() {
        return (mMediaPlayer != null && mMediaPlayer.isPlaying());
    }

    public class PlayerBinder extends Binder {
        public PlayerService getPlayer() {
            return PlayerService.this;
        }
    }
}
