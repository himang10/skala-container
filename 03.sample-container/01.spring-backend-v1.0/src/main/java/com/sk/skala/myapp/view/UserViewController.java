package com.sk.skala.myapp.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sk.skala.myapp.domain.User;
import com.sk.skala.myapp.service.UserService;

/**
 * 사용자 관리 화면 컨트롤러 (Thymeleaf)
 * - 사용자 검색 / 등록 / 삭제
 */
@Controller
@RequestMapping("/view/users")
public class UserViewController {

    private final UserService userService;

    public UserViewController(UserService userService) {
        this.userService = userService;
    }

    // GET /view/users — 사용자 목록 + 검색
    @GetMapping
    public String list(@RequestParam(required = false) String keyword, Model model) {
        if (keyword != null && !keyword.isBlank()) {
            model.addAttribute("users", userService.searchUsersByName(keyword));
        } else {
            model.addAttribute("users", userService.getAllUsers());
        }
        model.addAttribute("keyword", keyword);
        return "user/list";     // templates/user/list.html
    }

    // POST /view/users — 사용자 등록 (폼 전송)
    @PostMapping
    public String create(@RequestParam String name,
                         @RequestParam String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        userService.createUser(user);
        return "redirect:/view/users";
    }

    // POST /view/users/{id}/delete — 사용자 삭제
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable long id) {
        userService.deleteUser(id);
        return "redirect:/view/users";
    }
}
