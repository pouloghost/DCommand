package gt.research.dc.util;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gt.research.dc.core.config.ConfigManager;
import gt.research.dc.core.constant.DBConstants;
import gt.research.dc.core.db.Apk;
import gt.research.dc.core.db.ApkDao;
import gt.research.dc.core.db.DaoMaster;
import gt.research.dc.core.db.DaoSession;

/**
 * Created by ayi.zty on 2016/2/16.
 */
public class ResourceUtils {
    public static String updateApkPackage(Context context, File apkFile) {
        String manifest = BinaryXmlUtils.readManifest(apkFile.getAbsolutePath());
        Pattern reg = Pattern.compile("package=\"(.*?)\"");
        Matcher matcher = reg.matcher(manifest);
        if (matcher.find()) {
            LogUtils.debug("count " + matcher.groupCount());
            String pkgName = matcher.group(1);
            String id = FileUtils.fileToId(apkFile);

            SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context, DBConstants.DB_FILE_CONFIG, null);
            DaoMaster daoMaster = new DaoMaster(helper.getReadableDatabase());
            DaoSession session = daoMaster.newSession();
            ApkDao apkDao = session.getApkDao();
            Apk apk = apkDao.load(id);
            apkDao.deleteByKey(id);
            apk.setPkgName(pkgName);
            apkDao.insertOrReplace(apk);

            ConfigManager.getInstance().loadLocalConfig(context);

            return pkgName;
        }
        return null;
    }
}
