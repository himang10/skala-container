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

import com.sk.skala.myapp.domain.Order;
import com.sk.skala.myapp.domain.OrderItem;
import com.sk.skala.myapp.domain.OrderStatus;
import com.sk.skala.myapp.domain.Product;
import com.sk.skala.myapp.dto.OrderItemRequest;
import com.sk.skala.myapp.dto.OrderItemResponse;
import com.sk.skala.myapp.dto.OrderRequest;
import com.sk.skala.myapp.dto.OrderResponse;
import com.sk.skala.myapp.dto.ProductResponse;
import com.sk.skala.myapp.dto.UserResponse;
import com.sk.skala.myapp.service.OrderService;
import com.sk.skala.myapp.service.ProductService;
import com.sk.skala.myapp.service.UserService;

import lombok.RequiredArgsConstructor;

/**
 * 주문 관리 화면 MVC 컨트롤러
 */
@Controller
@RequestMapping("/web/orders")
@RequiredArgsConstructor
public class WebOrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final ProductService productService;

    private OrderResponse toOrderResponse(Order order) {
        List<OrderItemResponse> items = order.getOrderItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal()))
                .toList();
        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getName(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getOrderedAt(),
                items);
    }

    private ProductResponse toProductResponse(Product p) {
        return new ProductResponse(
                p.getId(), p.getName(), p.getPrice(), p.getStockQuantity(),
                p.getStatus(), p.getDescription(), null,
                p.getUser() != null ? p.getUser().getId() : null,
                p.getUser() != null ? p.getUser().getName() : null,
                p.getCategory() != null ? p.getCategory().getId() : null,
                p.getCategory() != null ? p.getCategory().getName() : null);
    }

    /** 주문 목록 (선택적 userId 필터) */
    @GetMapping
    public String list(@RequestParam(required = false) Long userId, Model model) {
        List<OrderResponse> orders;
        if (userId != null) {
            orders = orderService.getOrdersByUserId(userId).stream()
                    .map(this::toOrderResponse)
                    .toList();
        } else {
            orders = orderService.getAllOrders().stream()
                    .map(this::toOrderResponse)
                    .toList();
        }

        List<UserResponse> users = userService.getAllUsers().stream()
                .map(u -> new UserResponse(u.getId(), u.getName(), u.getEmail()))
                .toList();

        model.addAttribute("orders", orders);
        model.addAttribute("users", users);
        model.addAttribute("selectedUserId", userId);
        model.addAttribute("currentPage", "orders");
        return "orders/list";
    }

    /** 주문 상세 */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id);
        model.addAttribute("order", toOrderResponse(order));
        model.addAttribute("currentPage", "orders");
        return "orders/detail";
    }

    /** 주문 생성 폼 */
    @GetMapping("/new")
    public String newForm(Model model) {
        List<UserResponse> users = userService.getAllUsers().stream()
                .map(u -> new UserResponse(u.getId(), u.getName(), u.getEmail()))
                .toList();
        List<ProductResponse> products = productService.getAllProducts().stream()
                .filter(p -> p.getStockQuantity() != null && p.getStockQuantity() > 0)
                .map(this::toProductResponse)
                .toList();

        model.addAttribute("users", users);
        model.addAttribute("products", products);
        model.addAttribute("currentPage", "orders");
        return "orders/form";
    }

    /** 주문 생성 처리 */
    @PostMapping
    public String create(@RequestParam Long userId,
                         @RequestParam("productId") List<Long> productIds,
                         @RequestParam("quantity") List<Integer> quantities,
                         RedirectAttributes redirectAttributes) {
        List<OrderItemRequest> items = new java.util.ArrayList<>();
        for (int i = 0; i < productIds.size(); i++) {
            if (productIds.get(i) != null) {
                items.add(new OrderItemRequest(productIds.get(i), quantities.get(i)));
            }
        }
        OrderRequest request = new OrderRequest(userId, items);
        Order saved = orderService.createOrder(request);
        redirectAttributes.addFlashAttribute("successMsg",
                "주문 #" + saved.getId() + "이(가) 접수되었습니다.");
        return "redirect:/web/orders/" + saved.getId();
    }

    /** 주문 상태 변경 처리 */
    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam OrderStatus status,
                               RedirectAttributes redirectAttributes) {
        try {
            if (status == OrderStatus.CANCELLED) {
                orderService.cancelOrder(id);
                redirectAttributes.addFlashAttribute("successMsg", "주문이 취소되었습니다.");
            } else {
                orderService.updateOrderStatus(id, status);
                redirectAttributes.addFlashAttribute("successMsg",
                        "주문 상태가 " + status + "으로 변경되었습니다.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "상태 변경 실패: " + e.getMessage());
        }
        return "redirect:/web/orders/" + id;
    }
}
