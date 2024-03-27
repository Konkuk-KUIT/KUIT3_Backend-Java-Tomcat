package webserver;

import controller.*;
import db.MemoryUserRepository;
import db.Repository;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import static webserver.HttpMethod.GET;
import static webserver.HttpMethod.POST;


public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    private final Repository repository;
    private Controller controller;
    public RequestHandler(Socket connection) {
        this.connection = connection;
        repository = MemoryUserRepository.getInstance();
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            DataOutputStream dos = new DataOutputStream(out);
            HttpResponse httpResponse = new HttpResponse(dos);

            HttpRequest httpRequest = HttpRequest.from(br);
            String url = httpRequest.getPath();
            String method = httpRequest.getMethod();

            // 요구 사항 1
            if (httpRequest.getMethod().equals(GET.getMethod()) && httpRequest.getPath().endsWith(".html")) {
                controller = new ForwardController();
                System.out.println("ForwardController :"+controller);
            }
            if (url.equals("/")) {
                controller = new HomeController();
                System.out.println("HomeController :"+controller);
            }
            //요구 사항 3
            if(method.equals(POST.getMethod()) && url.equals("/user/signup")){
                controller = new SignUpController();
                System.out.println("SignUpController :"+controller);
            }
            //요구 사항 5
            if (url.equals("/user/login.html")){
                controller = new LoginHtmlController();
                System.out.println("LoginHtmlController: " + controller);
            }

            if (url.equals("/user/login")) {
                controller = new LoginController();
                System.out.println("LoginController :"+controller);
            }


            // 요구 사항 6
            if (url.equals("/user/userList")) {
                controller = new ListController();
                System.out.println("ListController :"+controller);
            }

            // 요구 사항 7번
            if (method.equals(GET.getMethod()) && url.endsWith(".css")) {
                controller = new CssController();
                System.out.println("CssController :"+controller);
            }

            // image
            if (method.equals(GET.getMethod()) && url.endsWith(".jpeg")) {
                controller = new ImageController();
                System.out.println("ImageController :"+controller);
            }

            controller.execute(httpRequest, httpResponse);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }
}