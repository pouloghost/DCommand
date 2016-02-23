package gt.research.dc.core.config.verifier.original;

import android.content.Context;
import android.content.pm.Signature;

import java.io.File;
import java.util.Set;

import gt.research.dc.core.config.verifier.IApkVerifier;
import gt.research.dc.core.config.verifier.OnVerifiedListener;
import gt.research.dc.util.LogUtils;
import gt.research.dc.util.ReflectUtils;
import gt.research.dc.util.VerifyUtils;

/**
 * Created by ayi.zty on 2016/2/1.
 */
public class ReflectVerifier implements IApkVerifier {
    //// TODO: 2016/2/16 compatibility, api 21 and up is ok. Lower OS is to crash.


    @Override
    public void verify(Context context, String path, OnVerifiedListener listener) {
        if (null == listener) {
            return;
        }
        listener.onVerified(doVerify(context, path));
    }

    private boolean doVerify(Context context, String path) {
        File apkFile = new File(path);
        Set<String> localKey = VerifyUtils.convertToPubKey(VerifyUtils.getLocalSignature(context));
        if (!apkFile.exists() || null == localKey) {
            return false;
        }
        try {
            Class packageParserClass = Class.forName("android.content.pm.PackageParser");
            Object packageParser = packageParserClass.newInstance();
            int parseFlag = 1 << 1 | 1 << 8;
            Object pkg = ReflectUtils.invokeMethod(packageParser, "parsePackage",
                    new Class[]{File.class, int.class}, new Object[]{apkFile, parseFlag});
            Class packageClass = Class.forName("android.content.pm.PackageParser$Package");
            ReflectUtils.invokeMethod(packageParser, "collectCertificates",
                    new Class[]{packageClass, int.class}, new Object[]{pkg, parseFlag});
            Set<String> pubKeys = VerifyUtils.convertToPubKey((Signature[]) ReflectUtils.getFieldValue(pkg, "mSignatures"));
            if (null == pubKeys || pubKeys.size() != localKey.size()) {
                return false;
            }
            return pubKeys.containsAll(localKey);
        } catch (Throwable throwable) {
            LogUtils.exception(ReflectVerifier.this, throwable);
            return false;
        }
    }
}
