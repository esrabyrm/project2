import java.util.Locale;

public class Address {
    public final String city;
    public final String street;
    public final String house;
    public final int floors;

    public Address(String city, String street, String house, int floors) {
        this.city = city;
        this.street = street;
        this.house = house;
        this.floors = floors;
    }

    public String normalizedKey() {
        return (norm(city) + "|" + norm(street) + "|" + norm(house) + "|" + floors);
    }

    private static String norm(String s) {
        if (s == null) return "";
        return s.trim().replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }
}
