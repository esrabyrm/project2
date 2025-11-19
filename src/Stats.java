import java.util.HashMap;
import java.util.Map;

public class Stats {
    public final Map<String,Integer> dupCounts = new HashMap<>();
    public final Map<String,int[]>  floorsByCity = new HashMap<>();
    public long readCount;
    public long skippedCount;
    public long elapsedMs;
}
