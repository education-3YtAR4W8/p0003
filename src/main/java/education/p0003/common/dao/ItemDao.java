package education.p0003.common.dao;

import education.p0003.common.entity.Item;
import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import java.util.List;

@Dao
@ConfigAutowireable
public interface ItemDao {
    @Select
    List<Item> selectAll();
}
