package devchallenge.android.radiotplayer.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class PodcastsAdapter extends RecyclerView.Adapter<PodcastsAdapter.PodcastViewHolder> {


    @Override
    public PodcastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(PodcastViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class PodcastViewHolder extends RecyclerView.ViewHolder {

        public PodcastViewHolder(View itemView) {
            super(itemView);
        }
    }
}
