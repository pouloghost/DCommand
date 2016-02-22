package gt.research.dc.core.classloader.command;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import gt.research.dc.core.AbsCommand;
import gt.research.dc.core.CommandContext;
import gt.research.dc.core.classloader.ClassFetcher;
import gt.research.dc.core.classloader.ClassManager;
import gt.research.dc.core.common.ICache;
import gt.research.dc.core.common.manifest.Manifest;
import gt.research.dc.core.db.Apk;
import gt.research.dc.core.db.DbManager;
import gt.research.dc.core.db.Intf;
import gt.research.dc.core.db.IntfDao;
import gt.research.dc.event.IOnNewApkListener;
import gt.research.dc.util.LogUtils;

/**
 * Created by ayi.zty on 2016/1/26.
 */
public class CommandManager implements IOnNewApkListener, ICache {
    //id:interface-> details
    private Map<String, Intf> mInterfaceMap;

    private volatile static CommandManager sInstance;

    private CommandManager(Context context) {
        loadDb(context);
    }

    public static CommandManager getInstance(Context context) {
        if (null == sInstance) {
            synchronized (CommandManager.class) {
                if (null == sInstance) {
                    sInstance = new CommandManager(context);
                }
            }
        }
        return sInstance;
    }

    public <T extends AbsCommand> void getImplement(final Context context, final Class<T> intf, final String id,
                                                    boolean ignoreCache, final LoadCommandListener<T> listener) {
        if (null == listener) {
            return;
        }
        ClassManager.getInstance().loadClass(context, id, ignoreCache,
                new ClassManager.LoadClassListener() {
                    @Override
                    public void onClassLoaded(ClassFetcher fetcher, Apk info) {
                        if (null == fetcher) {
                            listener.onCommandLoaded(null);
                            return;
                        }
                        Intf intfInfo = mInterfaceMap.get(getKey(id, intf));
                        if (null == intfInfo) {
                            listener.onCommandLoaded(null);
                            return;
                        }
                        Class<T> clazz = fetcher.getClass(intfInfo.getImpl());
                        if (null == clazz) {
                            LogUtils.debug(CommandManager.this, "no class");
                            listener.onCommandLoaded(null);
                            return;
                        }
                        try {
                            T command = clazz.newInstance();
                            CommandContext commandContext = new CommandContext();
                            commandContext.apkInfo = info;
                            command.setContext(commandContext);

                            listener.onCommandLoaded(command);
                        } catch (InstantiationException | IllegalAccessException e) {
                            LogUtils.exception(CommandManager.this, e);
                            listener.onCommandLoaded(null);
                        }
                    }
                });
    }

    @Override
    public void onNewApk(Context context, Apk info, File apkFile, Manifest manifest) {
        LogUtils.debug(this, "new apk " + info.getApk());
        DbManager dbManager = DbManager.getInstance(context);
        IntfDao intfDao = dbManager.getDao(Intf.class);
        dbManager.deleteDataByApkIdInDb(info.getApk(), "APK", intfDao);
        Map<String, String> metas = manifest.getMetas();
        for (String name : metas.keySet()) {
            Intf intf = new Intf();
            intf.setApk(info.getApk());
            intf.setIntf(name);
            intf.setImpl(metas.get(name));
            intfDao.insertOrReplace(intf);
        }
        LogUtils.debug(this, "update db " + metas.size());
    }

    @Override
    public void invalidate(Context context, String id) {
        LogUtils.debug(this, "invalidate apk " + id);
        Collection<Intf> values = mInterfaceMap.values();
        for (Intf intf : values) {
            if (TextUtils.equals(intf.getApk(), id)) {
                mInterfaceMap.remove(getKey(intf));
            }
        }
        DbManager dbManager = DbManager.getInstance(context);
        IntfDao intfDao = dbManager.getDao(Intf.class);
        List<Intf> fromDb = dbManager.loadDataByApkIdInDb(id, "APK", intfDao);
        for (Intf intf : fromDb) {
            mInterfaceMap.put(getKey(intf), intf);
        }
    }

    @Override
    public void clear(Context context) {
        loadDb(context);
    }

    private void loadDb(Context context) {
        IntfDao intfDao = DbManager.getInstance(context).getDao(Intf.class);
        mInterfaceMap = new ArrayMap<>();
        List<Intf> intfs = intfDao.loadAll();
        for (Intf intf : intfs) {
            mInterfaceMap.put(getKey(intf), intf);
        }
        LogUtils.debug(this, "load db " + mInterfaceMap.size());
    }

    private String getKey(Intf intf) {
        return intf.getApk() + ":" + intf.getIntf();
    }

    private String getKey(String id, Class intf) {
        return id + ":" + intf.getName();
    }

    public interface LoadCommandListener<T> {
        void onCommandLoaded(T command);
    }
}
