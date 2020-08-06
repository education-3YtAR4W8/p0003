package education.p0003.common.entity;

import lombok.Data;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;

@Entity
@Table(name = "item_tbl")
@Data
public class Item {
    @Id
    @Column(name = "id")
    Integer id;

    @Column(name = "name")
    String name;

    @Column(name = "size")
    Double size;
}
