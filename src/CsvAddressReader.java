import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class CsvAddressReader implements AddressReader {

    @Override
    public Stream<Address> read(Path file) throws IOException {
        BufferedReader br = Files.newBufferedReader(file, StandardCharsets.UTF_8);
        List<Address> out = new ArrayList<>();
        String header = br.readLine();
        if (header == null) {
            br.close();
            return out.stream();
        }
        Map<String,Integer> map = headerMap(parseCsvLine(header));

        String line;
        while ((line = br.readLine()) != null) {
            try {
                String[] cols = parseCsvLine(line);
                String city   = get(cols, map, "city");
                String street = get(cols, map, "street");
                String house  = get(cols, map, "house");
                String floorsStr = get(cols, map, "floors");
                int floors = Integer.parseInt(floorsStr);
                out.add(new Address(city, street, house, floors));
            } catch (Exception ex) {
                // bozuk satırı atla
            }
        }
        br.close();
        return out.stream();
    }

    private static Map<String,Integer> headerMap(String[] headers) {
        Map<String,Integer> map = new HashMap<>();
        for (int i=0;i<headers.length;i++) {
            map.put(headers[i].trim().toLowerCase(Locale.ROOT), i);
        }
        // çok dillilik için basit takma adlar
        alias(map, "город", "city");
        alias(map, "sehir", "city");
        alias(map, "улица", "street");
        alias(map, "cadde", "street");
        alias(map, "дом", "house");
        alias(map, "bina", "house");
        alias(map, "этажей", "floors");
        alias(map, "kat", "floors");
        return map;
    }
    private static void alias(Map<String,Integer> map, String key, String to) {
        if (map.containsKey(key) && !map.containsKey(to)) map.put(to, map.get(key));
    }

    private static String get(String[] cols, Map<String,Integer> map, String key) {
        Integer idx = map.get(key);
        if (idx == null || idx < 0 || idx >= cols.length) throw new IllegalArgumentException("missing " + key);
        return cols[idx];
    }

    // Tırnak/kaçış destekli sağlam CSV ayrıştırıcı
    static String[] parseCsvLine(String line) {
        List<String> cells = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i=0;i<line.length();i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i+1 < line.length() && line.charAt(i+1) == '"') {
                    sb.append('"'); i++; // kaçışlı tırnak
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                cells.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        cells.add(sb.toString().trim());
        return cells.toArray(new String[0]);
    }
}
