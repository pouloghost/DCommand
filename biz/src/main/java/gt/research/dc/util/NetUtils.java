package gt.research.dc.util;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;

import java.io.File;

/**
 * Created by ayi.zty on 2016/1/26.
 */
public class NetUtils {
    public static void download(Context context, final String url, final DownloadListener listener) {
        String fileName = getFileName(url);
        File old = new File(context.getExternalFilesDir(null), fileName);
        if (old.exists()) {
            if (null != listener) {
                listener.onCached(url, old.getAbsolutePath());
            }
            return;
        }
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDestinationInExternalFilesDir(context, null, fileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);

        final DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        final long ref = manager.enqueue(request);
        if (null != listener) {
            listener.onEnqueue(url);
            context.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    long receivedRef = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    if (ref == receivedRef) {
                        Uri uri = manager.getUriForDownloadedFile(receivedRef);
                        listener.onFinish(url, uri.getPath());
                        context.unregisterReceiver(this);
                    }
                }
            }, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
    }

    public static void clearDownloads(Context context) {
        File root = context.getExternalFilesDir(null);
        File[] files = root.listFiles();
        for (File file : files) {
            file.delete();
        }
    }

    private static String getFileName(String url) {
        int index = url.lastIndexOf("/");
        return url.substring(index);
    }

    public interface DownloadListener {
        void onEnqueue(String url);

        void onFinish(String url, String file);

        void onCached(String url, String file);
    }
}
