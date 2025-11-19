import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class XmlAddressReader implements AddressReader {

    @Override
    public Stream<Address> read(Path file) throws IOException {
        List<Address> out = new ArrayList<>();
        try {
            SAXParserFactory f = SAXParserFactory.newInstance();
            SAXParser p = f.newSAXParser();

            p.parse(file.toFile(), new DefaultHandler() {
                String city, street, house, floors;
                StringBuilder buf = new StringBuilder();

                @Override public void startElement(String uri, String local, String qName, Attributes atts) {
                    buf.setLength(0);
                }

                @Override public void characters(char[] ch, int start, int length) {
                    buf.append(ch, start, length);
                }

                @Override public void endElement(String uri, String local, String qName) {
                    String tag = qName.toLowerCase(Locale.ROOT);
                    String val = buf.toString().trim();
                    switch (tag) {
                        case "city": case "город": case "sehir":   city = val; break;
                        case "street": case "улица": case "cadde": street = val; break;
                        case "house": case "дом": case "bina":     house = val; break;
                        case "floors": case "этажей": case "kat":  floors = val; break;
                        case "address":
                            try {
                                int f = Integer.parseInt(floors);
                                out.add(new Address(city, street, house, f));
                            } catch (Exception ignore) {}
                            city = street = house = floors = null;
                            break;
                    }
                }
            });
        } catch (SAXException | RuntimeException e) {
            throw new IOException("XML parse error: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new IOException(e);
        }
        return out.stream();
    }
}
