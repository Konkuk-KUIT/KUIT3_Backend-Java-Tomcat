package controller.GetController;

import controller.Controller;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import structure.StartLine;

public class HomeController implements Controller {

    @Override
    public boolean doesFit(StartLine startLine) {   // startLine의 HTTP version은 몰라유.. 어케 처리 함?
        return startLine.isGet() && startLine.isMatchingPath("/", "/index.html"); // TODO: 얘 하드 코딩 바꾸자, 한 곳에서 관리 하고 싶어..? 그냥 뭔가 맘에 안드어 ㅗ
    }

    @Override
    public byte[] runLogic(BufferedReader br, DataOutputStream dos) {
        try (FileInputStream input = new FileInputStream("/Users/tony/IdeaProjects/KUIT3_Backend-Java-Tomcat/webapp/index.html")) {    // TODO: 얘 고쳐라, Path.of() 몰라
            return input.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
