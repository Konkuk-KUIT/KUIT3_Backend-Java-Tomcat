package webserver;

import controller.Controller;
import controller.ControllerMapper;
import http.HttpRequest;
import http.HttpResponse;
import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{    // Ran By Thread
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private static final ControllerMapper controllerMapper = new ControllerMapper();

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){   // Socket Connection, shutdown... check
            BufferedReader br = new BufferedReader(new InputStreamReader(in));  // 데이터 냠냠
            DataOutputStream dos = new DataOutputStream(out);   // 보낼 출구 뚫기


            HttpRequest httpRequest = new HttpRequest(br);

            Controller controller = controllerMapper.getController(httpRequest);

            HttpResponse httpResponse = controller.runLogic(httpRequest);

            sendHttpResponse(dos, httpResponse);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 OK \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void sendHttpResponse(DataOutputStream dos, HttpResponse httpResponse) {
        try {
            dos.write(httpResponse.getHeader());
            dos.write(httpResponse.getBody(), 0, httpResponse.getBodyLength());
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}