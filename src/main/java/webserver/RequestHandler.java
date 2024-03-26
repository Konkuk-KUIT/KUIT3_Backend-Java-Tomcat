package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static http.util.IOUtils.readData;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            // InputStream의 요청을 읽어와 파싱
            String startLine = br.readLine();
            // String readData = readData(br, startLine.length());
            String[] startLines = startLine.split(" ");
            // String requestMethod = startLines[0];
            String requestUrl = startLines[1];

            String baseUrl = "/Users/seohyun/Desktop/Dev/KUIT3-Backend/KUIT3_Backend-Java-Tomcat/webapp/";
            String url = "";

            // requestUrl이 빈 값인 경우
            if(Objects.equals(requestUrl, "/")) {
                String indexUrl = "index.html";
                url = baseUrl + indexUrl;

//                log.log(Level.INFO, "빈 값, 경로: " + url);
            }
            // requestUrl이 존재하는 경우
            else {
                url = baseUrl + requestUrl;
                if(noFile(url)) {
                    return;
                }
//                log.log(Level.INFO, "빈 값 아님, 경로: " + url);
            }

            // 파일이 존재하면 파일 내용을 읽어옴
            byte[] body = Files.readAllBytes(Paths.get(url));

//            response200Header(dos, body.length);
            responseBody(dos, body);

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

    private boolean noFile(String url){
        // file이 존재하는지 확인
        File file = new File(url);
        // 파일이 존재하지 않으면 true 반환
        return !file.exists();
    }

}