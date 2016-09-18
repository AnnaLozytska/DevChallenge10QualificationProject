package devchallenge.android.radiotplayer.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import devchallenge.android.radiotplayer.R;
import devchallenge.android.radiotplayer.model.PodcastInfoModel;
import devchallenge.android.radiotplayer.ui.adapter.PodcastsAdapter;
import devchallenge.android.radiotplayer.util.PodcastQueueManager;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class PodcastsFragment extends Fragment {

    RecyclerView mPodcastsList;
    PodcastsAdapter mAdapter;
    Subscription mQueueUpdates;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_podcasts, container, false);
        mPodcastsList = (RecyclerView) root.findViewById(R.id.podcasts);
        mPodcastsList.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new PodcastsAdapter(getContext());
        mPodcastsList.setAdapter(mAdapter);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        mQueueUpdates = PodcastQueueManager.getInstance()
                .getPodcastQueueUpdates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onBackpressureLatest()
                .subscribe(new Action1<List<PodcastInfoModel>>() {
                    @Override
                    public void call(List<PodcastInfoModel> podcasts) {
                        mAdapter.swapItems(podcasts);
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!mQueueUpdates.isUnsubscribed()) {
            mQueueUpdates.unsubscribe();
        }
    }
}
