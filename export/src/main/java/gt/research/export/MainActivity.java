package gt.research.export;

import android.os.Bundle;
import android.widget.TextView;

import gt.research.dc.core.classloader.component.BaseActivity;


public class MainActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((TextView) findViewById(R.id.dtext)).setText("test");
    }
}
