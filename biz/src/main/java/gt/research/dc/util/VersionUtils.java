package gt.research.dc.util;

/**
 * Created by ayi.zty on 2016/1/28.
 */
public class VersionUtils {
    private static final String sVersionSplitter = "\\.";
    private static final int sDisplace = 4;

    public static boolean isLatest(String oldVer, String newVer) {
        return longVersion(oldVer) == longVersion(newVer);
    }

    private static long longVersion(String version) {
        String[] parts = version.split(sVersionSplitter);
        long result = 0;
        for (String part : parts) {
            result += Long.valueOf(part);
            result <<= sDisplace;
        }
        return result;
    }
}
