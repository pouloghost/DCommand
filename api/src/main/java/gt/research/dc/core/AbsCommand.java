package gt.research.dc.core;

/**
 * Created by ayi.zty on 2016/1/26.
 */
public abstract class AbsCommand {
    private CommandContext mContext;

    public void setContext(CommandContext context) {
        mContext = context;
    }

    public CommandContext getContext() {
        return mContext;
    }
}
