package gt.research.dc.data;

import java.util.HashMap;

/**
 * Created by ayi.zty on 2016/1/26.
 */
public class ApkInfo {
    public String version;
    public String url;
    public String id;
    public boolean isLatest;
    public String pkgName;
    public HashMap<String, String> interfaces;

    public ApkInfo() {

    }

    public ApkInfo(ApkInfo src) {
        version = src.version;
        url = src.url;
        id = src.id;
        isLatest = src.isLatest;
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

