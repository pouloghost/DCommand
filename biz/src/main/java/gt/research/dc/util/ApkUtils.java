package gt.research.dc.util;

import android.content.Context;

import java.io.File;

import gt.research.dc.core.command.verifier.IApkVerifier;
import gt.research.dc.core.command.verifier.OnVerifiedListener;
import gt.research.dc.data.ApkInfo;

/**
 * Created by ayi.zty on 2016/2/16.
 */
public class ApkUtils {
    public static void downloadAndVerifyApk(final Context context, final ApkInfo apk, final File apkFile,
                                            final Runnable afterLoad, final IApkVerifier verifier) {
        NetUtils.download(context, apk.url, new NetUtils.DownloadListener() {
            @Override
            public void onEnqueue(String url) {

            }

            @Override
            public void onFinish(String url, String file) {
                onFileGot(context, file, apkFile, afterLoad, true, verifier);
            }

            @Override
            public void onCached(String url, String file) {
                onFileGot(context, file, apkFile, afterLoad, false, verifier);
            }

            @Override
            public void onFail() {
                if (null != afterLoad) {
                    afterLoad.run();
                }
            }
        });
    }

    private static void onFileGot(final Context context, final String file, final File apkFile,
                                  final Runnable afterLoad, final boolean updatePackage, final IApkVerifier verifier) {
        OnVerifiedListener listener = new OnVerifiedListener() {
            @Override
            public void onVerified(boolean isSecure) {
                if (isSecure) {
                    FileUtils.copy(file, apkFile.getAbsolutePath());
                } else {
                    new File(file).delete();
                }
                if (updatePackage && apkFile.exists()) {
                    ResourceUtils.updateApkPackage(context, apkFile);
                }
                if (null != afterLoad) {
                    afterLoad.run();
                }
            }
        };
        if (null == verifier) {
            listener.onVerified(true);
            return;
        }
        verifier.verify(context, file, listener);
    }
}
