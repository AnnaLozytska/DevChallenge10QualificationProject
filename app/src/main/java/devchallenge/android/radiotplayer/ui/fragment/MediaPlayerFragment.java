package devchallenge.android.radiotplayer.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import devchallenge.android.radiotplayer.R;
import devchallenge.android.radiotplayer.event.EventManager;
import devchallenge.android.radiotplayer.event.PlayerUpdateEvent;
import devchallenge.android.radiotplayer.model.PodcastInfoModel;
import devchallenge.android.radiotplayer.service.PlayerService;
import devchallenge.android.radiotplayer.util.PodcastQueueManager;


public class MediaPlayerFragment extends PlayerBoundFragment {

    private ImageButton mPrevious;
    private ImageButton mPlayPause;
    private ImageButton mNext;
    private TextView mPlayingTitle;
    private TextView mTime;
    private ImageView mImage;

    private PodcastInfoModel mCurrentPodcast;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_player, container, false);

        mPrevious = (ImageButton) root.findViewById(R.id.previous);
        mPlayPause = (ImageButton) root.findViewById(R.id.play_pause);
        mNext = (ImageButton) root.findViewById(R.id.next);
        mPlayingTitle = (TextView) root.findViewById(R.id.playing_title);
        mTime = (TextView) root.findViewById(R.id.time);
        mImage = (ImageView) root.findViewById(R.id.image);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventManager.getInstance().registerEventListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventManager.getInstance().unregisterEventListener(this);
    }

    @Override
    protected void onPlayerBound() {
        configureViews(mPlayer.getCurrentPodcast());
    }

    @Subscribe
    public void omPlayeUpdateEvent(PlayerUpdateEvent updateEvent) {
        PodcastInfoModel updated = updateEvent.getPlayingPodcast();
        if (mCurrentPodcast != null
                && (updated.getTitle().equals(mCurrentPodcast.getTitle())
                || (updated.getPlayingState() == mCurrentPodcast.getPlayingState()))) {
            return;
        }
        configureViews(updated);
    }

    private void configureViews(PodcastInfoModel playingPodcast) {
        mCurrentPodcast = playingPodcast;
        final PodcastQueueManager queueManager = PodcastQueueManager.getInstance();
        if (mCurrentPodcast != null) {
            Picasso.with(getContext())
                    .load(mCurrentPodcast.getImageUri())
                    .resizeDimen(R.dimen.cover_image_size, R.dimen.cover_image_size)
                    .centerCrop()
                    .placeholder(R.drawable.item_placeholder)
                    .into(mImage);
            mPlayingTitle.setText(mCurrentPodcast.getTitle());

            if (queueManager.hasPrevious(mCurrentPodcast.getTitle())) {
                enablePrevious();
            } else {
                mPrevious.setEnabled(false);
            }

            if (mCurrentPodcast.getPlayingState() == PlayerService.PLAYING
                    || mCurrentPodcast.getPlayingState() == PlayerService.BUFFERING) {
                mPlayPause.setImageResource(R.drawable.ic_pause);
                mPlayPause.setOnClickListener(getPauseClickListener());
            } else {
                mPlayPause.setImageResource(R.drawable.ic_play);
                mPlayPause.setOnClickListener(getPlayClickListener());
            }

            if (queueManager.hasNext(mCurrentPodcast.getTitle())) {
                enableNext();
            } else {
                mNext.setEnabled(false);
            }
        } else {
            mPrevious.setEnabled(false);
            if (queueManager.hasPodcasts()) {
                PodcastInfoModel firstPodcast = queueManager.getFirstItem();
                mPlayingTitle.setText(firstPodcast.getTitle());
                mPlayPause.setEnabled(true);
                mPlayPause.setOnClickListener(getPlayClickListener());
                if (queueManager.hasNext(firstPodcast.getTitle())) {
                    enableNext();
                }
            }
        }
    }

    private View.OnClickListener getPlayClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.play();
                mPlayPause.setImageResource(R.drawable.ic_pause);
                mPlayPause.setOnClickListener(getPauseClickListener());
            }
        };
    }

    private View.OnClickListener getPauseClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.pause();
                mPlayPause.setImageResource(R.drawable.ic_play);
                mPlayPause.setOnClickListener(getPlayClickListener());
            }
        };
    }

    private void enablePrevious() {
        mPrevious.setEnabled(true);
        mPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.previous();
            }
        });
    }

    private void enableNext() {
        mNext.setEnabled(true);
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.next();
            }
        });

    }

    @Override
    protected void onPlayerUnbound() {
        PlayerService.bind(getContext(), mPlayerConnection);

    }
}
