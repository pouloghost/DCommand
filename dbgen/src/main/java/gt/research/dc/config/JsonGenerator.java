package gt.research.dc.config;

import com.alibaba.fastjson.JSON;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;

import gt.research.dc.core.IVersion;
import gt.research.dc.data.ApkInfo;
import gt.research.dc.data.Config;

/**
 * Created by ayi.zty on 2016/1/29.
 */
public class JsonGenerator {
    public static void main(String[] args) throws Exception {
        Config config = new Config();
        ApkInfo apkInfo = new ApkInfo();
        apkInfo.id = "IVersion";
        apkInfo.url = "https://os.alipayobjects.com/rmsportal/UkkweRpRqckOHqu.apk";
        apkInfo.timestamp = System.currentTimeMillis();
        apkInfo.interfaces = new HashMap<>();
        apkInfo.interfaces.put(IVersion.class.getName(), "gt.research.export.VersionImpl");
        config.update = Arrays.asList(apkInfo);
        config.delete = Arrays.asList("id");

        File file = new File("config.json");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(JSON.toJSONString(config));
        writer.flush();
        writer.close();
    }
}
