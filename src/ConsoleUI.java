import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Stream;

public class ConsoleUI {
    private final AddressReader csvReader;
    private final AddressReader xmlReader;
    private final StatsService statsService;

    public ConsoleUI(AddressReader csvReader, AddressReader xmlReader, StatsService statsService) {
        this.csvReader = csvReader;
        this.xmlReader = xmlReader;
        this.statsService = statsService;
    }

    public void run() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("File path (or q to quit): ");
            String line = sc.nextLine().trim();
            if (line.equalsIgnoreCase("q")) {
                System.out.println("Bye.");
                return;
            }
            if (line.isEmpty()) {
                System.out.println("Empty path.");
                continue;
            }
            Path file = Path.of(line);
            if (!Files.exists(file)) {
                System.out.println("File not found.");
                continue;
            }

            String name = file.getFileName().toString().toLowerCase(Locale.ROOT);
            AddressReader reader = null;
            if (name.endsWith(".csv")) reader = csvReader;
            else if (name.endsWith(".xml")) reader = xmlReader;
            else {
                System.out.println("Unsupported file type. Use .csv or .xml");
                continue;
            }

            try {
                Instant t0 = Instant.now();
                try (Stream<Address> stream = reader.read(file)) {
                    Stats stats = statsService.compute(stream);
                    stats.elapsedMs = Duration.between(t0, Instant.now()).toMillis();
                    print(stats);
                }
            } catch (IOException e) {
                System.out.println("I/O error: " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("Parsing error: " + e.getMessage());
            }
        }
    }

    private void print(Stats s) {
        System.out.println();
        System.out.println("Duplicates (count >= 2):");
        s.dupCounts.entrySet().stream()
                .filter(e -> e.getValue() >= 2)
                .sorted((a,b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(50)
                .forEach(e -> System.out.printf("  %s -> %d%n", e.getKey(), e.getValue()));

        System.out.println();
        System.out.println("Floors per city:");
        s.floorsByCity.forEach((city, bins) -> {
            System.out.printf("  %-12s: 1F=%-5d 2F=%-5d 3F=%-5d 4F=%-5d 5F=%-5d%n",
                    city, bins[1], bins[2], bins[3], bins[4], bins[5]);
        });

        System.out.println();
        System.out.printf("Processed: %,d rows, skipped: %,d%n", s.readCount, s.skippedCount);
        System.out.printf("Processing time: %d ms%n", s.elapsedMs);
        System.out.println();
    }
}
