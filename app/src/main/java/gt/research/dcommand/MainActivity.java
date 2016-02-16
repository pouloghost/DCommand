package gt.research.dcommand;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import gt.research.dc.core.config.ConfigManager;
import gt.research.dc.core.config.fetcher.NetFileFetcher;
import gt.research.dc.core.resource.ResourceFetcher;
import gt.research.dc.core.resource.ResourceManager;

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
//                CommandManager.getInstance().
//                        getImplement(MainActivity.this, IVersion.class, new CommandManager.LoadCommandListener<IVersion>() {
//                            @Override
//                            public void onCommandLoaded(IVersion command) {
//                                if(null == command){
//                                    mVersion.setText("error");
//                                    return;
//                                }
//                                mVersion.setText(command.getVersion());
//                            }
//                        });

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
                Resources res = getResources();
                ResourceManager resourceManager = ResourceManager.getInstance(res.getDisplayMetrics(), res.getConfiguration());
                resourceManager.loadResource(MainActivity.this, "IVersion", new ResourceManager.LoadResourceListener() {
                    @Override
                    public void onResourceLoaded(ResourceFetcher fetcher) {
                        if (null == fetcher) {
                            mVersion.setText("error");
                        }
                        mVersion.setText(fetcher.getString("test"));
                    }
                });
            }
        });

        findViewById(R.id.updateConfig).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfigManager.getInstance().updateConfig(MainActivity.this, null);
            }
        });
    }
}
