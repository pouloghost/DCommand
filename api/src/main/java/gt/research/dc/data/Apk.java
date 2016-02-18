package gt.research.dc.data;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "APK".
 */
public class Apk {

    /** Not-null value. */
    private String id;
    /** Not-null value. */
    private String url;
    private String pkgName;
    private long timestamp;

    public Apk() {
    }

    public Apk(String id) {
        this.id = id;
    }

    public Apk(String id, String url, String pkgName, long timestamp) {
        this.id = id;
        this.url = url;
        this.pkgName = pkgName;
        this.timestamp = timestamp;
    }

    /** Not-null value. */
    public String getId() {
        return id;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setId(String id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getUrl() {
        return url;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setUrl(String url) {
        this.url = url;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
