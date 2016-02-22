package gt.research.dc.core.common.manifest;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import gt.research.dc.core.db.Comp;
import gt.research.dc.util.FileUtils;
import gt.research.dc.util.LogUtils;

/**
 * Created by ayi.zty on 2016/2/19.
 */
public final class Manifest {
    private static final int sEndDocTag = 0x00100101;
    private static final int sStartTag = 0x00100102;
    private static final int sEndTag = 0x00100103;
    private static final byte sTypeInt = 0x10;
    private static final byte sTypeString = 0x03;
    private static final String sManifestName = "AndroidManifest.xml";

    private static final String sTagManifest = "manifest";
    private static final String sTagApplication = "application";
    public static final String sTagActivity = "activity";

    private static final String sTagMeta = "meta-data";
    private static final String sAttrPackage = "package";
    private static final String sAttrName = "name";
    private static final String sAttrValue = "value";

    private String mPackage;
    private Map<String, String> mMetas;
    private List<Comp> mComponents;
    private String mApkId;

    private Manifest() {
    }

    public static Manifest fromFile(String path) {
        Manifest manifest = new Manifest();
        manifest.parseManifest(readManifest(path));
        manifest.mApkId = FileUtils.pathToId(path);
        return manifest;
    }

    public static Manifest fromXml(String apk, String xml) {
        Manifest manifest = new Manifest();
        manifest.parseManifest(xml);
        manifest.mApkId = apk;
        return manifest;
    }

    public String getPackage() {
        return mPackage;
    }

    public List<Comp> getComponents() {
        return mComponents;
    }

    public Map<String, String> getMetas() {
        return mMetas;
    }

    private void parseManifest(String manifest) {
        LogUtils.debug(this, "parse manifest " + manifest);
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(manifest));
            clearFields();

