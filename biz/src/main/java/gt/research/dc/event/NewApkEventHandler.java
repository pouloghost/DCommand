package gt.research.dc.event;

import android.content.Context;

import java.io.File;

import gt.research.dc.core.classloader.command.CommandManager;
import gt.research.dc.core.classloader.component.ComponentManager;
import gt.research.dc.core.common.manifest.Manifest;
import gt.research.dc.core.db.Apk;
import gt.research.dc.core.resource.ResourceManager;
import gt.research.dc.util.LogUtils;

/**
 * Created by ayi.zty on 2016/2/18.
 */
public class NewApkEventHandler {
    public static void onNewApk(Context context, Apk info, File apkFile) {
        if (null == apkFile || !apkFile.exists() || null == info) {
            return;
        }
        LogUtils.debug(NewApkEventHandler.class.getSimpleName(), "notify new apk");
        Manifest manifest = Manifest.fromFile(apkFile.getAbsolutePath());
        IOnNewApkListener listener = ResourceManager.getInstance(context);
        if (null != listener) {
            listener.onNewApk(context, info, apkFile, manifest);
        }
        listener = CommandManager.getInstance(context);
        listener.onNewApk(context, info, apkFile, manifest);
        listener = ComponentManager.getInstance(context);
        listener.onNewApk(context, info, apkFile, manifest);
    }
}
