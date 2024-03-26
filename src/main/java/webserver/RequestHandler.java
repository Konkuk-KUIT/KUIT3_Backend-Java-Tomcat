package webserver;

import http.util.HttpRequestUtils;
import http.util.IOUtils;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

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

            String line= IOUtils.readData(br,40);
            String[] queryStrings = line.split(" ");
            String url=queryStrings[1];

            Path path=Paths.get("C:/Users/home/Desktop/2024_1학기/kuit/2주차/KUIT3_Backend-Java-Tomcat/webapp/index.html");;
            if(Objects.equals(url, "/index.html")){
                path=Paths.get("C:/Users/home/Desktop/2024_1학기/kuit/2주차/KUIT3_Backend-Java-Tomcat/webapp/index.html");
            }
            else if(Objects.equals(url, "/user/form.html")){
                path=Paths.get("C:/Users/home/Desktop/2024_1학기/kuit/2주차/KUIT3_Backend-Java-Tomcat/webapp/user/form.html");
            }

            byte[] body = Files.readAllBytes(path);
            response200Header(dos, body.length);
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

}