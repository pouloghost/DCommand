package gt.research.dcommand;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;

import gt.research.dc.core.resource.ResourceFetcher;
import gt.research.dc.core.resource.ResourceManager;
import gt.research.dc.data.ApkInfo;
import gt.research.dc.util.FileUtils;
import gt.research.dc.util.LogUtils;
import gt.research.dc.util.ReflectUtils;

public class DynamicLayoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: 2016/2/16 hack to make the activity's resource contains dynamic resources 
        super.onCreate(savedInstanceState);
        final Resources res = getResources();
        ResourceManager resourceManager = ResourceManager.getInstance(res.getDisplayMetrics(), res.getConfiguration());
        resourceManager.loadResource(this, "IVersion", true, new ResourceManager.LoadResourceListener() {
            @Override
            public void onResourceLoaded(ResourceFetcher fetcher, ApkInfo info) {
                if (null == fetcher) {
                    finish();
                    return;
                }
                XmlPullParser xmlPullParser = fetcher.getLayout("dynamic");
                if (null == xmlPullParser) {
                    finish();
                    return;
                }

                AssetManager assetManager = res.getAssets();
                try {
                    ReflectUtils.invokeMethod(assetManager, "addAssetPath",
                            new Class[]{String.class},
                            new Object[]{FileUtils.getCacheApkFile(DynamicLayoutActivity.this, info).getAbsolutePath()});
                } catch (Throwable throwable) {
                    LogUtils.exception(throwable);
                    finish();
                    return;
                }

                View view = LayoutInflater.from(DynamicLayoutActivity.this).inflate(xmlPullParser, null);
                setContentView(view);

                int buttonId = fetcher.getId("button");
                findViewById(buttonId).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(DynamicLayoutActivity.this, "lalal", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }
}
