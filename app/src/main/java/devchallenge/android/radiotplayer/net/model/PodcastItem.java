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
public class PodcastItem {

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
    private String enclosureUrl;

    @Path("enclosure")
    @Attribute(name = "length")
    private String length;

    @Path("content")
    @Attribute(name = "fileSize")
    private long fileSize;

    private String imageUrl;

    public String getTitle() {
        return title;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }

    public String getLength() {
        return length;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getEnclosureUrl() {
        // workaround for the case when enclosure tag in not provided (yes, it happens!)
        if (enclosureUrl == null) {
            String audioRef = description.substring(description.indexOf("audio src=\"") + 11);
            int end = audioRef.indexOf(".mp3\"") + 4;
            enclosureUrl = audioRef.substring(0, end);
        }
        return enclosureUrl;
    }

    public String getImageUrl() {
        // workaround for getting podcast cover image as there is no separate tag for it (hopefully, yet)
        if (imageUrl == null) {
            imageUrl = description.substring(description.indexOf("img src=\"") + 9,
                    description.indexOf(".jpg\"") + 4);
        }
        return imageUrl;
    }
}
