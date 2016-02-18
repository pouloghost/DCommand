package gt.research.dc.event;

import gt.research.dc.core.command.CommandManager;
import gt.research.dc.core.resource.ResourceManager;

/**
 * Created by ayi.zty on 2016/2/17.
 */
public class CacheEventHandler {
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
