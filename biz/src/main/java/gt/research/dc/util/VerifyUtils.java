package gt.research.dc.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ayi.zty on 2016/2/23.
 */
public class VerifyUtils {
    private static final String sKeyModulus = "modulus=";
    private static final String sKeyExponent = ",publicExponent";

    public static Signature[] getLocalSignature(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            return packageInfo.signatures;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.exception(VerifyUtils.class.getSimpleName(), e);
            return null;
        }
    }

    public static Set<String> convertToPubKey(Signature[] signatures) {
        if (null == signatures) {
            return null;
        }
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
            LogUtils.exception(VerifyUtils.class.getSimpleName(), e);
            result = null;
        }
        return result;
    }

    private static String stripeToKey(String pubKeyStr) {
        int start = pubKeyStr.indexOf(sKeyModulus) + sKeyModulus.length();
        int end = pubKeyStr.indexOf(sKeyExponent);
        return pubKeyStr.substring(start, end);
    }
}
