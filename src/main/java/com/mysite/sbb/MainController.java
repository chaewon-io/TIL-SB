package com.mysite.sbb;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {
    @GetMapping("/sbb")

    //@ResponseBody : URL 요청에 대한 응답으로 문자열을 리턴
    //만약 @ResponseBody 어노테이션을 삭제하면 템플릿 파일을 탐색한다
    @ResponseBody
    public String index() {
        return "안녕하세요. sbb에 오신것을 환영합니다.";
    }
}
