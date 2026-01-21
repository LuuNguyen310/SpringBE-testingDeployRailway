package org.luun.hsf.asm01.kitchencontrolbe.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.luun.hsf.asm01.kitchencontrolbe.entity.enums.ProductType;

@Entity
@Table(name = "products")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false)
    private ProductType productType;

    @Column(nullable = false)
    private String unit;

    @Column(name = "shelf_life_days")
    private Integer shelfLifeDays;

    //Ko nên dùng enum bên trong class vì khó mở rộng sau này
//    public enum ProductType {
//        RAW_MATERIAL,
//        SEMI_FINISHED,
//        FINISHED_PRODUCT
//    }
}
