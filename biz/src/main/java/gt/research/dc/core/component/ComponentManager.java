package gt.research.dc.core.component;

import android.content.Context;

import gt.research.dc.core.command.verifier.IApkVerifier;
import gt.research.dc.core.command.verifier.original.OriginalVerifier;
import gt.research.dc.data.ApkInfo;

/**
 * Created by ayi.zty on 2016/2/18.
 */
public class ComponentManager {
    private IApkVerifier mVerifier;

    private volatile static ComponentManager sInstance;

    private ComponentManager() {
        mVerifier = new OriginalVerifier();
    }

    public static ComponentManager getInstance() {
        if (null == sInstance) {
            synchronized (ComponentManager.class) {
                if (null == sInstance) {
                    sInstance = new ComponentManager();
                }
            }
        }
        return sInstance;
    }

    public void loadComponent(final Context context, final String id, final String clazz,
                              boolean ignoreCache, final LoadComponentListener listener) {
        if (null == listener) {
            return;
        }
        if (!ignoreCache) {
            // TODO: 2016/2/18 add cache
        }

    }

    public void setVerifier(IApkVerifier mVerifier) {
        this.mVerifier = mVerifier;
    }

    public interface LoadComponentListener {
        <T> T onComponentLoaded(T comp, ApkInfo info);
    }
}
