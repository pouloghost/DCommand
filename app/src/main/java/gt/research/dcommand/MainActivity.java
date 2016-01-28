package gt.research.dcommand;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import gt.research.dc.core.IVersion;
import gt.research.dc.core.config.ConfigManager;
import gt.research.dc.core.config.fetcher.NetFileFetcher;
import gt.research.dc.core.manager.CommandManager;

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

        ConfigManager configManager = ConfigManager.getInstance();
        configManager.setConfigFetcher(mFetcher);

        findViewById(R.id.call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommandManager.getInstance().
                        getImplement(MainActivity.this, IVersion.class, new CommandManager.LoadCommandListener<IVersion>() {
                            @Override
                            public void onCommandLoaded(IVersion command) {
                                mVersion.setText(command.getVersion());
                            }
                        });
            }
        });

        findViewById(R.id.updateConfig).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = mUrl.getText().toString();
                mFetcher.setUrl(url);
                ConfigManager.getInstance().updateLocalConfig(MainActivity.this);
            }
        });
    }
}
