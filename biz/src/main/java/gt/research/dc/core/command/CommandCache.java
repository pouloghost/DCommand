package gt.research.dc.core.command;

import android.text.TextUtils;

import gt.research.dc.core.AbsCommand;
import gt.research.dc.core.common.ICache;
import gt.research.dc.core.common.LruCacheMap;
import gt.research.dc.data.CommandContext;
import gt.research.dc.util.CommandUtils;

/**
 * Created by ayi.zty on 2016/2/3.
 */
public class CommandCache implements ICache {
    private LruCacheMap<Class, Entry> mCache;

    public CommandCache() {
        mCache = new LruCacheMap<>(6);
    }

    public <T extends AbsCommand> T getCachedCommand(Class<T> intf) {
        Entry entry = mCache.get(intf);
        if (null == entry) {
            return null;
        }
        return CommandUtils.constructCommand(intf, entry.context.apkInfo, entry.context.classLoader);
    }

    public <T extends AbsCommand> void onNewCommand(Class<T> intf, AbsCommand command) {
        mCache.put(intf, new Entry(command.getContext()));
    }

    @Override
    public void invalidate(String id) {
        for (Class clazz : mCache.keySet()){
            Entry entry = mCache.get(clazz);
            if(TextUtils.equals(entry.context.apkInfo.id, id)){
                mCache.remove(clazz);
            }
        }
    }

    @Override
    public void clear() {
        mCache.evictAll();
    }

    private static class Entry {
        public CommandContext context;

        public Entry(CommandContext commandContext) {
            context = commandContext;
        }
    }
}
