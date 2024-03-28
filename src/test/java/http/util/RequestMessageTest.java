package http.util;

import HttpRequest.RequestMessage;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestMessageTest {
    private BufferedReader bufferedReaderFromFile(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
    }

    @Test
    void inputRequest() throws IOException {
        RequestMessage httpRequest = RequestMessage.from(bufferedReaderFromFile("./src/test/java/resources/HttpRequestMessage"));
        assertEquals("/user/create", httpRequest.getStartLine().getUrl());
        // 추가적인 테스트 코드 작성
    }
}
