package webserver;

import controller.Controller;
import controller.ControllerMapper;
import java.io.*;
import java.net.Socket;
import java.util.Objects;
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

            Controller controller = controllerMapper.getController(br);

            byte[] body = controller.runLogic(br, dos); // TODO: Body 안하고 바로 외부 stream으로 보내면 좋을거 같다.

//            byte[] body = null; // TODO: null??   return 값
//
//
//            if(isIndexRequest(br)) {
//                body = sendByteIndexHtml();
//            }
            response200Header(dos, body.length);    // Header에 필요한 body의 길이를 넣어주면 얘가 그걸 토대로 Output stream을 방출
            responseBody(dos, body);        // 진짜 body내용을 방출

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private Boolean isIndexRequest(BufferedReader bufferedReader) {
        try {
            // HTTP message의 start-Line 벗겨
            StartLine startLine = new StartLine(bufferedReader.readLine()); //TODO: 모든 HTTP Method에 대해 다 path를 읽어야 하나? -> HTTP method check unnecessary?
            if(Objects.equals(startLine.getPath(), "/")
                    || Objects.equals(startLine.getPath(), "/index.html")) {
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