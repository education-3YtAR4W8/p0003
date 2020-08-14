package education.p0003.box;

import education.p0003.common.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
public class ItemAndQuantity implements Serializable {
    private static final long serialVersionUID = 1L;

    public Item item;
    public Integer quantity;
}
