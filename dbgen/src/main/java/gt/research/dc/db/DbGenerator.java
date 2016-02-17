package gt.research.dc.db;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class DbGenerator {
    private static final String sDbRoot = "D:\\Workbench\\DCommand\\biz\\src\\main\\java";

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "gt.research.dc.core.db");

        addApk(schema);
        addIntf(schema);
        addComp(schema);

        new DaoGenerator().generateAll(schema, sDbRoot);
    }

    private static void addApk(Schema schema) {
        Entity apk = schema.addEntity("Apk");
        apk.addStringProperty("id").notNull().primaryKey();
        apk.addStringProperty("version").notNull();
        apk.addStringProperty("url").notNull();
        apk.addBooleanProperty("latest").notNull();
        apk.addStringProperty("pkgName");
    }

    private static void addIntf(Schema schema) {
        Entity intf = schema.addEntity("Intf");
        intf.addStringProperty("intf").notNull().primaryKey();
        intf.addStringProperty("impl").notNull();
        intf.addStringProperty("apk").notNull();
    }

    private static void addComp(Schema schema) {
        Entity comp = schema.addEntity("Comp");
        comp.addStringProperty("comp").notNull().primaryKey();
        comp.addStringProperty("apk").notNull();
        comp.addStringProperty("type").notNull();
    }
}
