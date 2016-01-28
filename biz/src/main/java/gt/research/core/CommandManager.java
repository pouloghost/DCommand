package gt.research.core;

import android.content.Context;

import java.io.File;

import dalvik.system.DexClassLoader;
import gt.research.dc.core.AbsCommand;
import gt.research.dc.core.FileConstants;
import gt.research.dc.data.ApkInfo;
import gt.research.util.FileUtils;
import gt.research.util.LogUtils;
import gt.research.util.NetUtils;

/**
 * Created by ayi.zty on 2016/1/26.
 */
public class CommandManager {
    private volatile static CommandManager sInstance;

    private CommandManager() {

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

    public <T extends AbsCommand> void getImplement(final Context context, final Class<T> intf, final LoadCommandListener<T> listener) {
        if (null == listener) {
            return;
        }
        ConfigManager.getInstance().getApk(context, intf.getName(), new ConfigManager.LoadApkInfoListener() {
            @Override
            public void onApkLoaded(final ApkInfo info) {
                if (null == info) {
                    LogUtils.debug("no info");
                    listener.onCommandLoaded(null);
                    return;
                }

                final File cacheDir = context.getDir(FileConstants.DIR_DOWNLOAD, Context.MODE_PRIVATE);
                final File apkFile = new File(cacheDir, info.id + FileConstants.SUFFIX_APK);
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
                        try {
                            T command = (T) dexClassLoader.loadClass(info.getImplement(intf.getName())).newInstance();
                            //// TODO: 2016/1/27
                            command.setContext(null);
                            listener.onCommandLoaded(command);
                        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                            LogUtils.exception(e);
                            listener.onCommandLoaded(null);
                            return;
                        }
                    }
                };
                if (!apkFile.exists()) {
                    downloadApk(context, info, apkFile, afterLoad);
                    return;
                }
                afterLoad.run();
            }
        });
    }

    private void downloadApk(Context context, final ApkInfo apk, final File apkFile, final Runnable afterLoad) {
        NetUtils.download(context, apk.url, new NetUtils.DownloadListener() {
            @Override
            public void onEnqueue(String url) {

            }

            @Override
            public void onFinish(String url, String file) {
                onFileGot(file, apkFile, afterLoad);
            }

            @Override
            public void onCached(String url, String file) {
                onFileGot(file, apkFile, afterLoad);
            }
        });
    }

    private void onFileGot(String file, File apkFile, Runnable afterLoad) {
        FileUtils.copy(file, apkFile.getAbsolutePath());
        if (null != afterLoad) {
            afterLoad.run();
        }
    }

    public interface LoadCommandListener<T> {
        void onCommandLoaded(T command);
    }
}
