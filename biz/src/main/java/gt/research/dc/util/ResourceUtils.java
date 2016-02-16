package gt.research.dc.util;

import android.content.Context;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ayi.zty on 2016/2/16.
 */
public class ResourceUtils {
    public static void updateApkPackage(Context context, File apkFile) {
        String manifest = BinaryXmlUtils.readManifest(apkFile.getAbsolutePath());
        Pattern reg = Pattern.compile("package=\"(.*)\"");
        Matcher matcher = reg.matcher(manifest);
        if (matcher.find()) {
            LogUtils.debug(matcher.groupCount() + "");
        }
    }
}
