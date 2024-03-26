import org.junit.jupiter.api.Test;
import structure.RequestStartLine;

public class StartLineTest {

    @Test
    void parsingTest() {
        String line = "POST /index.html?a=1&b=2 HTTP/1.1";

        RequestStartLine startLine = new RequestStartLine(line);

        System.out.println(startLine.toString());
    }
}
