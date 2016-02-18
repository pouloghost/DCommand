package gt.research.dc.core.command;

import android.content.Context;

import java.io.File;

import dalvik.system.DexClassLoader;
import gt.research.dc.core.AbsCommand;
import gt.research.dc.core.command.verifier.IApkVerifier;
import gt.research.dc.core.command.verifier.original.OriginalVerifier;
import gt.research.dc.core.common.ICache;
import gt.research.dc.core.config.ApkConfigManager;
import gt.research.dc.util.CommandUtils;
import gt.research.dc.util.LogUtils;

/**
 * Created by ayi.zty on 2016/1/26.
 */
public class CommandManager {
    private volatile static CommandManager sInstance;

    private IApkVerifier mVerifier;
    private CommandCache mCache;

    private CommandManager() {
        mVerifier = new OriginalVerifier();
        mCache = new CommandCache();
    }

    public static CommandManager getInstance() {
        if (null == sInstance) {
            synchronized (CommandManager.class) {
                if (null == sInstance) {
                    sInstance = new CommandManager();
                }
            }
        }
        return sInstance;
    }

    public <T extends AbsCommand> void getImplement(final Context context, final Class<T> intf,
                                                    boolean ignoreCache, final LoadCommandListener<T> listener) {
        if (null == listener) {
            return;
        }
        if (!ignoreCache) {
            T command = mCache.getCachedCommand(intf);
            if (null != command) {
                listener.onCommandLoaded(command);
                return;
            }
        }
        ApkConfigManager.getInstance().getApkInfoAndFileByInterface(context, intf.getName(), mVerifier,
                new ApkConfigManager.LoadApkInfoAndFileListener() {
                    @Override
                    public void onApkInfoAndFileListener(ApkInfo info, File apkFile) {
                        if (!apkFile.exists()) {
                            LogUtils.debug("download fail");
                            listener.onCommandLoaded(null);
                            return;
                        }
                        DexClassLoader dexClassLoader = new DexClassLoader(apkFile.getAbsolutePath(),
                                apkFile.getParent(), null, context.getClassLoader());
                        T command = CommandUtils.constructCommand(intf, info, dexClassLoader);
                        mCache.onNewCommand(intf, command);
                        listener.onCommandLoaded(command);
                    }
                });
    }

    public <T extends AbsCommand> void getImplement(final Context context, final Class<T> intf,
                                                    final LoadCommandListener<T> listener) {
        getImplement(context, intf, false, listener);
    }

    public void setVerifier(IApkVerifier mVerifier) {
        this.mVerifier = mVerifier;
    }

    public ICache getCache() {
        return mCache;
    }

    public interface LoadCommandListener<T> {
        void onCommandLoaded(T command);
    }
}
