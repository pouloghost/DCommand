package gt.research.dc.core.command.verifier.original;

/**
 * Created by ayi.zty on 2016/2/2.
 */
public class Chunk {
    public String value;
    public int start;
    public int end;

    public Chunk() {

    }

    public Chunk(String value, int start, int end) {
        this.value = value;
        this.start = start;
        this.end = end;
    }
}
