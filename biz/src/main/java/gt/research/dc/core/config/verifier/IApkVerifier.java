package gt.research.dc.core.config.verifier;

import android.content.Context;

/**
 * Created by ayi.zty on 2016/2/1.
 */
public interface IApkVerifier {
    void verify(Context context, String path, OnVerifiedListener listener);
}
