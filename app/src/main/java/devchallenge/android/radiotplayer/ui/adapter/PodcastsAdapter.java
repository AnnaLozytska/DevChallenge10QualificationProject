package devchallenge.android.radiotplayer.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import devchallenge.android.radiotplayer.R;
import devchallenge.android.radiotplayer.model.PodcastInfoModel;
import devchallenge.android.radiotplayer.util.Utils;

public class PodcastsAdapter extends RecyclerView.Adapter<PodcastsAdapter.PodcastViewHolder> {

    private Context mContext;
    private List<PodcastInfoModel> mPodcasts;
    private Picasso mPicasso;

    public PodcastsAdapter(Context context) {
        mContext = context;
        mPicasso = Picasso.with(context);
        mPodcasts = Collections.emptyList();
    }

    public void swapItems(List<PodcastInfoModel> newItems) {
        mPodcasts = newItems;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mPodcasts.size();
    }

    @Override
    public PodcastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_podcast_info, parent, false);

        return new PodcastViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PodcastViewHolder holder, int position) {
        PodcastInfoModel podcast = mPodcasts.get(position);

        mPicasso.load(podcast.getImageUri())
                .resizeDimen(R.dimen.item_image_size, R.dimen.item_image_size)
                .centerCrop()
                .placeholder(R.drawable.item_placeholder)
                .into(holder.image);

        holder.title.setText(podcast.getTitle());

        String pubDate = Utils.formatTime(mContext, new Date(podcast.getPublishedTimestamp()));
        holder.pubDate.setText(pubDate);

        if (podcast.getSummary() != null) {
            Spanned summary = Html.fromHtml(podcast.getSummary());
            holder.summary.setText(summary);
        }
    }

    public static class PodcastViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        ImageView playing;
        TextView title;
        TextView summary;
        TextView pubDate;
        ImageButton download;

        public PodcastViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            playing = (ImageView) itemView.findViewById(R.id.playing);
            title = (TextView) itemView.findViewById(R.id.title);
            summary = (TextView) itemView.findViewById(R.id.summary);
            pubDate = (TextView) itemView.findViewById(R.id.pub_date);
            playing = (ImageButton) itemView.findViewById(R.id.download);

        }
    }
}
