import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface AddressReader {
    Stream<Address> read(Path file) throws IOException;
}
