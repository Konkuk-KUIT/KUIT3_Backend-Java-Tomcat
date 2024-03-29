package http.util;

import HttpRequest.HttpRequest;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpRequestTest {
    private BufferedReader bufferedReaderFromFile(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
    }

    @Test
    void inputRequest() throws IOException {
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile("./src/test/java/resources/HttpRequestMessage"));
        System.out.println(httpRequest.getBody());
        assertEquals("/user/create", httpRequest.getUrl());
        // 추가적인 테스트 코드 작성
    }
}
