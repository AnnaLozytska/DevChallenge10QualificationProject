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

import devchallenge.android.radiotplayer.service.PlayerService;


public abstract class PlayerBoundFragment extends Fragment {

    protected PlayerService mPlayer;
    protected boolean mIsPlayerBound = false;
    protected ServiceConnection mPlayerConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.PlayerBinder binder = (PlayerService.PlayerBinder)service;
            mPlayer = binder.getPlayer();
            mIsPlayerBound = true;
            onPlayerBound();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsPlayerBound = false;
            onPlayerBound();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        PlayerService.bind(getContext(), mPlayerConnection);
    }

    @Override
    public void onStop() {
        super.onStop();
        getContext().unbindService(mPlayerConnection);
    }

    protected abstract void onPlayerBound();
    protected abstract void onPlayerUnbound();
}
