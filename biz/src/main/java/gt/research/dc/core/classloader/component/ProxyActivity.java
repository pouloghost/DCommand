package gt.research.dc.core.classloader.component;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;

import java.util.concurrent.CountDownLatch;

import gt.research.dc.core.classloader.ClassFetcher;
import gt.research.dc.core.classloader.ClassManager;
import gt.research.dc.core.constant.ComponentConstants;
import gt.research.dc.core.db.Apk;
import gt.research.dc.core.db.Comp;
import gt.research.dc.core.resource.ResourceFetcher;
import gt.research.dc.core.resource.ResourceManager;
import gt.research.dc.util.LogUtils;

/**
 * Created by ayi.zty on 2016/2/22.
 */
public class ProxyActivity extends Activity {
    private BaseActivity mDynamicActivity;

    private ResourceFetcher mResourceFetcher;
    private ClassFetcher mClassFetcher;
    private Apk mApkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (null == intent) {
            LogUtils.debug(this, "no intent");
            finish();
            return;
        }
        String comp = intent.getStringExtra(ComponentConstants.BUNDLE_COMPONENT_NAME);
        final String apk = intent.getStringExtra(ComponentConstants.BUNDLE_APK_ID);
        if (TextUtils.isEmpty(comp) || TextUtils.isEmpty(apk)) {
            LogUtils.debug(this, "params wrong");
            finish();
            return;
        }
        loadAndStartDynamicActivity(comp, apk, savedInstanceState);
    }

    private void loadAndStartDynamicActivity(final String comp, final String apk, final Bundle savedInstanceState) {
        new Thread() {
            @Override
            public void run() {
                final CountDownLatch countDownLatch = new CountDownLatch(2);
                new Thread() {
                    @Override
                    public void run() {
                        ResourceManager.getInstance(ProxyActivity.this).loadResource(ProxyActivity.this,
                                apk, false, new ResourceManager.LoadResourceListener() {
                                    @Override
                                    public void onResourceLoaded(ResourceFetcher fetcher, Apk info) {
                                        mResourceFetcher = fetcher;
                                        countDownLatch.countDown();
                                    }
                                });
                    }
                }.start();
                new Thread() {
                    @Override
                    public void run() {
                        ClassManager.getInstance().loadClass(ProxyActivity.this,
                                apk, false, new ClassManager.LoadClassListener() {
                                    @Override
                                    public void onClassLoaded(ClassFetcher fetcher, Apk info) {
                                        mClassFetcher = fetcher;
                                        mApkInfo = info;
                                        countDownLatch.countDown();
                                    }
                                });
                    }
                }.start();
                try {
                    countDownLatch.await();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startDynamicActivity(comp, apk, savedInstanceState);
                        }
                    });
                } catch (InterruptedException e) {
                    LogUtils.exception(ProxyActivity.this, e);
                }
            }
        }.start();
    }

    private void startDynamicActivity(String comp, String apk, final Bundle savedInstanceState) {
        if (null == mResourceFetcher || null == mClassFetcher || TextUtils.isEmpty(comp)) {
            LogUtils.debug(this, "wrong loading");
            return;
        }
        ComponentManager.getInstance(this).loadActivity(this, comp, apk, false,
                new ComponentManager.LoadComponentListener<BaseActivity>() {
                    @Override
                    public void onComponentLoaded(BaseActivity instance, Comp info) {
                        if (null == instance) {
                            LogUtils.debug(ProxyActivity.this, "no instance");
                            return;
                        }
                        mDynamicActivity = instance;
                        mDynamicActivity.setProxy(ProxyActivity.this);
                        mDynamicActivity.onCreate(savedInstanceState);
                    }
                });
    }

    @Override
    public AssetManager getAssets() {
        return null == mResourceFetcher.getAsset() ? super.getAssets() : mResourceFetcher.getAsset();
    }

    @Override
    public Resources getResources() {
        return null == mResourceFetcher.getResources() ? super.getResources() : mResourceFetcher.getResources();
    }

    @Override
    public ClassLoader getClassLoader() {
        return mClassFetcher.getClassLoader();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mDynamicActivity.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        mDynamicActivity.onStart();
        super.onStart();
    }

    @Override
    protected void onRestart() {
        mDynamicActivity.onRestart();
        super.onRestart();
    }

    @Override
    protected void onResume() {
        mDynamicActivity.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mDynamicActivity.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        mDynamicActivity.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mDynamicActivity.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mDynamicActivity.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mDynamicActivity.onRestoreInstanceState(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        mDynamicActivity.onNewIntent(intent);
        super.onNewIntent(intent);
    }

    @Override
    public void onBackPressed() {
        mDynamicActivity.onBackPressed();
        super.onBackPressed();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        return mDynamicActivity.onTouchEvent(event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        super.onKeyUp(keyCode, event);
        return mDynamicActivity.onKeyUp(keyCode, event);
    }

    @Override
    public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
        mDynamicActivity.onWindowAttributesChanged(params);
        super.onWindowAttributesChanged(params);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        mDynamicActivity.onWindowFocusChanged(hasFocus);
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mDynamicActivity.onCreateOptionsMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mDynamicActivity.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public String getPackageName() {
        return mResourceFetcher.getPackage();
    }
}
