package org.luun.hsf.asm01.kitchencontrolbe.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.luun.hsf.asm01.kitchencontrolbe.entity.enums.LogStatus;
import org.luun.hsf.asm01.kitchencontrolbe.entity.enums.LogType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "log_batches")
@Data
public class LogBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id")
    private Long batchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private ProductionPlan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Float quantity;

    @Column(name = "production_date")
    private LocalDate productionDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    private LogStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private LogType type;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

//    public enum LogStatus {
//        PROCESSING, DONE, EXPIRED, DAMAGED
//    }

//    public enum LogType {
//        PRODUCTION, PURCHASE
//    }
}
