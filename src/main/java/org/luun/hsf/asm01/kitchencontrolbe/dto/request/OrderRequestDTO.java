package org.luun.hsf.asm01.kitchencontrolbe.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {
    private int storeId;
    private List<OrderDetailRequest> orderDetails;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderDetailRequest {
        private int productId;
        private float quantity;
    }
}
