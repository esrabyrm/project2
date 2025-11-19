import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class StatsService {

    public Stats compute(Stream<Address> stream) {
        Stats s = new Stats();
        AtomicLong read = new AtomicLong(0);
        AtomicLong skipped = new AtomicLong(0);

        stream.forEach(addr -> {
            try {
                read.incrementAndGet();

                String key = addr.normalizedKey();
                s.dupCounts.merge(key, 1, Integer::sum);

                if (addr.floors >= 1 && addr.floors <= 5) {
                    int[] bins = s.floorsByCity.computeIfAbsent(addr.city, c -> new int[6]);
                    bins[addr.floors]++;
                } else {
                    skipped.incrementAndGet();
                }
            } catch (Exception ex) {
                skipped.incrementAndGet();
            }
        });

        s.readCount = read.get();
        s.skippedCount = skipped.get();
        return s;
    }
}
