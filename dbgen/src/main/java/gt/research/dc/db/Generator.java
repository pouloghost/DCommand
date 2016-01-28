package gt.research.dc.db;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class Generator {
    private static final String sDbRoot = "D:\\Workbench\\DCommand\\biz\\src\\main\\java";
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "gt.research.core.db");

        addApk(schema);
        addIntf(schema);

        new DaoGenerator().generateAll(schema, sDbRoot);
    }

    private static void addApk(Schema schema) {
        Entity apk = schema.addEntity("Apk");
        apk.addStringProperty("id").notNull().primaryKey().index();
        apk.addStringProperty("version").notNull();
        apk.addStringProperty("url").notNull();
    }

    private static void addIntf(Schema schema){
        Entity intf = schema.addEntity("Intf");
        intf.addStringProperty("intf").notNull().primaryKey().index();
        intf.addStringProperty("impl").notNull();
        intf.addStringProperty("apk").notNull();
    }
}
