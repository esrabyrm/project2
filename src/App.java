public class App {
    public static void main(String[] args) {
        ConsoleUI ui = new ConsoleUI(
                new CsvAddressReader(),
                new XmlAddressReader(),
                new StatsService());
        ui.run();
    }
}
