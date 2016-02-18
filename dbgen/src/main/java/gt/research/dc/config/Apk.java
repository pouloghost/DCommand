package gt.research.dc.config;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "APK".
 */
public class Apk {

    /** Not-null value. */
    private String apk;
    /** Not-null value. */
    private String url;
    private String pkgName;
    private long timestamp;

    public Apk() {
    }

    public Apk(String apk) {
        this.apk = apk;
    }

    public Apk(String apk, String url, String pkgName, long timestamp) {
        this.apk = apk;
        this.url = url;
        this.pkgName = pkgName;
        this.timestamp = timestamp;
    }

    /** Not-null value. */
    public String getApk() {
        return apk;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setApk(String apk) {
        this.apk = apk;
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
