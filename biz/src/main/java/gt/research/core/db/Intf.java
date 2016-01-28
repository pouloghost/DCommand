package gt.research.core.db;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "INTF".
 */
public class Intf {

    /** Not-null value. */
    private String intf;
    /** Not-null value. */
    private String impl;
    /** Not-null value. */
    private String apk;

    public Intf() {
    }

    public Intf(String intf) {
        this.intf = intf;
    }

    public Intf(String intf, String impl, String apk) {
        this.intf = intf;
        this.impl = impl;
        this.apk = apk;
    }

    /** Not-null value. */
    public String getIntf() {
        return intf;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setIntf(String intf) {
        this.intf = intf;
    }

    /** Not-null value. */
    public String getImpl() {
        return impl;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setImpl(String impl) {
        this.impl = impl;
    }

    /** Not-null value. */
    public String getApk() {
        return apk;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setApk(String apk) {
        this.apk = apk;
    }

}
