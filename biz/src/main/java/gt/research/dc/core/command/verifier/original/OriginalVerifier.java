package gt.research.dc.core.command.verifier.original;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;

import gt.research.dc.core.command.verifier.IApkVerifier;
import gt.research.dc.core.command.verifier.OnVerifiedListener;
import gt.research.dc.util.LogUtils;
import gt.research.dc.util.ReflectUtils;

/**
 * Created by ayi.zty on 2016/2/1.
 */
public class OriginalVerifier implements IApkVerifier {
    private static final String sKeyModulus = "modulus=";
    private static final String sKeyExponent = ",publicExponent";

    @Override
    public void verify(Context context, String path, OnVerifiedListener listener) {
        if (null == listener) {
            return;
        }
        listener.onVerified(doVerify(context, path));
    }

    private boolean doVerify(Context context, String path) {
        File apkFile = new File(path);
        Set<String> localKey = convertToPubKey(getLocalSignature(context));
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
            Set<String> pubKeys = convertToPubKey((Signature[]) ReflectUtils.getFieldValue(pkg, "mSignatures"));
            if (pubKeys.size() != localKey.size()) {
                return false;
            }
            return pubKeys.containsAll(localKey);
        } catch (Exception e) {
            LogUtils.exception(e);
            return false;
        }
    }

    private Signature[] getLocalSignature(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            return packageInfo.signatures;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.exception(e);
            return null;
        }
    }

    private Set<String> convertToPubKey(Signature[] signatures) {
        Set<String> result = new HashSet<>(signatures.length);
        try {
            CertificateFactory certFactory = CertificateFactory
                    .getInstance("X.509");
            for (Signature signature : signatures) {
                X509Certificate cert = (X509Certificate) certFactory
                        .generateCertificate(new ByteArrayInputStream(signature.toByteArray()));
                result.add(stripeToKey(cert.getPublicKey().toString()));
            }
        } catch (CertificateException e) {
            LogUtils.exception(e);
            result = null;
        }
        return result;
    }

    private String stripeToKey(String pubKeyStr) {
        int start = pubKeyStr.indexOf(sKeyModulus) + sKeyModulus.length();
        int end = pubKeyStr.indexOf(sKeyExponent);
        return pubKeyStr.substring(start, end);
    }
}
