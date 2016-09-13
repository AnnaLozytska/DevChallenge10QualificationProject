package devchallenge.android.radiotplayer.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root
public class MediaInfoModel {

    @Attribute(name = "url")
    private String url;

    @Attribute(name = "fileSize")
    private long size;

    @Attribute(name = "type")
    private String type;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
