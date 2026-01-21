package org.luun.hsf.asm01.kitchencontrolbe.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "production_plans")
@Data
public class ProductionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long planId;

    @Column(name = "kitchen_id") // Assuming this is a simple identifier for now
    private Integer kitchenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "plan_date")
    private LocalDate planDate;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    private String status;
    private String note;

    @OneToMany(mappedBy = "plan")
    private List<Order> orders;

    @OneToMany(mappedBy = "plan")
    private List<LogBatch> logBatches;
}
