package com.sk.skala.myapp.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sk.skala.myapp.domain.Product;
import com.sk.skala.myapp.domain.ProductStatus;
import com.sk.skala.myapp.service.ProductService;

/**
 * 상품 관리 화면 컨트롤러 (Thymeleaf)
 * - 상품 검색 / 등록 / 삭제
 */
@Controller
@RequestMapping("/view/products")
public class ProductViewController {

    private final ProductService productService;

    public ProductViewController(ProductService productService) {
        this.productService = productService;
    }

    // GET /view/products — 상품 목록 + 검색
    @GetMapping
    public String list(@RequestParam(required = false) String keyword, Model model) {
        if (keyword != null && !keyword.isBlank()) {
            model.addAttribute("products", productService.searchProductsByName(keyword));
        } else {
            model.addAttribute("products", productService.getAllProducts());
        }
        model.addAttribute("keyword", keyword);
        return "product/list";      // templates/product/list.html
    }

    // POST /view/products — 상품 등록 (폼 전송)
    @PostMapping
    public String create(@RequestParam String name,
                         @RequestParam Integer price,
                         @RequestParam Integer stockQuantity,
                         @RequestParam(required = false) String description) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setStockQuantity(stockQuantity);
        product.setStatus(ProductStatus.ON_SALE);   // 신규 상품은 판매중 상태로 등록
        product.setDescription(description);
        productService.createProduct(product, null);
        return "redirect:/view/products";
    }

    // POST /view/products/{id}/delete — 상품 삭제 (HTML 폼은 DELETE를 지원하지 않아 POST 사용)
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/view/products";
    }
}
