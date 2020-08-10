package education.p0003.common.entity;

import lombok.Data;
import org.seasar.doma.*;

import java.util.UUID;

@Entity
@Table(name = "item_tbl")
@Data
public class Item {
    @Id
    @Column(name = "id")
    String id;

    @Column(name = "name")
    String name;

    @Column(name = "size")
    double size;
}
