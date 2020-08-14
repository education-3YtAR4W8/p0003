package education.p0003.common.entity;

import lombok.Data;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "item_tbl")
@Data
public class Item implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    Integer id;

    @Column(name = "name")
    public String name;

    @Column(name = "size")
    BigDecimal size;
}