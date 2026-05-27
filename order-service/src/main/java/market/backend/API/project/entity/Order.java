package market.backend.API.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Order {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer productId;
    @NotNull(message="quantity cannot be null")
    private Integer quantity;
    @Min(value = 0, message = "price cannot be negative")
    private double totalPrice;
    private String status;
    private LocalDateTime createdAt;
}
