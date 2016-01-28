package gt.research.dcommand;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import gt.research.core.CommandManager;
import gt.research.core.ConfigManager;
import gt.research.dc.core.IVersion;

public class MainActivity extends AppCompatActivity {
    private TextView mVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVersion = (TextView) findViewById(R.id.version);
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
                ConfigManager.getInstance().updateConfig(MainActivity.this, null);
            }
        });
    }
}
