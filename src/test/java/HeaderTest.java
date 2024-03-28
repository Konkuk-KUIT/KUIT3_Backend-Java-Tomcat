import java.util.HashMap;
import org.junit.jupiter.api.Test;
import structure.Header;
import structure.HeaderKey;

public class HeaderTest {
    @Test
    void getHeader() {
        Header header = new Header();
        header.addAttribute(HeaderKey.CONTENT_LENGTH, "100");
        System.out.println(header.getHeader());
    }
}
