# Step-by-Step DTO Implementation Guide

This document explains what Data Transfer Objects (DTOs) are, why they are essential for a good API design, and how to create them for your project entities.

---

### Part 1: What is a DTO and Why Do We Need It?

A **Data Transfer Object (DTO)** is a simple Java object (POJO) used to transfer data between different layers of an application. In a Spring Boot REST API, their main job is to carry data between the client (frontend) and your server (specifically, the Controller layer).

You should **NEVER** expose your database entities directly to the client. Instead, you should always use DTOs. Here are the main reasons why:

1.  **Security:** Your database entity might contain sensitive information. For example, the `User` entity has a `password` field. If you send the `User` entity directly to the client, you will expose the hashed password. A `UserDTO` would be created without the `password` field.

2.  **API Contract Stability:** You might want to rename a column in your database or refactor your `Entity`. If you expose the entity directly, this change would break the API and force the frontend team to make changes. By using DTOs, you can change your internal database structure freely while keeping the public API (the structure of the DTOs) consistent.

3.  **Data Shaping and Aggregation:** The client often needs data in a different format than how it's stored in the database.
    -   A DTO can combine data from multiple entities. For example, `OrderResponseDTO` could contain the `storeName` from the `Store` entity, not just the `store_id`.
    -   A DTO can hide unnecessary information. An `Order` entity might have many complex relationships, but the DTO only includes the fields the client actually needs to display.

4.  **Separation of Concerns:** DTOs help to create a clean separation between the persistence layer (your `@Entity` classes) and the API layer (your `@RestController` classes).

### Part 2: Common DTO Naming Conventions

To keep the project organized, we use specific suffixes for different types of DTOs:

-   **`...RequestDTO`**: Used for data coming **from** the client to the server, typically for creating or updating a resource (e.g., in `@PostMapping` or `@PutMapping` body).
-   **`...ResponseDTO`**: Used for data going **from** the server to the client, typically as a response to a request (e.g., in `@GetMapping` or as the return value of a `POST`).
-   **`...SummaryDTO` / `...ItemDTO`**: A simplified `ResponseDTO` used for list views, containing only the essential information to avoid sending large amounts of data.
-   **`...DetailDTO`**: A more detailed `ResponseDTO` for a single-item view.

### Part 3: Step-by-Step DTO Creation

Let's create DTOs for some of the key entities in your project. We'll place them in a new package: `org.luun.hsf.asm01.kitchencontrolbe.dto`.

#### Step 1: DTOs for `User` and `Role` (Demonstrates Security)

The `User` entity contains a `password`. We must not expose this.

**`RoleDTO.java`**
A simple DTO for the role.

```java
// Suggested path: src/main/java/org/luun/hsf/asm01/kitchencontrolbe/dto/response/RoleDTO.java
package org.luun.hsf.asm01.kitchencontrolbe.dto.response;

import lombok.Data;

@Data
public class RoleDTO {
    private Integer roleId;
    private String roleName;
}
```

**`UserResponseDTO.java`**
This DTO will be sent to the client. Notice the absence of the `password` field. It also "flattens" the `Role` object into a `RoleDTO`.

```java
// Suggested path: src/main/java/org/luun/hsf/asm01/kitchencontrolbe/dto/response/UserResponseDTO.java
package org.luun.hsf.asm01.kitchencontrolbe.dto.response;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Long userId;
    private String username;
    private String fullName;
    private RoleDTO role; // Using RoleDTO, not the Role entity
    private Integer storeId; // Sending just the ID is often enough
}
```

#### Step 2: DTOs for `Order` (Demonstrates Request vs. Response)

This is the pattern you already have in your project, expanded slightly.

**`OrderRequestDTO.java`**
Used when a store staff creates a new order. It uses simple IDs.

```java
// Path: src/main/java/org/luun/hsf/asm01/kitchencontrolbe/dto/request/OrderRequestDTO.java
package org.luun.hsf.asm01.kitchencontrolbe.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequestDTO {
    // The storeId should come from the logged-in user, not the request body
    private List<OrderDetailRequest> orderDetails;

    @Data
    public static class OrderDetailRequest {
        private long productId;
        private float quantity;
    }
}
```

**`OrderResponseDTO.java`**
Used to send order details back to the client. It contains more descriptive information, like the product name, which is more useful for display.

```java
// Path: src/main/java/org/luun/hsf/asm01/kitchencontrolbe/dto/response/OrderResponseDTO.java
package org.luun.hsf.asm01.kitchencontrolbe.dto.response;

import lombok.Data;
import org.luun.hsf.asm01.kitchencontrolbe.entity.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDTO {
    private Long orderId;
    private Long storeId;
    private String storeName; // Example of aggregated data
    private LocalDateTime orderDate;
    private OrderStatus status;
    private List<OrderDetailResponse> orderDetails;

    @Data
    public static class OrderDetailResponse {
        private Long productId;
        private String productName; // More useful than just the ID
        private float quantity;
    }
}
```

### Part 4: Using Mappers (MapStruct) to Automate Conversion

Manually copying fields from an Entity to a DTO is tedious and error-prone. The **MapStruct** library (which is already in your `pom.xml` via `mapstruct` and `lombok-mapstruct-binding`) automates this.

You simply define a `Mapper` interface, and MapStruct will generate the implementation class during compilation.

**How to create a Mapper:**

1.  Create a new package, e.g., `org.luun.hsf.asm01.kitchencontrolbe.mapper`.
2.  Create a new interface for each entity-DTO pair.

**Example: `UserMapper.java`**

```java
// Suggested path: src/main/java/org/luun/hsf/asm01/kitchencontrolbe/mapper/UserMapper.java
package org.luun.hsf.asm01.kitchencontrolbe.mapper;

import org.luun.hsf.asm01.kitchencontrolbe.dto.response.UserResponseDTO;
import org.luun.hsf.asm01.kitchencontrolbe.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring") // Tells MapStruct to create a Spring Bean
public interface UserMapper {

    // Map source (User) to target (UserResponseDTO)
    @Mapping(source = "store.storeId", target = "storeId") // Manually map nested field
    UserResponseDTO toUserResponseDTO(User user);
}
```
-   `@Mapper(componentModel = "spring")`: This makes the generated mapper a Spring component, so you can `@Autowired` it into your services.
-   `@Mapping(source = "store.storeId", target = "storeId")`: MapStruct is smart, but sometimes you need to help it. This line says: "Take the value from `user.getStore().getStoreId()` and put it into `dto.setStoreId()`."

Now, in your `UserService`, you can just do:

```java
// In your service implementation...
@Autowired
private UserMapper userMapper;
@Autowired
private UserRepository userRepository;

public UserResponseDTO getUserById(Long id) {
    User user = userRepository.findById(id).orElseThrow();
    return userMapper.toUserResponseDTO(user); // Clean and simple conversion
}
```

By following this guide, you can create a robust and secure API layer for your application.
