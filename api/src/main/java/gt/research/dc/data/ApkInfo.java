package gt.research.dc.data;

import java.util.HashMap;

/**
 * Created by ayi.zty on 2016/1/26.
 */
public class ApkInfo {
    public String url;
    public String id;
    public String pkgName;
    public HashMap<String, String> interfaces;
    public long timestamp;

    public ApkInfo() {

    }

    public ApkInfo(ApkInfo src) {
        pkgName = src.pkgName;
        timestamp = src.timestamp;
        url = src.url;
        id = src.id;
        interfaces = new HashMap<>(src.interfaces);
    }

    public boolean containsImplement(String intf) {
        return interfaces.containsKey(intf);
    }

    public String getImplement(String intf) {
        return interfaces.get(intf);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

