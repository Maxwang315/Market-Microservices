package market.backend.API.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import market.backend.API.project.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
