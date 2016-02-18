package gt.research.dc.util;

import gt.research.dc.core.AbsCommand;
import gt.research.dc.core.db.Apk;
import gt.research.dc.data.CommandContext;

/**
 * Created by ayi.zty on 2016/2/3.
 */
public class CommandUtils {
    public static <T extends AbsCommand> T constructCommand(Class<T> intf, Apk info, ClassLoader classLoader) {
//        try {
            // FIXME: 2016/2/18
//            T command = (T) classLoader.loadClass(info.getImplement(intf.getName())).newInstance();

            CommandContext commandContext = new CommandContext();
            commandContext.classLoader = classLoader;
            commandContext.apkInfo = new Apk(info.getId(), info.getUrl(), info.getPkgName(),
                    info.getTimestamp());
//            command.setContext(commandContext);
//            return command;
//        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
//            LogUtils.exception(e);
            return null;
//        }
    }
}
