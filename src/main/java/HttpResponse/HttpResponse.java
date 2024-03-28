package HttpResponse;

// request와
import HttpRequest.MessageHeader;

public class HttpResponse {
    private MessageStartLine startLine;
    private MessageHeader header;
    private String body;
}
