package devchallenge.android.radiotplayer.net.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

@Root(name="item", strict = false)
@NamespaceList({
        @Namespace(),
        @Namespace(reference = "http://www.itunes.com/dtds/podcast-1.0.dtd", prefix = "itunes"),
        @Namespace(reference = "http://search.yahoo.com/mrss/", prefix = "media")
})
public class PodcastInfoNet {

    @Element(name = "title")
    private String title;

    @Element(name = "pubDate")
    private String publishedDate;

    @Element(name = "description")
    private String description;

    @Element(name = "summary", required = false)
    private String summary;

    @Path("enclosure")
    @Attribute(name = "url")
    private String audioUrl;

    @Path("enclosure")
    @Attribute(name = "length")
    private int length;

    @Path("content")
    @Attribute(name = "fileSize")
    private long fileSize;

    public String getTitle() {
        return title;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public String getDescription() {
        return description;
    }

    public String getSummary() {
        return summary;
    }

    public String getAudioUrl() {
        // workaround for the case when enclosure tag in not provided (yes, it happens!)
        if (audioUrl == null) {
            String audioRef = description.substring(description.indexOf("audio src=\"") + 11);
            int end = audioRef.indexOf(".mp3\"") + 4;
            audioUrl = audioRef.substring(0, end);
        }
        return audioUrl;
    }

    public String getImageUrl() {
        // workaround for getting podcast cover image as there is no separate tag for it (hopefully, yet)
        return description.substring(description.indexOf("img src=\"") + 9, description.indexOf(".jpg\"") + 4);
    }

    public int getLength() {
        return length;
    }

    public long getFileSize() {
        return fileSize;
    }
}
