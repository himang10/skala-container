package com.sk.skala.myapp.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 첫 화면(메뉴) 컨트롤러
 * - @Controller: 문자열을 반환하면 templates/ 아래의 Thymeleaf 템플릿 이름으로 해석된다.
 */
@Controller
public class HomeViewController {

    // GET / — 메인 메뉴 화면
    @GetMapping("/")
    public String home() {
        return "index";     // templates/index.html
    }
}
