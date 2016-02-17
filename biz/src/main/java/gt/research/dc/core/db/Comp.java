package gt.research.dc.core.db;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "COMP".
 */
public class Comp {

    /** Not-null value. */
    private String comp;
    /** Not-null value. */
    private String apk;
    /** Not-null value. */
    private String type;

    public Comp() {
    }

    public Comp(String comp) {
        this.comp = comp;
    }

    public Comp(String comp, String apk, String type) {
        this.comp = comp;
        this.apk = apk;
        this.type = type;
    }

    /** Not-null value. */
    public String getComp() {
        return comp;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setComp(String comp) {
        this.comp = comp;
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
    public String getType() {
        return type;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setType(String type) {
        this.type = type;
    }

}
