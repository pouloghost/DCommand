package gt.research.dc.core.classloader;

import gt.research.dc.core.command.verifier.IApkVerifier;
import gt.research.dc.core.command.verifier.original.OriginalVerifier;

/**
 * Created by ayi.zty on 2016/2/18.
 */
public class ClassLoaderManager {
    private IApkVerifier mVerifier;

    private volatile static ClassLoaderManager sInstance;

    private ClassLoaderManager() {
        mVerifier = new OriginalVerifier();
    }

    public static ClassLoaderManager getInstance() {
        if (null == sInstance) {
            synchronized (ClassLoaderManager.class) {
                if (null == sInstance) {
                    sInstance = new ClassLoaderManager();
                }
            }
        }
        return sInstance;
    }
}
