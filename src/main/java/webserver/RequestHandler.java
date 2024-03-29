    package webserver;

    import db.MemoryUserRepository;
    import db.Repository;
    import http.util.HttpRequestUtils;
    import http.util.IOUtils;
    import model.User;

    import java.io.*;
    import java.net.Socket;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.util.Map;
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

                //body에 html을 넣어서 내보낼 곳
                byte[] body = new byte[0];
                //로그인 시 user정보 저장할 공간
                Repository repository = MemoryUserRepository.getInstance();

                //http request parsing - 메서드 요청경로 분리
                //request의 구조는 startline, header, blank line, body로 구성되어 있음.
                String startLine = br.readLine();
                System.out.println("httpRequestLine : " + startLine);

                String[] startLines = startLine.split(" ");

                String method = startLines[0];
                String url = startLines[1];
                System.out.println(method + url);
                //여기까지 startLine에서 메서드와 url 가져오는 작업.

                int lengthOfBodyContent = 0;
                String cookie = "";

                while(true) {
                    String headerLine = br.readLine();
                    if (headerLine.isEmpty()){
                        break;
                    }
                    if (headerLine.split(": ")[0].equals("Content-Length")) {
                        lengthOfBodyContent = Integer.parseInt(headerLine.split(": ")[1]);
                        System.out.println(lengthOfBodyContent);
                    }
                    if (headerLine.startsWith("Cookie")) {
                        cookie = headerLine.split(": ")[1];
                    }

//                    if (headerLine.startsWith("Cookie")) {
//                        //로그인할 때 쿠키로 로그인 여부 및 상태 유지용
//                        //나중에는 쿠키의 생명 주기를 설정할 수도 있다!
//                        cookie = headerLine.split(": ")[1];
//                    }
                }
                //여기까지 헤더에서 필요한 정보 가져오는 작업(Content-Length)

                //일단 get(index,html)은 body가 없음.
                if (method.equals("GET") && url.endsWith(".html")) {
                    //body에 html 내용을 담아주면 됨. response를 해주면 되지.
                    //처음 들어갈 때는 "/"로만 받는듯...ㄹㅇ 꼴받는다.(꼭 sout으로 찍어보자...)
                    body = Files.readAllBytes(Path.of("./webapp" + url));
                }
                if(url.equals("/")) {
                    body = Files.readAllBytes(Path.of("./webapp/index.html"));
                }
                System.out.println("request url : ./webapp" + url);

                if (url.equals("/user/signup") && method.equals("POST")) {
                    //입력받는 정보들로 user객체 생성 후에 repository에 adduser()해주기.
                    //sout해본 결과 : GET으로 받아오는 듯?
                    String queryString = IOUtils.readData(br, lengthOfBodyContent);
                    System.out.println("GET method queryString : " + queryString);
                    //아이디, 비번, 이름, 이메일
                    //쿼리파싱 메서드가 맵을 반환하기 때문에 map에 맞춰 반환해주기.
                    //근데 hashmap은 순서를 보장하지 않기 떄문에 sout해도 순서는 마구마구 나옴.
                    Map<String, String> userData = HttpRequestUtils.parseQueryParameter(queryString);
                    userData.forEach((key, value) -> System.out.println(key + ":" + value));

                    String id = userData.get("userId");
                    String pw = userData.get("password");
                    String name = userData.get("name");
                    String email = userData.get("email");
                    User user = new User(id, pw, name, email);
                    repository.addUser(user);
                    System.out.println("successfully signed up : " + name);

                    response302Header(dos, "/index.html");
                    return;
                    //200header는 안보내기.
                }