            int eventType = parser.next();
            while (XmlPullParser.END_DOCUMENT != eventType) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        switch (parser.getName()) {
                            case sTagManifest:
                                mPackage = readAttrValue(parser, sAttrPackage);
                                LogUtils.debug(this, "package " + mPackage);
                                break;
                            case sTagMeta:
                                String name = readAttrValue(parser, sAttrName);
                                String value = readAttrValue(parser, sAttrValue);
                                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(value)) {
                                    break;
                                }
                                mMetas.put(name, value);
                                LogUtils.debug(this, "meta " + name + " : " + value);
                                break;
                            case sTagActivity:
                                Comp comp = getComp(parser);
                                if (null != comp) {
                                    mComponents.add(comp);
                                }
                                break;
                            case sTagApplication:
                                break;
                        }
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            LogUtils.exception(Manifest.this, e);
        }
    }


    private Comp getComp(XmlPullParser parser) {
        if (TextUtils.isEmpty(mPackage)) {
            return null;
        }
        Comp comp = new Comp();
        comp.setApk(mApkId);
        comp.setType(parser.getName());
        String name = readAttrValue(parser, sAttrName);
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        if (name.startsWith(".")) {
            comp.setComp(mPackage + name);
        } else {
            comp.setComp(name);
        }
        LogUtils.debug(this, "new component " + comp.getComp());
        return comp;
    }

    private String readAttrValue(XmlPullParser parser, String name) {
        final int count = parser.getAttributeCount();
        for (int i = 0; i < count; ++i) {
            if (TextUtils.equals(parser.getAttributeName(i), name)) {
                return parser.getAttributeValue(i);
            }
        }
        return null;
    }

    private void clearFields() {
        mComponents = new ArrayList<>();
        mMetas = new ArrayMap<>();
        mPackage = null;
    }

    private static String readManifest(String path) {
        InputStream inputStream = null;
        try {
            ZipFile file = new ZipFile(path);
            ZipEntry entry = file.getEntry(sManifestName);
            inputStream = file.getInputStream(entry);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] tmp = new byte[1024];
            int count;
            while (-1 != (count = inputStream.read(tmp))) {
                outputStream.write(tmp, 0, count);
            }
            return decompressXML(outputStream.toByteArray());
        } catch (IOException e) {
            LogUtils.exception(Manifest.class.getSimpleName(), e);
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LogUtils.exception(Manifest.class.getSimpleName(), e);
                }
            }
        }
        return null;
    }

    private static String decompressXML(byte[] xml) {
        /*
            <manifest versionCode="1"

            0201// type
            1000// header size
            8800 0000// size
            0200 0000// line number
            ffff ffff// comment
            ffff ffff// ns
            0c00 0000// name
            1400// start
            1400// size
            0500// count
            0000// id
            0000// class
            0000// style
            0700 0000// ns
            0000 0000// name
            ffff ffff// raw value
            0800// size
            00// 00
            10// type
            0100 0000//data
        */
        StringBuilder resultXml = new StringBuilder();
        // seems to be true
        // Compressed XML file/bytes starts with 24x bytes of data,
        // 9 32 bit words in little endian order (LSB first):
        //   0th word is 03 00 08 00
        //   3rd word SEEMS TO BE:  Offset at then of StringTable
        //   4th word is: Number of strings in string table
        // WARNING: Sometime I indiscriminently display or refer to word in
        //   little endian storage format, or in integer format (ie MSB first).
        int numbStrings = LEW4(xml, 4 * 4);

        // StringIndexTable starts at offset 24x, an array of 32 bit LE offsets
        // of the length/string data in the StringTable.
        int sitOff = 0x24;  // Offset of start of StringIndexTable

        // StringTable, each string is represented with a 16 bit little endian
        // character count, followed by that number of 16 bit (LE) (Unicode) chars.
        int stOff = sitOff + numbStrings * 4;  // StringTable follows StrIndexTable

        // XMLTags, The XML tag tree starts after some unknown content after the
        // StringTable.  There is some unknown data after the StringTable, scan
        // forward from this point to the flag for the start of an XML start tag.
        int xmlTagOff = LEW4(xml, 3 * 4);  // Start from the offset in the 3rd word.
        // Scan forward until we find the bytes: 0x02011000(x00100102 in normal int)
        for (int ii = xmlTagOff; ii < xml.length - 4; ii += 4) {
            if (LEW4(xml, ii) == sStartTag) {
                xmlTagOff = ii;
                break;
            }
        }

        int off = xmlTagOff;
        while (off < xml.length) {
            int tag = LEW4(xml, off);
            int nameSi = LEW4(xml, off + 5 * 4);
            String name = compXmlString(xml, sitOff, stOff, nameSi);

            switch (tag) {
                case sStartTag:
                    int numbAttrs = LEW4(xml, off + 7 * 4);  // Number of Attributes to follow
                    off += 9 * 4;  // Skip over 6+3 words of sStartTag data

                    // Look for the Attributes
                    StringBuffer sb = new StringBuffer();
                    for (int ii = 0; ii < numbAttrs; ii++) {
                        int attrNameSi = LEW4(xml, off + 1 * 4);  // AttrName String Index
                        int rawValue = LEW4(xml, off + 2 * 4); // AttrValue Str Ind, or FFFFFFFF
                        byte type = LEW1(xml, off + 3 * 4 + 3);
                        int data = LEW4(xml, off + 4 * 4);  // AttrValue ResourceId or dup AttrValue StrInd
                        off += 5 * 4;  // Skip over the 5 words of an attribute

                        String attrName = compXmlString(xml, sitOff, stOff, attrNameSi);
                        switch (type) {
                            case sTypeInt:
                                sb.append(" " + attrName + "=\"" + data + "\"");
                                break;
                            case sTypeString:
                                String attrValue = rawValue != -1
                                        ? compXmlString(xml, sitOff, stOff, rawValue)
                                        : "resourceID 0x" + Integer.toHexString(data);
                                sb.append(" " + attrName + "=\"" + attrValue + "\"");
                                break;
                        }
                    }
                    resultXml.append("<" + name + sb + ">");
                    break;
                case sEndTag:
                    off += 6 * 4;  // Skip over 6 words of sEndTag data
                    resultXml.append("</" + name + ">");
                    break;
                case sEndDocTag:
                    return resultXml.toString();
                default:
                    LogUtils.debug(Manifest.class.getSimpleName(), "  Unrecognized tag code '" + Integer.toHexString(tag)
                            + "' at offset " + off);
                    return resultXml.toString();
            }
        }

        return resultXml.toString();
    }

    private static String compXmlString(byte[] xml, int sitOff, int stOff, int strInd) {
        if (strInd < 0) return null;
        int strOff = stOff + LEW4(xml, sitOff + strInd * 4);
        return compXmlStringAt(xml, strOff);
    }

    private static String compXmlStringAt(byte[] arr, int strOff) {
        int strLen = arr[strOff + 1] << 8 & 0xff00 | arr[strOff] & 0xff;
        byte[] chars = new byte[strLen];
        for (int ii = 0; ii < strLen; ii++) {
            chars[ii] = arr[strOff + 2 + ii * 2];
        }
        return new String(chars);  // Hack, just use 8 byte chars
    }

    // convert to little endian
    private static byte LEW1(byte[] arr, int off) {
        return arr[off];
    }

    private static short LEW2(byte[] arr, int off) {
        return (short) (arr[off + 1] << 8 & 0xff00 | arr[off] & 0xFF);
    }

    private static int LEW4(byte[] arr, int off) {
        return arr[off + 3] << 24 & 0xff000000 | arr[off + 2] << 16 & 0xff0000
                | arr[off + 1] << 8 & 0xff00 | arr[off] & 0xFF;
    }
}
