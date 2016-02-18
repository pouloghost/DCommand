package gt.research.dc.core.db;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
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
    private Map<String, AbstractDao> mDaos;

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
            LogUtils.exception(throwable);
        }
        return dao;
    }
}
