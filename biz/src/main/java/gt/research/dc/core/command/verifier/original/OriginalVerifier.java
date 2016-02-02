package gt.research.dc.core.command.verifier.original;

import android.content.Context;

import org.apache.harmony.security.utils.JarUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import gt.research.dc.core.command.verifier.IApkVerifier;
import gt.research.dc.core.command.verifier.OnVerifiedListener;
import gt.research.dc.core.constant.FileConstants;
import gt.research.dc.util.LogUtils;
import libcore.io.Base64;

/**
 * Created by ayi.zty on 2016/2/1.
 */
public class OriginalVerifier implements IApkVerifier {
    private static final String sPrefixCert = "META-INF/CERT";
    private static final String sSuffixRsa = ".RSA";
    private static final String sSuffixSf = ".SF";

    private static final String sPrefixName = "Name:";
    private static final String sPrefixSHA1 = "SHA1-Digest:";
    private static final String sAlgSHA1 = "SHA1";
    private static final String sPrefixSHA256 = "SHA-256-Digest:";
    private static final String sAlgSHA256 = "SHA256";

    private static final String sManifestDigest = "-Digest-Manifest";

    private String mAlgorithm;
    private int mOffset = 0;

    @Override
    public void verify(Context context, String path, OnVerifiedListener listener) {
        File apkFile = new File(context.getDir(FileConstants.DIR_DOWNLOAD, Context.MODE_PRIVATE), path);
        try {
            // init
            JarFile jarFile = new JarFile(apkFile);
            Enumeration<JarEntry> entries = jarFile.entries();
            Set<JarEntry> normalFiles = new HashSet<>();
            Map<String, byte[]> certRsas = new HashMap<>();
            Map<String, byte[]> certSfs = new HashMap<>();
            byte[] manifest = null;
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.startsWith(sPrefixCert)) {
                    addToMap(jarFile, name, entry, certRsas, certSfs);
                    continue;
                }
                if (name.equals(JarFile.MANIFEST_NAME)) {
                    manifest = readJarEntry(jarFile, entry);
                    continue;
                }
                normalFiles.add(entry);
            }
            boolean isCertOk = verifyCertificate(certRsas, certSfs, manifest);
            for (JarEntry entry : normalFiles) {

            }
        } catch (IOException e) {
            LogUtils.exception(e);
        }
    }

    private boolean verifyCertificate(Map<String, byte[]> certRsas, Map<String, byte[]> certSfs, byte[] manifest) {
        if (certRsas.size() != certSfs.size()) {
            return false;
        }
        Set<String> keys = certRsas.keySet();
        for (String key : keys) {
            byte[] rsa = certRsas.get(key);
            byte[] sf = certSfs.get(key);
            if (null == sf) {
                return false;
            }
            try {
                Certificate[] signerCertChain = JarUtils.verifySignature(
                        new ByteArrayInputStream(sf), new ByteArrayInputStream(rsa));
                if (null != signerCertChain) {
                    //// TODO: 2016/2/1 JarVerifier:298
                }
            } catch (IOException | GeneralSecurityException e) {
                LogUtils.exception(e);
                return false;
            }
            ManifestReader reader = new ManifestReader(sf);
            Map<String, String> header = reader.getHeader();
            Map<String, Chunk> digests = reader.getChunks();
            String manifestDigest = header.get(mAlgorithm + sManifestDigest);
            if (null == manifestDigest) {
                return false;
            }
            if (!verifyData(manifestDigest, manifest, 0, manifest.length)) {
                return false;
            }
            for (String name : digests.keySet()) {
                Chunk chunk = digests.get(name);
                if (!verifyData(chunk.value, manifest, chunk.start, chunk.end)) {
                    return false;
                }
            }
        }
        return true;
    }


    private void addToMap(JarFile zipFile, String name, JarEntry entry, Map<String, byte[]> certRsas, Map<String, byte[]> certSfs) {
        int index = name.indexOf(sSuffixRsa);
        if (-1 != index) {
            byte[] bytes = readJarEntry(zipFile, entry);
            if (null != bytes) {
                certRsas.put(name.substring(0, index), bytes);
            }
        } else if (-1 != (index = name.indexOf(sSuffixSf))) {
            byte[] bytes = readJarEntry(zipFile, entry);
            if (null != bytes) {
                certSfs.put(name.substring(0, index), bytes);
            }
        }
    }

    private byte[] readJarEntry(JarFile zipFile, JarEntry entry) {
        InputStream in = null;
        try {
            in = zipFile.getInputStream(entry);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int count;
            while ((count = in.read(buffer)) != -1) {
                bytes.write(buffer, 0, count);
            }
            return bytes.toByteArray();
        } catch (IOException e) {
            LogUtils.exception(e);
            return null;
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    LogUtils.exception(e);
                }
            }
        }
    }

    private boolean verifyData(String hash, byte[] data, int start, int end) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(mAlgorithm);
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
        md.update(data, start, end - start);
        byte[] b = md.digest();
        byte[] hashBytes = hash.getBytes(StandardCharsets.ISO_8859_1);
        return MessageDigest.isEqual(b, Base64.decode(hashBytes));
    }
}
