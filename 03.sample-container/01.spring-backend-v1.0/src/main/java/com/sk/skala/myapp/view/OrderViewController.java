package com.sk.skala.myapp.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sk.skala.myapp.domain.Product;
import com.sk.skala.myapp.service.OrderService;
import com.sk.skala.myapp.service.ProductService;
import com.sk.skala.myapp.service.UserService;

/**
 * 주문 관리 화면 컨트롤러 (Thymeleaf)
 * 화면 흐름:
 *   1. 주문 관리 화면에서 상품 목록을 보고 [주문] 버튼 클릭
 *   2. 사용자 선택 화면에서 사용자·수량·배송지 입력 후 [주문 확정] 클릭 → 주문 완료
 *   3. 주문 목록에서 상태 버튼을 클릭할 때마다 다음 상태로 전환
 */
@Controller
@RequestMapping("/view/orders")
public class OrderViewController {

    private final OrderService orderService;
    private final ProductService productService;
    private final UserService userService;

    public OrderViewController(OrderService orderService,
                               ProductService productService,
                               UserService userService) {
        this.orderService = orderService;
        this.productService = productService;
        this.userService = userService;
    }

    // GET /view/orders — 상품 목록(주문 버튼) + 주문 목록(상태 버튼)
    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("orders", orderService.getAllOrders());
        return "order/list";        // templates/order/list.html
    }

    // GET /view/orders/new?productId=1 — 사용자 선택 화면
    @GetMapping("/new")
    public String selectUser(@RequestParam Long productId, Model model) {
        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. id=" + productId));
        model.addAttribute("product", product);
        model.addAttribute("users", userService.getAllUsers());
        return "order/select-user"; // templates/order/select-user.html
    }

    // POST /view/orders — 주문 확정 (사용자 선택 OK → 주문 완료)
    @PostMapping
    public String create(@RequestParam Long productId,
                         @RequestParam Long userId,
                         @RequestParam(defaultValue = "1") Integer quantity,
                         @RequestParam(required = false) String address) {
        orderService.createOrder(productId, userId, quantity, address);
        return "redirect:/view/orders";
    }

    // POST /view/orders/{id}/next — 상태 버튼 클릭 → 다음 상태로 전환
    @PostMapping("/{id}/next")
    public String advanceStatus(@PathVariable Long id) {
        orderService.advanceStatus(id);
        return "redirect:/view/orders";
    }
}
