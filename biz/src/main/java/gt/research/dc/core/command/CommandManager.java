package gt.research.dc.core.command;

import android.content.Context;

import java.io.File;

import dalvik.system.DexClassLoader;
import gt.research.dc.core.AbsCommand;
import gt.research.dc.core.command.verifier.IApkVerifier;
import gt.research.dc.core.command.verifier.OnVerifiedListener;
import gt.research.dc.core.command.verifier.original.OriginalVerifier;
import gt.research.dc.core.config.ConfigManager;
import gt.research.dc.core.constant.FileConstants;
import gt.research.dc.data.ApkInfo;
import gt.research.dc.data.CommandContext;
import gt.research.dc.util.FileUtils;
import gt.research.dc.util.LogUtils;
import gt.research.dc.util.NetUtils;

/**
 * Created by ayi.zty on 2016/1/26.
 */
public class CommandManager {
    private volatile static CommandManager sInstance;

    private IApkVerifier mVerifier;

    private CommandManager() {
        mVerifier = new OriginalVerifier();
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

                            CommandContext commandContext = new CommandContext();
                            commandContext.classLoader = dexClassLoader;
                            commandContext.apkInfo = new ApkInfo(info);
                            command.setContext(commandContext);

                            listener.onCommandLoaded(command);
                        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                            LogUtils.exception(e);
                            listener.onCommandLoaded(null);
                            return;
                        }
                    }
                };
                if (!info.isLatest || !apkFile.exists()) {
                    downloadApk(context, info, apkFile, afterLoad);
                    return;
                }
                afterLoad.run();
            }
        });
    }

    public void setVerifier(IApkVerifier mVerifier) {
        this.mVerifier = mVerifier;
    }

    private void downloadApk(final Context context, final ApkInfo apk, final File apkFile, final Runnable afterLoad) {
        NetUtils.download(context, apk.url, new NetUtils.DownloadListener() {
            @Override
            public void onEnqueue(String url) {

            }

            @Override
            public void onFinish(String url, String file) {
                onFileGot(context, file, apkFile, afterLoad);
            }

            @Override
            public void onCached(String url, String file) {
                onFileGot(context, file, apkFile, afterLoad);
            }

            @Override
            public void onFail() {
                if (null != afterLoad) {
                    afterLoad.run();
                }
            }
        });
    }

    private void onFileGot(Context context, final String file, final File apkFile, final Runnable afterLoad) {
        mVerifier.verify(context, file, new OnVerifiedListener() {
            @Override
            public void onVerified(boolean isSecure) {
                if (isSecure) {
                    FileUtils.copy(file, apkFile.getAbsolutePath());
                } else {
                    new File(file).delete();
                }
                if (null != afterLoad) {
                    afterLoad.run();
                }
            }
        });
    }

    public interface LoadCommandListener<T> {
        void onCommandLoaded(T command);
    }
}
