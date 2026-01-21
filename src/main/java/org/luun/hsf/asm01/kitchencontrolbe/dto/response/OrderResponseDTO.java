package org.luun.hsf.asm01.kitchencontrolbe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.luun.hsf.asm01.kitchencontrolbe.entity.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    private int orderId;
    private int storeId;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private List<OrderDetailResponse> orderDetails;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderDetailResponse {
        private int productId;
        private float quantity;
    }
}
