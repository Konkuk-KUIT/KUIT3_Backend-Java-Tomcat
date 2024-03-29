import org.junit.jupiter.api.Test;
import http.structure.Header;
import http.structure.HeaderKey;

public class HeaderTest {
    @Test
    void getHeader() {
        Header header = new Header();
        header.addAttribute(HeaderKey.CONTENT_LENGTH, "100");
//        System.out.println(header.getHeader());
    }
}
