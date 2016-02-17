package gt.research.dc.util;

import gt.research.dc.core.command.CommandManager;
import gt.research.dc.core.resource.ResourceManager;

/**
 * Created by ayi.zty on 2016/2/17.
 */
public class CacheUtils {
    public static void invalidateCache(String id) {
        CommandManager.getInstance().getCache().invalidate(id);
        ResourceManager resourceManager = ResourceManager.getInstance(null, null);
        if (null != resourceManager) {
            resourceManager.getCache().invalidate(id);
        }
    }
    public static void invalidateAll(){
        CommandManager.getInstance().getCache().clear();
        ResourceManager resourceManager = ResourceManager.getInstance(null, null);
        if (null != resourceManager) {
            resourceManager.getCache().clear();
        }
    }
}
