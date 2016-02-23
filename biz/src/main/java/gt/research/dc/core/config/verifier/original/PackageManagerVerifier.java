package gt.research.dc.core.config.verifier.original;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.util.Set;

import gt.research.dc.core.config.verifier.IApkVerifier;
import gt.research.dc.core.config.verifier.OnVerifiedListener;
import gt.research.dc.util.VerifyUtils;

/**
 * Created by ayi.zty on 2016/2/23.
 */
public class PackageManagerVerifier implements IApkVerifier {
    @Override
    public void verify(Context context, String path, OnVerifiedListener listener) {
        if (null == listener) {
            return;
        }
        listener.onVerified(doVerify(context, path));
    }

    private boolean doVerify(Context context, String path) {
        PackageInfo apkInfo = context.getPackageManager().getPackageArchiveInfo(path, PackageManager.GET_SIGNATURES);
        Signature[] apkSignatures = apkInfo.signatures;
        Signature[] localSignatures = VerifyUtils.getLocalSignature(context);
        if (null == apkSignatures || null == localSignatures || apkSignatures.length != localSignatures.length) {
            return false;
        }
        Set<String> apkPubKeys = VerifyUtils.convertToPubKey(apkSignatures);
        Set<String> localPubKeys = VerifyUtils.convertToPubKey(localSignatures);
        if (null == apkPubKeys || null == localPubKeys || apkPubKeys.size() != localPubKeys.size()) {
            return false;
        }
        return apkPubKeys.containsAll(localPubKeys);
    }
}
