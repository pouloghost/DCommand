package gt.research.dc.core.component;

import android.text.TextUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import gt.research.dc.core.db.Comp;
import gt.research.dc.util.BinaryXmlUtils;
import gt.research.dc.util.LogUtils;

/**
 * Created by ayi.zty on 2016/2/17.
 */
public class ComponentsTable {
    private static final String sTagApplication = "application";
    private static final String sTagActivity = "activity";
    private static final String sAttrName = "name";

    private List<Comp> mActivities;
    private String mApkId;

    private ComponentsTable() {
    }

    public static ComponentsTable fromFile(String apk, String path) {
        ComponentsTable table = new ComponentsTable();
        table.loadManifest(BinaryXmlUtils.readManifest(path));
        table.mApkId = apk;
        return table;
    }

    public static ComponentsTable fromXml(String apk, String xml) {
        ComponentsTable table = new ComponentsTable();
        table.loadManifest(xml);
        table.mApkId = apk;
        return table;
    }

    public Collection<Comp> getActivities() {
        return mActivities;
    }

    private void loadManifest(String manifest) {
        String pkg = BinaryXmlUtils.readPackage(manifest);
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(manifest));
            int eventType = parser.next();
            while (XmlPullParser.END_DOCUMENT != eventType) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        switch (parser.getName()) {
                            case sTagActivity:
                                Comp comp = getComp(parser, pkg, sTagActivity);
                                mActivities.add(comp);
                                break;
                            case sTagApplication:
                                clearMem();
                                break;
                        }
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            LogUtils.exception(e);
        }
    }

    private Comp getComp(XmlPullParser parser, String pkg, String type) {
        Comp comp = new Comp();
        comp.setApk(mApkId);
        comp.setType(type);
        final int count = parser.getAttributeCount();
        for (int i = 0; i < count; ++i) {
            if (TextUtils.equals(parser.getAttributeName(i), sAttrName)) {
                String name = parser.getAttributeValue(i);
                if (name.startsWith(".")) {
                    comp.setComp(pkg + name);
                } else {
                    comp.setComp(name);
                }
                return comp;
            }
        }
        return null;
    }

    private void clearMem() {
        mActivities = new ArrayList<>();
    }
}
