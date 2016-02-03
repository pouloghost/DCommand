package gt.research.dc.core.command;

import android.util.LruCache;

import gt.research.dc.core.AbsCommand;
import gt.research.dc.data.CommandContext;
import gt.research.dc.util.CommandUtils;

/**
 * Created by ayi.zty on 2016/2/3.
 */
public class CommandCache {
    private LruCache<Class, Entry> mCache;

    public CommandCache() {
        mCache = new LruCache<>(6);
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

    private static class Entry {
        public CommandContext context;

        public Entry(CommandContext commandContext) {
            context = commandContext;
        }
    }
}
