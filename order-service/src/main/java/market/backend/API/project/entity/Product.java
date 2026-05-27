package market.backend.API.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@TableName("Product")
public class Product {
    @NotBlank(message = "product cannot be blank")
    private String productName;
    @Min(value = 0, message = "price cannot be negative")
    private Integer productPrice;
    @Min(value = 0, message = "inventory cannot be negative")
    private Integer productInventory;
    @TableId(type = IdType.AUTO)
    private Integer productId;
}
