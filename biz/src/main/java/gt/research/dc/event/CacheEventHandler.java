package gt.research.dc.event;

import android.content.Context;

import gt.research.dc.core.classloader.ClassManager;
import gt.research.dc.core.classloader.command.CommandManager;
import gt.research.dc.core.classloader.component.ComponentManager;
import gt.research.dc.core.resource.ResourceManager;

/**
 * Created by ayi.zty on 2016/2/17.
 */
public class CacheEventHandler {
    public static void invalidateCache(Context context, String id) {
        ClassManager.getInstance().getCache().invalidate(context, id);
        ResourceManager resourceManager = ResourceManager.getInstance(context);
        if (null != resourceManager) {
            resourceManager.getCache().invalidate(context, id);
        }
        CommandManager.getInstance(context).invalidate(context, id);
        ComponentManager.getInstance(context).invalidate(context, id);
    }

    public static void invalidateAll(Context context) {
        ClassManager.getInstance().getCache().clear(context);
        ResourceManager resourceManager = ResourceManager.getInstance(context);
        if (null != resourceManager) {
            resourceManager.getCache().clear(context);
        }
        CommandManager.getInstance(context).clear(context);
        ComponentManager.getInstance(context).clear(context);
    }
}
