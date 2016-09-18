package devchallenge.android.radiotplayer.ui.fragment;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import devchallenge.android.radiotplayer.R;
import devchallenge.android.radiotplayer.event.EventManager;
import devchallenge.android.radiotplayer.service.PlayerService;


public class MediaPlayerFragment extends Fragment {

    private ImageButton mPrevious;
    private ImageButton mPlayPause;
    private ImageButton mNext;
    private TextView mPlayingTitle;
    private ImageView mImage;

    private PlayerService mPlayer;
    boolean mIsPlayerBound = false;
    private ServiceConnection mPlayerConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.PlayerBinder binder = (PlayerService.PlayerBinder)service;
            mPlayer = binder.getPlayer();
            mIsPlayerBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsPlayerBound = false;
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_player, container, false);

        mPrevious = (ImageButton) root.findViewById(R.id.previous);
        mPlayPause = (ImageButton) root.findViewById(R.id.play_pause);
        mNext = (ImageButton) root.findViewById(R.id.next);
        mPlayingTitle = (TextView) root.findViewById(R.id.playing_title);
        mImage = (ImageView) root.findViewById(R.id.image);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventManager.getInstance().registerEventListener(this);
        PlayerService.bind(getContext(), mPlayerConnection);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventManager.getInstance().unregisterEventListener(this);
        getContext().unbindService(mPlayerConnection);
    }
}
