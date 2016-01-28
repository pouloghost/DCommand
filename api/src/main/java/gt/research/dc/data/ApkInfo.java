package gt.research.dc.data;

import java.util.HashMap;

/**
 * Created by ayi.zty on 2016/1/26.
 */
public class ApkInfo {
    public String version;
    public String url;
    public String id;
    public HashMap<String, String> interfaces;

    public boolean containsImplement(String intf) {
        return interfaces.containsKey(intf);
    }
    public String getImplement(String intf){
        return interfaces.get(intf);
    }
}

