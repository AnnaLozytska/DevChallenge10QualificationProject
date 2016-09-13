package devchallenge.android.radiotplayer.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="item")
public class PodcastInfoModel {

    @Element(name = "title")
    private String title;

    @Element(name = "pubDate")
    private String publishedDate;

    @Element(name = "itunes:subtitle")
    private String subtitle;

    @Element(name = "author")
    private String author;

    @Element(name = "description")
    private String description;

    @Element(name = "media:content")
    private MediaInfoModel mediaInfo;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MediaInfoModel getMediaInfo() {
        return mediaInfo;
    }

    public void setMediaInfo(MediaInfoModel mediaInfo) {
        this.mediaInfo = mediaInfo;
    }
}