//                if (url.equals("/user/signup") && method.equals("GET")) {
//                    String[] temp = url.split("\\?");
//                    String queryString = temp[1];
//
//                    Map<String, String> userData = HttpRequestUtils.parseQueryParameter(queryString);
//                    String id = userData.get("userId");
//                    String pw = userData.get("password");
//                    String name = userData.get("name");
//                    String email = userData.get("email");
//
//                    User user = new User(id, pw, name, email);
//                    repository.addUser(user);
//
//                    response302Header(dos, "/index.html");
//
//                }

                if (url.equals("/user/login")) {
                    //cookie를 true로 만드는 메서드 추가
                    //cookie가 true면 index.html을 반환하고, false면 login_failed.html
                    String queryString = IOUtils.readData(br, lengthOfBodyContent);
                    System.out.println("queryString : " + queryString);
                    Map<String, String> userLoginData = HttpRequestUtils.parseQueryParameter(queryString);
                    String id = userLoginData.get("userId");
                    String pw = userLoginData.get("password");
                    User user = repository.findUserById(id);
                    //로그인 확인 절차
                    if (user != null && user.getPassword().equals(pw)) {
                        response302HeaderWithCookie(dos, "/index.html", true);
                    }
                    response302HeaderWithCookie(dos, "/user/login_failed.html", false);

                }
                System.out.println("Cookie : " + cookie);

                //리스트는 쿠키 검증해서 앞이랑 같이 그냥 html주소만 넣어서 주기 -> cookie가 이미 앞에서 검증됐기 때문!
                if(url.equals("/user/userList")) {
                    if (!cookie.equals("loggedin=true")) {
                        response302Header(dos, "/user/login.html");
                        return;
                    }
                    body = Files.readAllBytes(Path.of("./webapp/user/list.html"));
                }

                if(method.equals("GET") && url.endsWith(".css")) {
                    body = Files.readAllBytes(Path.of("./webapp" + url));
                    response200HeaderWithCss(dos, lengthOfBodyContent);
                    responseBody(dos, body);
                    return;
                }

                response200Header(dos, body.length);
                responseBody(dos, body);

            } catch (IOException e) {
                log.log(Level.SEVERE,e.getMessage());
            }
        }

        //http resonse의 구조는 status line, headers, blank line, body로 구성.
        private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
            try {
                dos.writeBytes("HTTP/1.1 200 OK \r\n");
                dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
                dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
                dos.writeBytes("\r\n");
                dos.flush();
            } catch (IOException e) {
                log.log(Level.SEVERE, e.getMessage());
            }
        }

        private void response200HeaderWithCss(DataOutputStream dos, int lengthOfBodyContent) {
            try {
                dos.writeBytes("HTTP/1.1 200 OK \r\n");
                dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
                dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
                dos.writeBytes("\r\n");
                dos.flush();
            } catch (IOException e) {
                log.log(Level.SEVERE, e.getMessage());
            }
        }

        //302헤더는 어짜피 url 주소만 새로 반환하기 떄문에 body내용은 없는듯.
        private void response302Header(DataOutputStream dos, String path) {
            try {
                dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
                dos.writeBytes("Location: "+path);
                dos.writeBytes("\r\n");
                dos.flush();
            } catch (IOException e) {
                log.log(Level.SEVERE, e.getMessage());
            }
        }

        private void response302HeaderWithCookie(DataOutputStream dos, String path, boolean isUserExist) {
            //cookie로 로그인을 해줘야 redirect해도 로그인 상태가 유지됨.
            try {
                dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
                if (isUserExist) {
                    dos.writeBytes("Location: " + path + "\r\n");
                    dos.writeBytes("Set-Cookie: loggedin=true" + "\r\n");
                }
                if (!isUserExist) {
                    dos.writeBytes("Location: /user/login_failed.html" + "\r\n");
                }
                dos.writeBytes(("\r\n"));
                dos.flush();
            } catch (IOException e) {
                log.log(Level.SEVERE, e.getMessage());
            }
        }

        //http response 중 body -> html내용이 담아지면 넘겨주는 메서드.
        private void responseBody(DataOutputStream dos, byte[] body) {
            try {
                dos.write(body, 0, body.length);
                dos.flush();
            } catch (IOException e) {
                log.log(Level.SEVERE, e.getMessage());
            }
        }

    }