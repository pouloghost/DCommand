package gt.research.dcommand;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;

import gt.research.dc.core.IVersion;
import gt.research.dc.core.classloader.ClassFetcher;
import gt.research.dc.core.classloader.ClassManager;
import gt.research.dc.core.command.CommandManager;
import gt.research.dc.core.config.ApkConfigManager;
import gt.research.dc.core.config.fetcher.NetFileFetcher;
import gt.research.dc.core.db.Apk;
import gt.research.dc.core.resource.ResourceFetcher;
import gt.research.dc.core.resource.ResourceManager;
import gt.research.dc.util.LogUtils;

public class MainActivity extends AppCompatActivity {
    private TextView mVersion;
    private ImageView mImage;
    private Spinner mUrl;
    private Spinner mAction;

    private NetFileFetcher mFetcher;
    private Runnable mCommand;
    private Runnable[] mCommands;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initCommands();

        setContentView(R.layout.activity_main);
        mVersion = (TextView) findViewById(R.id.version);
        mImage = (ImageView) findViewById(R.id.image);
        mUrl = (Spinner) findViewById(R.id.url);
        mAction = (Spinner) findViewById(R.id.action);

        mFetcher = new NetFileFetcher();

        final ApkConfigManager apkConfigManager = ApkConfigManager.getInstance();
        apkConfigManager.setConfigFetcher(mFetcher);

        mAction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (id >= mCommands.length) {
                    return;
                }
                mCommand = mCommands[((int) id)];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mUrl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String url = (String) parent.getAdapter().getItem((int) id);
                mFetcher.setUrl(url);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mUrl.setSelection(0);

        findViewById(R.id.call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null == mCommand) {
                    return;
                }
                mCommand.run();
                //get command
//                CommandManager.getInstance().
//                        getImplement(MainActivity.this, IVersion.class, new CommandManager.LoadCommandListener<IVersion>() {
//                            @Override
//                            public void onCommandLoaded(IVersion command) {
//                                if (null == command) {
//                                    mVersion.setText("error");
//                                    return;
//                                }
//                                mVersion.setText(command.getVersion());
//                                ComponentsTable table = ComponentsTable.fromFile("IVersion",
//                                        FileUtils.getCacheApkFile(MainActivity.this, "IVersion").getAbsolutePath());
//                                Comp comp = table.getComponent("gt.research.export.MainActivity");
//                                mVersion.setText(comp.getComp());
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

                //load layout
//                Intent intent = new Intent();
//                intent.setClass(MainActivity.this, DynamicLayoutActivity.class);
//                startActivity(intent);

                //load dynamic activity

            }
        });
    }

    private void initCommands() {
        mCommands = new Runnable[]{
                new Runnable() {
                    @Override
                    public void run() {
                        //load file
                        ApkConfigManager.getInstance().getApkInfoAndFileById(MainActivity.this, "IVersion", false,
                                new ApkConfigManager.LoadApkInfoAndFileListener() {
                                    @Override
                                    public void onApkInfoAndFile(Apk info, File apkFile) {
                                        if (null == info || null == apkFile) {
                                            LogUtils.debug(this, "error");
                                            return;
                                        }
                                        LogUtils.debug(this, info.getApk() + " " + info.getTimestamp());
                                        LogUtils.debug(this, "file " + apkFile.lastModified());
                                    }
                                });
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        //load resource
                        Resources res = getResources();
                        ResourceManager.getInstance(res.getDisplayMetrics(), res.getConfiguration()).
                                loadResource(MainActivity.this, "IVersion", false, new ResourceManager.LoadResourceListener() {
                                    @Override
                                    public void onResourceLoaded(ResourceFetcher fetcher, Apk info) {
                                        if (null == fetcher) {
                                            mVersion.setText("error");
                                            return;
                                        }
                                        mVersion.setText(fetcher.getString("test"));
                                        mImage.setImageDrawable(fetcher.getDrawable("test"));
                                    }
                                });
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        //load classloader
                        ClassManager.getInstance().loadClass(MainActivity.this, "IVersion", false,
                                new ClassManager.LoadClassListener() {
                                    @Override
                                    public void onClassLoaded(ClassFetcher fetcher, Apk info) {
                                        if (null == fetcher) {
                                            mVersion.setText("error");
                                            return;
                                        }
                                        try {
                                            Class clazz = fetcher.getClass("gt.research.export.VersionImpl");
                                            IVersion version = (IVersion) clazz.newInstance();
                                            mVersion.setText(version.getVersion());
                                        } catch (InstantiationException | IllegalAccessException e) {
                                            mVersion.setText("error");
                                            LogUtils.exception(MainActivity.this, e);
                                        }
                                    }
                                });
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        //load command
                        CommandManager.getInstance(getApplicationContext()).getImplement(MainActivity.this, IVersion.class,
                                "IVersion", false, new CommandManager.LoadCommandListener<IVersion>() {
                                    @Override
                                    public void onCommandLoaded(IVersion command) {
                                        if (null == command) {
                                            mVersion.setText("error");
                                            return;
                                        }
                                        mVersion.setText(command.getVersion());
                                    }
                                });
                    }
                }
        };
    }
}
