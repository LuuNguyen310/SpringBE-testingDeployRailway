# Step-by-Step Entity Implementation Guide

This document outlines the recommended order for creating the Java entity classes for your project. Following this order will simplify development by ensuring that an entity's dependencies (the other entities it has relationships with) are already created before you need them.

We will proceed in logical phases, starting with foundational data and building up to more complex features.

---

### Phase 1: Core Master Data (The Foundation)

**Goal:** Create the basic, independent entities that other parts of the system will rely on.

#### 1. `Role.java`
-   **Table:** `roles`
-   **Dependencies:** None.
-   **Reason:** Roles are fundamental for user management and have no other dependencies.

```java
// Suggested path: src/main/java/org/luun/hsf/asm01/kitchencontrolbe/entity/Role.java
package org.luun.hsf.asm01.kitchencontrolbe.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "roles")
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;
}
```

#### 2. `Store.java`
-   **Table:** `stores`
-   **Dependencies:** None.
-   **Reason:** Stores are independent entities representing physical locations.

```java
// Suggested path: src/main/java/org/luun/hsf/asm01/kitchencontrolbe/entity/Store.java
package org.luun.hsf.asm01.kitchencontrolbe.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "stores")
@Data
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Integer storeId;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    private String address;
    private String phone;
}
```

#### 3. `Product.java`
-   **Table:** `products`
-   **Dependencies:** None.
-   **Reason:** Products are the core items in your inventory and order system.

```java
// Suggested path: src/main/java/org/luun/hsf/asm01/kitchencontrolbe/entity/Product.java
package org.luun.hsf.asm01.kitchencontrolbe.entity;

import jakarta.persistence.*;
import lombok.Data;

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

    public enum ProductType {
        RAW_MATERIAL,
        SEMI_FINISHED,
        FINISHED_PRODUCT
    }
}
```

---

### Phase 2: Users and Authentication

**Goal:** Create the entities needed for user accounts and permissions.

#### 1. `User.java`
-   **Table:** `users`
-   **Dependencies:** `Role.java`, `Store.java`
-   **Reason:** A user must be assigned a role and can be associated with a store.

```java
// Suggested path: src/main/java/org/luun/hsf/asm01/kitchencontrolbe/entity/User.java
package org.luun.hsf.asm01.kitchencontrolbe.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToOne
    @JoinColumn(name = "store_id", unique = true)
    private Store store;
}
```

---

### Phase 3: Recipe Management

**Goal:** Define the structure for products and their recipes. Your project already has `Order` and `OrderDetail`, so we focus on what's next.

#### 1. `Recipe.java`
-   **Table:** `recipes`
-   **Dependencies:** `Product.java`
-   **Reason:** A recipe is defined to create a specific `FINISHED_PRODUCT` or `SEMI_FINISHED` product.

```java
// Suggested path: src/main/java/org/luun/hsf/asm01/kitchencontrolbe/entity/Recipe.java
package org.luun.hsf.asm01.kitchencontrolbe.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "recipes")
@Data
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id")
    private Long recipeId;

    @Column(name = "recipe_name", nullable=false)
    private String recipeName;

    @Column(name = "yield_quantity")
    private Float yieldQuantity;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeDetail> recipeDetails;
}
```

#### 2. `RecipeDetail.java`
-   **Table:** `recipe_details`
-   **Dependencies:** `Recipe.java`, `Product.java`
-   **Reason:** This is a linking table entity that details the raw materials (`Product`) required for a `Recipe`.

```java
// Suggested path: src/main/java/org/luun/hsf/asm01/kitchencontrolbe/entity/RecipeDetail.java
package org.luun.hsf.asm01.kitchencontrolbe.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "recipe_details")
@Data
public class RecipeDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_detail_id")
    private Long recipeDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_material_id", nullable = false)
    private Product rawMaterial;

    @Column(nullable = false)
    private Float quantity;
}
```

---
### Phase 4: Production Flow

**Goal:** Define entities for planning and tracking production in the central kitchen.

