package http.webserver;

import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import webserver.RequestHandler;

public class RequestHandlerTest {

//    @Mock
//    Socket socket;

    @Test
    void runTest() throws IOException {
        // when TODO: 프린트가 아니라 직접 비교로 해봐 : 시간 있다면...
        Socket socket = Mockito.mock(Socket.class);
        when(socket.getInetAddress()).thenReturn(null);
        when(socket.getPort()).thenReturn(1);
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream("http://localhost:{port}/".getBytes()));
        ByteArrayOutputStream dos = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(dos);

        RequestHandler requestHandler = new RequestHandler(socket);

        requestHandler.run();
        System.out.println(dos);

    }
}
