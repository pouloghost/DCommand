package gt.research.dc.core.db;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import gt.research.dc.core.constant.DBConstants;
import gt.research.dc.util.LogUtils;
import gt.research.dc.util.ReflectUtils;

/**
 * Created by ayi.zty on 2016/2/18.
 */
public class DbManager {
    private static final String sGetterTemplate = "get$Dao";
    private volatile static DbManager sInstance;

    private DaoSession mSession;
    private final Class[] mEntities = new Class[]{Apk.class, Intf.class, Comp.class};
    private final String[] mKeys = new String[]{"APK", "APK", "APK"};

    private DbManager(Context context) {
        SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context, DBConstants.DB_FILE_CONFIG, null);
        DaoMaster daoMaster = new DaoMaster(helper.getReadableDatabase());
        mSession = daoMaster.newSession();
    }

    public static DbManager getInstance(Context context) {
        if (null == sInstance) {
            synchronized (DbManager.class) {
                if (null == sInstance) {
                    sInstance = new DbManager(context);
                }
            }
        }
        return sInstance;
    }

    public <K, T extends AbstractDao<K, ?>> T getDao(Class<K> entity) {
        String getter = sGetterTemplate.replace("$", entity.getSimpleName());
        T dao = null;
        try {
            dao = (T) ReflectUtils.invokeMethod(mSession, getter);
        } catch (Throwable throwable) {
            LogUtils.exception(DbManager.this, throwable);
        }
        return dao;
    }

    public boolean deleteDataByApkId(String id) {
        boolean success = true;
        for (int i = 0; i < mEntities.length; ++i) {
            AbstractDao dao = getDao(mEntities[i]);
            if (null == dao) {
                success = false;
                continue;
            }
            deleteDataByApkIdInDb(id, mKeys[i], dao);
        }
        return success;
    }

    private void deleteDataByApkIdInDb(String id, String key, AbstractDao dao) {
        QueryBuilder queryBuilder = dao.queryBuilder();
        queryBuilder.where(new WhereCondition.StringCondition(key + " = '" + id + "'"));
        DeleteQuery delete = queryBuilder.buildDelete();
        delete.executeDeleteWithoutDetachingEntities();
    }
}
