package controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import structure.StartLine;

public interface Controller {
    byte[] runLogic(BufferedReader br, DataOutputStream dos, StartLine startLine) throws IOException;   // TODO : 이 친구의 리턴값은 확정이 아닙니다. 변경 가능성 농후, 아마 나중에 body를 http message로 transform(?) packing(?) 해주는 무언가가 나올겁니다.
    // todo: br은 추후 바디에 입력이 들어올떄 사용되고 dos는 결과값을 직접 stream으로 외부로 보낼때 사용될껀대 확실치 x, parameter을 넘기기 위해 startLine도 추가되었습니다.

    boolean doesFit(StartLine startLine);
}
