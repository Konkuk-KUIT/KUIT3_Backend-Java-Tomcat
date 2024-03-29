package webserver;

import org.junit.jupiter.api.Test;
import java.io.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.*;

class HttpRequestTest {

    private BufferedReader bufferedReaderFromFile(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
    }
    @Test
    void URL_테스트() throws Exception {

        // given
        String testFilePath = "src/test/java/resource/example.http";

        // when
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile(testFilePath));

        // then
        assertEquals("/user/create", httpRequest.getUrl());

    }

    @Test
    void 쿠키_테스트() throws Exception {

        // given
        String testFilePath = "src/test/java/resource/example.http";

        // when
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile(testFilePath));

        // then
        assertEquals("", httpRequest.getCookie());
    }

    @Test
    void 메서드_테스트() throws Exception {

        // given
        String testFilePath = "src/test/java/resource/example.http";

        // when
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile(testFilePath));

        // then
        assertEquals("POST",httpRequest.getMethod());
    }

    @Test
    void 쿼리_테스트() throws Exception {

        // given
        String testFilePath = "src/test/java/resource/example.http";

        // when
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile(testFilePath));

        // then
        assertEquals("userId=jw&password=password&name=jungwoo",httpRequest.getQueryString());
    }

}