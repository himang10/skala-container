package com.sk.skala.myapp.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 상품 카테고리 엔티티
 * - Category → Product: 양방향 @OneToMany / @ManyToOne 관계
 * - CascadeType 미설정: 상품은 카테고리와 독립적 생명주기를 가짐
 */
@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String name;                                  // 카테고리명 (유일값)

    private String description;                           // 카테고리 설명

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @ToString.Exclude                                     // 순환참조 방지
    private List<Product> products = new ArrayList<>();
}
