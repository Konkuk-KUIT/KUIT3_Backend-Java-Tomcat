package webserver;

import java.io.IOException;

public interface Controller {
    void execute(HttpRequest req, HttpResponse res) throws IOException;
}
