package org.luun.hsf.asm01.kitchencontrolbe.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.luun.hsf.asm01.kitchencontrolbe.entity.enums.DeliveryStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "deliveries")
@Data
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long deliveryId;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipper_id")
    private User shipper;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "delivery")
    private List<Order> orders;

//    public enum DeliveryStatus {
//        WAITTING, PROCESSING, DONE
//    }
}
