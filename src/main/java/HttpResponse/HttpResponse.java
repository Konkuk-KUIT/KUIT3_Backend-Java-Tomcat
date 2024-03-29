package HttpResponse;

// request와 헤더 구조 동일
import HttpRequest.MessageHeader;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class HttpResponse {
    private MessageStartLine startLine;
    private MessageHeader header;
    private String body;

    private DataOutputStream dos;


    public HttpResponse(OutputStream outputStream) throws IOException {
        this.dos = new DataOutputStream(outputStream);
    }

    public int getContentLength() {
        return this.header.getContentLength();
    }

    public String getCookie() {
        return this.header.getCookie();
    }

    public String getBody() {
        return this.body;
    }

    private void writeHeader(Path path, String method) {
        try {
            // write header
            // content-length는 해당 path의 파일의 크기
            long contentLength = Files.size(path);
            ResponseHeaderConstants headerConstant = ResponseHeaderConstants.valueOf(method.toUpperCase());

            dos.writeBytes(headerConstant.getValue());
            dos.writeBytes(ResponseHeaderConstants.CONTENT_TYPE_HTML.getValue());
            dos.writeBytes("Content-Length: " + contentLength + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void writeBody(Path path) {
        byte[] byteBody = new byte[0];
        try {
            byteBody = Files.readAllBytes(path);
            dos.write(byteBody, 0, byteBody.length);
            dos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void forward(String path) {
        // path에 해당하는 html 파일을 보여줌 (GET에 대한 요청)
        // .css로 끝나면 css파일로 해결해야 함
        Path filePath = Paths.get(path);
        writeHeader(filePath, "start_line_200");
        writeBody(filePath);
    }

    public void redirect(String path) {
        // path에 해당하는 html로 redirect시킴 (POST에 대한 요청)
        Path filePath = Paths.get(path);
        writeHeader(filePath, "start_line_302");
        writeBody(filePath);
    }

}
