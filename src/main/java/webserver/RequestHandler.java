package webserver;

import controller.Controller;
import controller.ControllerMapper;
import http.HttpRequest;
import http.HttpResponse;
import java.io.*;
import java.net.Socket;
import java.nio.channels.IllegalBlockingModeException;
import java.util.logging.Level;
import java.util.logging.Logger;
import structure.StartLine;

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

            System.out.println("DDD");

            HttpRequest httpRequest = new HttpRequest(br);

            System.out.println(httpRequest);
            System.out.println("HEY");

            Controller controller = controllerMapper.getController(httpRequest);

            HttpResponse httpResponse = controller.runLogic(httpRequest);

            sendHttpResponse(dos, httpResponse);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
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
            System.out.println("SENT");
            dos.write(httpResponse.getResponse());
            dos.write(httpResponse.getBody());
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}