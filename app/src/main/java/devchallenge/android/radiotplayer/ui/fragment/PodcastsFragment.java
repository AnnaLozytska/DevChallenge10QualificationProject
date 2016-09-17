package devchallenge.android.radiotplayer.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import devchallenge.android.radiotplayer.R;

public class PodcastsFragment extends Fragment {

    RecyclerView mPodcastsList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_podcasts, container, false);
        mPodcastsList = (RecyclerView) root.findViewById(R.id.podcasts);
        return root;
    }
}
