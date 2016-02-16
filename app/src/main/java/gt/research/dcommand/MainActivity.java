package gt.research.dcommand;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import gt.research.dc.core.IVersion;
import gt.research.dc.core.command.CommandManager;
import gt.research.dc.core.config.ConfigManager;
import gt.research.dc.core.config.fetcher.NetFileFetcher;
import gt.research.dc.util.BinaryXmlUtils;
import gt.research.dc.util.LogUtils;

public class MainActivity extends AppCompatActivity {
    private TextView mVersion;
    private EditText mUrl;
    private NetFileFetcher mFetcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVersion = (TextView) findViewById(R.id.version);
        mUrl = (EditText) findViewById(R.id.url);
        mFetcher = new NetFileFetcher();

        final ConfigManager configManager = ConfigManager.getInstance();
        configManager.setConfigFetcher(mFetcher);

        String url = mUrl.getText().toString();
        mFetcher.setUrl(url);

        findViewById(R.id.call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get command
                CommandManager.getInstance().
                        getImplement(MainActivity.this, IVersion.class, new CommandManager.LoadCommandListener<IVersion>() {
                            @Override
                            public void onCommandLoaded(IVersion command) {
                                if(null == command){
                                    mVersion.setText("error");
                                    return;
                                }
                                mVersion.setText(command.getVersion());
                            }
                        });

                //verify apk
//                FileUtils.copy(Environment.getExternalStorageDirectory() + "/export.apk",
//                        MainActivity.this.getDir(FileConstants.DIR_DOWNLOAD, Context.MODE_PRIVATE).getAbsolutePath() + "/export.apk");
//                new OriginalVerifier().verify(MainActivity.this,
//                        Environment.getExternalStorageDirectory() + "/export.apk", new OnVerifiedListener() {
//                            @Override
//                            public void onVerified(boolean isSecure) {
//                                LogUtils.debug("secure " + isSecure);
//                            }
//                        });
//                new OriginalVerifier().getLocalSignature(MainActivity.this);
//                LogUtils.debug(getApplicationInfo().sourceDir);

                //load resource
//                ConfigManager.getInstance().getApkById(MainActivity.this, "IVersion", new ConfigManager.LoadApkInfoListener() {
//                    @Override
//                    public void onApkLoaded(ApkInfo info) {
//                        if (null == info) {
//                            return;
//                        }
//                        Resources res = getResources();
//                        ResourceManager resourceManager = ResourceManager.getInstance(res.getDisplayMetrics(), res.getConfiguration());
//                        resourceManager.loadResource(MainActivity.this, info, new ResourceManager.LoadResourceListener() {
//                            @Override
//                            public void onResourceLoaded(Resources resources) {
//                                int id = resources.getIdentifier("test", "string", "gt.research.export");
//                                mVersion.setText(resources.getString(id));
//                            }
//                        });
//                    }
//                });

//                LogUtils.debug(BinaryXmlUtils.readManifest(Environment.getExternalStorageDirectory().getAbsolutePath() + "/export-ok.apk"));

            }
        });

        findViewById(R.id.updateConfig).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfigManager.getInstance().updateLocalConfig(MainActivity.this);
            }
        });
    }
}