#### 1. `ProductionPlan.java`
-   **Table:** `production_plans`
-   **Dependencies:** `User.java`
-   **Reason:** It links a production schedule to the coordinator (`User`) who created it. It acts as a parent for `Order`s and `LogBatch`es.

```java
// Suggested path: src/main/java/org/luun/hsf/asm01/kitchencontrolbe/entity/ProductionPlan.java
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
```

#### 2. `LogBatch.java`
-   **Table:** `log_batches`
-   **Dependencies:** `ProductionPlan.java`, `Product.java`
-   **Reason:** Tracks a specific batch of a product being made or purchased as part of a plan.

```java
// Suggested path: src/main/java/org/luun/hsf/asm01/kitchencontrolbe/entity/LogBatch.java
package org.luun.hsf.asm01.kitchencontrolbe.entity;

import jakarta.persistence.*;
import lombok.Data;
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
    
    public enum LogStatus {
        PROCESSING, DONE, EXPIRED, DAMAGED
    }

    public enum LogType {
        PRODUCTION, PURCHASE
    }
}
```
*Note: You will also need to update the existing `Order.java` entity to include its relationship to `ProductionPlan`.*

---
### Phase 5: Logistics

**Goal:** Manage the delivery of orders and handle feedback.

#### 1. `Delivery.java`
-   **Table:** `deliveries`
-   **Dependencies:** `User.java`
-   **Reason:** Groups multiple `Order`s into a single shipment assigned to a shipper (`User`).

```java
// Suggested path: src/main/java/org/luun/hsf/asm01/kitchencontrolbe/entity/Delivery.java
package org.luun.hsf.asm01.kitchencontrolbe.entity;

import jakarta.persistence.*;
import lombok.Data;
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

    public enum DeliveryStatus {
        WAITTING, PROCESSING, DONE
    }
}
```

#### 2. `QualityFeedback.java`
-   **Table:** `quality_feedbacks`
-   **Dependencies:** `Order.java`, `Store.java`
-   **Reason:** Captures store feedback on a specific `Order`.

```java
// Suggested path: src/main/java/org/luun/hsf/asm01/kitchencontrolbe/entity/QualityFeedback.java
package org.luun.hsf.asm01.kitchencontrolbe.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "quality_feedbacks")
@Data
public class QualityFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long feedbackId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    private Integer rating;
    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
```

---
### Phase 6: Inventory

**Goal:** Track stock levels in the central warehouse.

#### 1. `Inventory.java`
-   **Table:** `inventories`
-   **Dependencies:** `Product.java`, `LogBatch.java`
-   **Reason:** Represents the physical stock of a product from a specific batch.

```java
// Suggested path: src/main/java/org/luun/hsf/asm01/kitchencontrolbe/entity/Inventory.java
package org.luun.hsf.asm01.kitchencontrolbe.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "inventories")
@Data
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long inventoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false, unique = true)
    private LogBatch batch;

    private Float quantity;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;
}
```

#### 2. `InventoryTransaction.java`
-   **Table:** `inventory_transactions`
-   **Dependencies:** `Product.java`, `User.java`, `LogBatch.java`
-   **Reason:** Records all movements (import/export) of inventory, creating an auditable log.

```java
// Suggested path: src/main/java/org/luun/hsf/asm01/kitchencontrolbe/entity/InventoryTransaction.java
package org.luun.hsf.asm01.kitchencontrolbe.entity;

import jakarta.persistence.*;
import lombok.Data;
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
    
    public enum TransactionType {
        IMPORT, EXPORT
    }
}
```

---
### Phase 7: Reporting

**Goal:** Provide data structures for generating reports.

#### 1. `Report.java`
-   **Table:** `reports`
-   **Dependencies:** `User.java`
-   **Reason:** A generic entity to log generated reports and who created them.

```java
// Suggested path: src/main/java/org/luun/hsf/asm01/kitchencontrolbe/entity/Report.java
package org.luun.hsf.asm01.kitchencontrolbe.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Data
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;
    
    @Column(name = "report_type")
    private String reportType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "created_date")
    private LocalDateTime createdDate;
}
```