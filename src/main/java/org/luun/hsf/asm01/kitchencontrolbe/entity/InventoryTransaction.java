package org.luun.hsf.asm01.kitchencontrolbe.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.luun.hsf.asm01.kitchencontrolbe.entity.enums.TransactionType;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_transactions")
@Data
public class InventoryTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private LogBatch batch;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TransactionType type;

    private Float quantity;
    private String note;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

//    public enum TransactionType {
//        IMPORT, EXPORT
//    }
}
