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

import com.sk.skala.myapp.domain.Category;
import com.sk.skala.myapp.dto.CategoryRequest;
import com.sk.skala.myapp.dto.CategoryResponse;
import com.sk.skala.myapp.service.CategoryService;

import lombok.RequiredArgsConstructor;

/**
 * 카테고리 관리 화면 MVC 컨트롤러
 */
@Controller
@RequestMapping("/web/categories")
@RequiredArgsConstructor
public class WebCategoryController {

    private final CategoryService categoryService;

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getProducts().size());
    }

    /** 카테고리 목록 */
    @GetMapping
    public String list(Model model) {
        List<CategoryResponse> categories = categoryService.getAllCategories().stream()
                .map(this::toResponse)
                .toList();
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", "categories");
        return "categories/list";
    }

    /** 신규 등록 폼 */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("isNew", true);
        model.addAttribute("currentPage", "categories");
        return "categories/form";
    }

    /** 등록 처리 */
    @PostMapping
    public String create(@RequestParam String name,
                         @RequestParam(required = false) String description,
                         RedirectAttributes redirectAttributes) {
        CategoryRequest request = new CategoryRequest(name, description);
        Category saved = categoryService.createCategory(request);
        redirectAttributes.addFlashAttribute("successMsg",
                "카테고리 '" + saved.getName() + "'이(가) 등록되었습니다.");
        return "redirect:/web/categories";
    }

    /** 수정 폼 */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Category category = categoryService.getCategoryById(id);
        model.addAttribute("category", category);
        model.addAttribute("categoryId", id);
        model.addAttribute("isNew", false);
        model.addAttribute("currentPage", "categories");
        return "categories/form";
    }

    /** 수정 처리 */
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String name,
                         @RequestParam(required = false) String description,
                         RedirectAttributes redirectAttributes) {
        CategoryRequest request = new CategoryRequest(name, description);
        categoryService.updateCategory(id, request);
        redirectAttributes.addFlashAttribute("successMsg", "카테고리가 수정되었습니다.");
        return "redirect:/web/categories";
    }

    /** 삭제 처리 */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMsg", "카테고리가 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    "삭제 실패: " + e.getMessage());
        }
        return "redirect:/web/categories";
    }
}
