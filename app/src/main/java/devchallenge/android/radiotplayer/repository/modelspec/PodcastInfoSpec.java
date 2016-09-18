package devchallenge.android.radiotplayer.repository.modelspec;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.TableModelSpec;

@TableModelSpec(className="PodcastInfoRow", tableName="podcastsInfo")
class PodcastInfoSpec {

    @ColumnSpec(constraints = "NOT NULL")
    String title;

    @ColumnSpec(defaultValue="0")
    long publishedTimestamp;

    @ColumnSpec(defaultValue="")
    String description;

    @ColumnSpec(defaultValue="")
    String summary;

    @ColumnSpec(defaultValue="")
    String audioUrl;

    @ColumnSpec(defaultValue="")
    String imageUrl;

    @ColumnSpec(defaultValue="0")
    int length;

    @ColumnSpec(defaultValue="0")
    int fileSize;

}
