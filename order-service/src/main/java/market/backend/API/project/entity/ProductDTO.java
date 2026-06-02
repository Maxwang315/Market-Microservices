package market.backend.API.project.entity;

import lombok.Data;

@Data
public class ProductDTO {
    private Integer productId;
    private String productName;
    private Double productPrice;
    private Integer productInventory;
}
