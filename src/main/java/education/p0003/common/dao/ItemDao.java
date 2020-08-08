package education.p0003.common.dao;

import org.seasar.doma.Select;
import org.seasar.doma.Dao;
import org.seasar.doma.boot.ConfigAutowireable;

import education.p0003.common.entity.Item;

import java.math.BigDecimal;
import java.util.List;

@Dao
@ConfigAutowireable
public interface ItemDao {
    @Select
    List<Item> selectAll();

    @Select
    Item selectById(int id);

    @Select
    List<Integer> selectIdsBySize(BigDecimal size);
}
