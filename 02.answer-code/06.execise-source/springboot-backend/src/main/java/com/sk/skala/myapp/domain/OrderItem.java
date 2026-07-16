package com.sk.skala.myapp.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 주문 항목 엔티티
 *
 * - unitPrice: 주문 시점의 상품 단가를 별도 저장 (이후 상품 가격 변경 시에도 주문 내역 불변)
 * - subtotal: unitPrice × quantity, 저장 시 계산하여 저장
 */
@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @ToString.Exclude                                   // 순환참조 방지
    private Order order;                                // 속한 주문

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;                            // 주문된 상품

    @Column(nullable = false)
    private Integer quantity;                           // 주문 수량

    @Column(nullable = false)
    private Integer unitPrice;                          // 주문 시점 단가 (상품 가격 변경 불영향)

    @Column(nullable = false)
    private Integer subtotal;                           // 소계 = unitPrice × quantity
}
