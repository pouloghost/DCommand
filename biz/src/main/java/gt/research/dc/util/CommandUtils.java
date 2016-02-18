package gt.research.dc.util;

import gt.research.dc.core.AbsCommand;
import gt.research.dc.data.CommandContext;

/**
 * Created by ayi.zty on 2016/2/3.
 */
public class CommandUtils {
    public static <T extends AbsCommand> T constructCommand(Class<T> intf, ApkInfo info, ClassLoader classLoader) {
        try {
            T command = (T) classLoader.loadClass(info.getImplement(intf.getName())).newInstance();

            CommandContext commandContext = new CommandContext();
            commandContext.classLoader = classLoader;
            commandContext.apkInfo = new ApkInfo(info);
            command.setContext(commandContext);
            return command;
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            LogUtils.exception(e);
            return null;
        }
    }
}
