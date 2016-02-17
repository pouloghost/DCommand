package gt.research.dc.core.command;

import android.content.Context;

import java.io.File;

import dalvik.system.DexClassLoader;
import gt.research.dc.core.AbsCommand;
import gt.research.dc.core.command.verifier.IApkVerifier;
import gt.research.dc.core.command.verifier.original.OriginalVerifier;
import gt.research.dc.core.common.ICache;
import gt.research.dc.core.config.ConfigManager;
import gt.research.dc.data.ApkInfo;
import gt.research.dc.util.ApkUtils;
import gt.research.dc.util.CommandUtils;
import gt.research.dc.util.FileUtils;
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
        ConfigManager.getInstance().getApkInfoByInterface(context, intf.getName(), new ConfigManager.LoadApkInfoListener() {
            @Override
            public void onApkInfoLoaded(final ApkInfo info) {
                if (null == info) {
                    LogUtils.debug("no info");
                    listener.onCommandLoaded(null);
                    return;
                }

                final File cacheDir = FileUtils.getCacheDir(context);
                final File apkFile = FileUtils.getCacheApkFile(context, info);
                Runnable afterLoad = new Runnable() {
                    @Override
                    public void run() {
                        if (!apkFile.exists()) {
                            LogUtils.debug("download fail");
                            listener.onCommandLoaded(null);
                            return;
                        }
                        DexClassLoader dexClassLoader = new DexClassLoader(apkFile.getAbsolutePath(),
                                cacheDir.getAbsolutePath(), null, context.getClassLoader());
                        T command = CommandUtils.constructCommand(intf, info, dexClassLoader);
                        mCache.onNewCommand(intf, command);
                        listener.onCommandLoaded(command);
                    }
                };
                ApkUtils.downloadAndVerifyApk(context, info, apkFile, afterLoad, mVerifier);
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
