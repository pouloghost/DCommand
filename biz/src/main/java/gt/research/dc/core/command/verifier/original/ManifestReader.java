package gt.research.dc.core.command.verifier.original;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ayi.zty on 2016/2/2.
 */
public class ManifestReader {
    private static final char sSplitter = ':';
    private static final char sLineBreak0 = '\r';
    private static final char sLineBreak1 = '\n';
    private static final char sWhiteSpace = ' ';

    private static final String sName = "NAME";
    private static final String sAlgSHA1 = "SHA1";
    private static final String sAlgSHA256 = "SHA256";

    private byte[] mBytes;
    private Map<String, String> mHeaders;
    private Map<String, Chunk> mChunks;
    private int mOffset;
    private int mStart;
    private String mAlgorithm;

    public ManifestReader(byte[] bytes) {
        mBytes = bytes;
        mHeaders = new HashMap<>();
        mChunks = new HashMap<>();
        mOffset = 0;
        mStart = 0;
        readHeader();
        readChunk();
    }

    private void readHeader() {
        String name = readName();
        while (null != name) {
            String value = readValue();
            if (null == value) {
                return;
            }
            mHeaders.put(name, value);
            name = readName();
        }
    }

    private void readChunk() {
        int start = mStart;
        String name = readName();
        while (null != name) {
            if (!sName.equals(name)) {
                mChunks.clear();
                return;
            }
            name = readValue();
            String value = readName();
            if (value.contains(sAlgSHA1)) {
                mAlgorithm = sAlgSHA1;
            } else if (value.contains(sAlgSHA256)) {
                mAlgorithm = sAlgSHA256;
            }
            value = readValue();
            if (null == value) {
                mChunks.clear();
                return;
            }
            mChunks.put(name, new Chunk(value, start, mOffset));
            readValue();
            start = mOffset;
            name = readName();
        }
    }

    private String readName() {
        if (sLineBreak1 == mBytes[mOffset] || sLineBreak0 == mBytes[mOffset]) {
            return null;
        }
        mStart = mOffset;
        while (mOffset < mBytes.length) {
            if (sSplitter != mBytes[mOffset++]) {
                continue;
            }
            if (sWhiteSpace != mBytes[mOffset++]) {
                return null;
            }
            return new String(mBytes, mStart, mOffset - mStart - 1, StandardCharsets.US_ASCII);
        }
        return null;
    }

    private String readValue() {
        mStart = mOffset;
        while (mOffset < mBytes.length) {
            if (sLineBreak1 != mBytes[mOffset++]) {
                continue;
            }
            int goBack = sLineBreak0 == mBytes[mOffset - 2] ? 3 : 2;
            if (mOffset - mStart - goBack <= 0) {
                return null;
            }
            return new String(mBytes, mStart, mOffset - mStart - goBack, StandardCharsets.UTF_8);
        }
        return null;
    }

    public String getAlgorithm() {
        return mAlgorithm;
    }

    public Map<String, String> getHeader() {
        return mHeaders;
    }

    public Map<String, Chunk> getChunks() {
        return mChunks;
    }
}
