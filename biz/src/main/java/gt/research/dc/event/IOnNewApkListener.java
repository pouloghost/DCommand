package gt.research.dc.event;

import android.content.Context;

import java.io.File;

import gt.research.dc.core.common.manifest.Manifest;
import gt.research.dc.core.db.Apk;

/**
 * Created by ayi.zty on 2016/2/19.
 */
public interface IOnNewApkListener {
    void onNewApk(Context context, Apk info, File apkFile, Manifest manifest);
}
