package com.sk.skala.myapp.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.sk.skala.myapp.domain.Order;
import com.sk.skala.myapp.domain.OrderStatus;
import com.sk.skala.myapp.domain.User;
import com.sk.skala.myapp.dto.OrderItemResponse;
import com.sk.skala.myapp.dto.OrderResponse;
import com.sk.skala.myapp.service.CategoryService;
import com.sk.skala.myapp.service.OrderService;
import com.sk.skala.myapp.service.UserService;

import lombok.RequiredArgsConstructor;

/**
 * 대시보드(메인 화면) MVC 컨트롤러
 */
@Controller
@RequiredArgsConstructor
public class WebController {

    private final UserService userService;
    private final CategoryService categoryService;
    private final OrderService orderService;

    @GetMapping("/")
    public String index(Model model) {
        List<User> users = userService.getAllUsers();
        long userCount = users.size();
        long categoryCount = categoryService.getAllCategories().size();
        List<Order> allOrders = orderService.getAllOrders();
        long orderCount = allOrders.size();
        long pendingCount = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.PENDING)
                .count();

        // 최근 주문 5건
        List<OrderResponse> recentOrders = allOrders.stream()
                .sorted((a, b) -> b.getOrderedAt().compareTo(a.getOrderedAt()))
                .limit(5)
                .map(this::toOrderResponse)
                .toList();

        model.addAttribute("userCount", userCount);
        model.addAttribute("categoryCount", categoryCount);
        model.addAttribute("orderCount", orderCount);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("recentOrders", recentOrders);
        model.addAttribute("currentPage", "home");
        return "index";
    }

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
}
