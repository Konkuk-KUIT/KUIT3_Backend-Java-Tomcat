import org.junit.jupiter.api.Test;
import structure.StartLine;

public class StartLineTest {

    @Test
    void parsingTest() {
        String line = "POST /index.html?a=1&b=2 HTTP/1.1";

        StartLine startLine = new StartLine(line);

        System.out.println(startLine.toString());
    }
}
