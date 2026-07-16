package com.sk.skala.myapp.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sk.skala.myapp.domain.User;
import com.sk.skala.myapp.dto.UserResponse;
import com.sk.skala.myapp.service.UserService;

import lombok.RequiredArgsConstructor;

/**
 * 사용자 관리 화면 MVC 컨트롤러
 */
@Controller
@RequestMapping("/web/users")
@RequiredArgsConstructor
public class WebUserController {

    private final UserService userService;

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }

    /** 사용자 목록 */
    @GetMapping
    public String list(Model model) {
        List<UserResponse> users = userService.getAllUsers().stream()
                .map(this::toResponse)
                .toList();
        model.addAttribute("users", users);
        model.addAttribute("currentPage", "users");
        return "users/list";
    }

    /** 신규 등록 폼 */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("isNew", true);
        model.addAttribute("currentPage", "users");
        return "users/form";
    }

    /** 등록 처리 */
    @PostMapping
    public String create(@RequestParam String name,
                         @RequestParam String email,
                         RedirectAttributes redirectAttributes) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        userService.createUser(user);
        redirectAttributes.addFlashAttribute("successMsg", "사용자 '" + name + "'이(가) 등록되었습니다.");
        return "redirect:/web/users";
    }

    /** 수정 폼 */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable long id, Model model) {
        User user = userService.getUserById(id).orElse(null);
        if (user == null) {
            return "redirect:/web/users";
        }
        model.addAttribute("user", user);
        model.addAttribute("userId", id);
        model.addAttribute("isNew", false);
        model.addAttribute("currentPage", "users");
        return "users/form";
    }

    /** 수정 처리 (HTML form은 PUT 불가 → POST + _method 무시, 직접 처리) */
    @PostMapping("/{id}")
    public String update(@PathVariable long id,
                         @RequestParam String name,
                         @RequestParam String email,
                         RedirectAttributes redirectAttributes) {
        User updated = new User();
        updated.setName(name);
        updated.setEmail(email);
        userService.updateUser(id, updated);
        redirectAttributes.addFlashAttribute("successMsg", "사용자 정보가 수정되었습니다.");
        return "redirect:/web/users";
    }

    /** 삭제 처리 */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable long id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("successMsg", "사용자가 삭제되었습니다.");
        return "redirect:/web/users";
    }
}
