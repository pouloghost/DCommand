package gt.research.dc.util;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

import gt.research.dc.core.config.ApkConfigManager;
import gt.research.dc.core.constant.DBConstants;
import gt.research.dc.core.db.Apk;
import gt.research.dc.core.db.ApkDao;
import gt.research.dc.core.db.DaoMaster;
import gt.research.dc.core.db.DaoSession;
import gt.research.dc.event.CacheEventHandler;

/**
 * Created by ayi.zty on 2016/2/16.
 */
public class ResourceUtils {
    public static String updateApkPackage(Context context, File apkFile) {
        String manifest = BinaryXmlUtils.readManifest(apkFile.getAbsolutePath());
        String pkgName = BinaryXmlUtils.readPackage(manifest);
        if (null == pkgName) {
            return null;
        }

        String id = FileUtils.fileToId(apkFile);
        SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context, DBConstants.DB_FILE_CONFIG, null);
        DaoMaster daoMaster = new DaoMaster(helper.getReadableDatabase());
        DaoSession session = daoMaster.newSession();
        ApkDao apkDao = session.getApkDao();
        Apk apk = apkDao.load(id);
        apkDao.deleteByKey(id);
        apk.setPkgName(pkgName);
        apkDao.insertOrReplace(apk);

        CacheEventHandler.invalidateCache(id);
        ApkConfigManager.getInstance().updateFromDb(context);

        return pkgName;
    }
}
