package gt.research.dc.core.config.verifier.simple;

import android.content.Context;

import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import gt.research.dc.core.config.verifier.IApkVerifier;
import gt.research.dc.core.config.verifier.OnVerifiedListener;
import gt.research.dc.util.LogUtils;

/**
 * Created by ayi.zty on 2016/2/26.
 */
public class JarFileVerifier implements IApkVerifier {
    // TODO: 2016/2/26 try this
    @Override
    public void verify(Context context, String path, OnVerifiedListener listener) {
        if (null == listener) {
            return;
        }
        try {
            JarFile jarFile = new JarFile(path);
            JarEntry entry = jarFile.getJarEntry("");

        } catch (IOException e) {
            LogUtils.exception(this, e);
        }
    }
}
