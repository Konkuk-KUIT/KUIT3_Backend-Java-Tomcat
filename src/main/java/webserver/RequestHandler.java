package webserver;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{    // Ran By Thread
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){   // Socket Connection, shutdown... check
            BufferedReader br = new BufferedReader(new InputStreamReader(in));  // 데이터 냠냠
            DataOutputStream dos = new DataOutputStream(out);   // 보낼 출구 뚫기

            byte[] body = null; // TODO: null??
            if(isIndexRequest(br)) {
                body = sendByteIndexHtml();
            }
            response200Header(dos, body.length);
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private Boolean isIndexRequest(BufferedReader bufferedReader) {
        try {
            String input = bufferedReader.readLine();
            if(Objects.equals(input, "http://localhost:{port}/")
                    || Objects.equals(input, "http://localhost:{port}/index.html")) {      // TODO: 이거 맞아욤?
                return true;
            }
        } catch (IOException e) {
            log.log(Level.INFO, e.getMessage());
        }
        return false;
    }

    private byte[] sendByteIndexHtml() {
        try (FileInputStream input = new FileInputStream("/Users/tony/IdeaProjects/KUIT3_Backend-Java-Tomcat/webapp/index.html")) {    // TODO: 얘 고쳐라, Path.of() 몰라
            return input.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
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

}