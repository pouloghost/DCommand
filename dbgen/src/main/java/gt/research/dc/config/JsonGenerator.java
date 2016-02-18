package gt.research.dc.config;

import com.alibaba.fastjson.JSON;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;

import gt.research.dc.data.Apk;
import gt.research.dc.data.Config;

/**
 * Created by ayi.zty on 2016/1/29.
 */
public class JsonGenerator {
    public static void main(String[] args) throws Exception {
        Config config = new Config();
        Apk apkInfo = new Apk();
        apkInfo.setId("IVersion");
        apkInfo.setUrl("https://os.alipayobjects.com/rmsportal/PvAXMBnwAkomkee.apk");
        apkInfo.setTimestamp(System.currentTimeMillis());
        config.update = Arrays.asList(apkInfo);
        config.delete = Arrays.asList("id");

        File file = new File("config.json");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(JSON.toJSONString(config));
        writer.flush();
        writer.close();
    }
}
