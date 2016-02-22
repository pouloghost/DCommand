package gt.research.dc.core.classloader.component;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import gt.research.dc.util.LogUtils;

/**
 * Created by ayi.zty on 2016/2/19.
 */
public class BaseActivity extends Activity {
    public ProxyActivity self;

    //interfaces for proxy
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (null == self) {
            LogUtils.debug(this, "no self");
            super.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onBackPressed() {
        if (null == self) {
            LogUtils.debug(this, "no self");
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null == self) {
            LogUtils.debug(this, "no self");
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onStart() {
        if (null == self) {
            LogUtils.debug(this, "no self");
            super.onStart();
        }
    }

    @Override
    public void onRestart() {
        if (null == self) {
            LogUtils.debug(this, "no self");
            super.onRestart();
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (null == self) {
            LogUtils.debug(this, "no self");
            super.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (null == self) {
            LogUtils.debug(this, "no self");
            super.onSaveInstanceState(outState);
        }
    }

    public void onNewIntent(Intent intent) {
        if (null == self) {
            LogUtils.debug(this, "no self");
            super.onNewIntent(intent);
        }
    }

    @Override
    public void onResume() {
        if (null == self) {
            LogUtils.debug(this, "no self");
            super.onResume();
        }
    }

    @Override
    public void onPause() {
        if (null == self) {
            LogUtils.debug(this, "no self");
            super.onPause();
        }
    }

    @Override
    public void onStop() {
        if (null == self) {
            LogUtils.debug(this, "no self");
            super.onStop();
        }
    }

    @Override
    public void onDestroy() {
        if (null == self) {
            LogUtils.debug(this, "no self");
            super.onDestroy();
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (null == self) {
            LogUtils.debug(this, "no self");
            super.onTouchEvent(event);
        }
        return false;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (null == self) {
            LogUtils.debug(this, "no self");
            super.onKeyUp(keyCode, event);
        }
        return false;
    }

    public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
        if (null == self) {
            LogUtils.debug(this, "no self");
            super.onWindowAttributesChanged(params);
        }
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        if (null == self) {
            LogUtils.debug(this, "no self");
            super.onWindowFocusChanged(hasFocus);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (null == self) {
            LogUtils.debug(this, "no self");
            super.onCreateOptionsMenu(menu);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (null == self) {
            LogUtils.debug(this, "no self");
            return super.onOptionsItemSelected(item);
        }
        return false;
    }

    // interfaces from proxy
    @Override
    public void setContentView(View view) {
        if (null == self) {
            LogUtils.debug(this, "no self");
            super.setContentView(view);
        } else {
            self.setContentView(view);
        }
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (null == self) {
            LogUtils.debug(this, "no self");
            super.setContentView(view, params);
        } else {
            self.setContentView(view, params);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        if (null == self) {
            LogUtils.debug(this, "no self");
            super.setContentView(layoutResID);
        } else {
            self.setContentView(layoutResID);
        }
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        if (null == self) {
            LogUtils.debug(this, "no self");
            super.addContentView(view, params);
        } else {
            self.addContentView(view, params);
        }
    }

    @Override
    public View findViewById(int id) {
        if (null == self) {
            LogUtils.debug(this, "no self");
            return super.findViewById(id);
        } else {
            return self.findViewById(id);
        }
    }

    @Override
    public Intent getIntent() {
        if (null == self) {
            LogUtils.debug(this, "no self");
            return super.getIntent();
        } else {
            return self.getIntent();
        }
    }

    @Override
    public ClassLoader getClassLoader() {
        if (null == self) {
            LogUtils.debug(this, "no self");
            return super.getClassLoader();
        } else {
            return self.getClassLoader();
        }
    }

    @Override
    public Resources getResources() {
        if (null == self) {
            LogUtils.debug(this, "no self");
            return super.getResources();
        } else {
            return self.getResources();
        }
    }

    @Override
    public String getPackageName() {
        if (null == self) {
            LogUtils.debug(this, "no self");
            return super.getPackageName();
        } else {
            return self.getPackageName();
        }
    }

    @Override
    public LayoutInflater getLayoutInflater() {
        if (null == self) {
            LogUtils.debug(this, "no self");
            return super.getLayoutInflater();
        } else {
            return self.getLayoutInflater();
        }
    }

    @Override
    public MenuInflater getMenuInflater() {
        if (null == self) {
            LogUtils.debug(this, "no self");
            return super.getMenuInflater();
        } else {
            return self.getMenuInflater();
        }
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        if (null == self) {
            LogUtils.debug(this, "no self");
            return super.getSharedPreferences(name, mode);
        } else {
            return self.getSharedPreferences(name, mode);
        }
    }

    @Override
    public Context getApplicationContext() {
        if (null == self) {
            LogUtils.debug(this, "no self");
            return super.getApplicationContext();
        } else {
            return self.getApplicationContext();
        }
    }

    @Override
    public WindowManager getWindowManager() {
        if (null == self) {
            LogUtils.debug(this, "no self");
            return super.getWindowManager();
        } else {
            return self.getWindowManager();
        }
    }

    @Override
    public Window getWindow() {
        if (null == self) {
            LogUtils.debug(this, "no self");
            return super.getWindow();
        } else {
            return self.getWindow();
        }
    }

    @Override
    public Object getSystemService(String name) {
        if (null == self) {
            LogUtils.debug(this, "no self");
            return super.getSystemService(name);
        } else {
            return self.getSystemService(name);
        }
    }

    @Override
    public void finish() {
        if (null == self) {
            LogUtils.debug(this, "no self");
            super.finish();
        } else {
            self.finish();
        }
    }


    public void setProxy(ProxyActivity proxy) {
        self = proxy;
    }

    public ProxyActivity self() {
        return self;
    }
}
