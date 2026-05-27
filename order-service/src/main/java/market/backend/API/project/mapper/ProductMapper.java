package market.backend.API.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import market.backend.API.project.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
