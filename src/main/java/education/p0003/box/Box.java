package education.p0003.box;

import education.p0003.common.entity.Item;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Box implements Serializable {
    private static final long serialVersionUID = 1L;

    public List<ItemAndQuantity> itemAndQuantities = new ArrayList<>();

    public Integer getQuantityEnableToStore(Item item) {
        BigDecimal space = new BigDecimal("1.00");
        for (ItemAndQuantity itemAndQuantity : itemAndQuantities) {
            space = space.subtract(itemAndQuantity.item.getSize().multiply(BigDecimal.valueOf(itemAndQuantity.quantity)));
        }
        return space.divide(item.getSize()).intValue();
    }

    public Integer storeItems(ItemAndQuantity itemAndQuantity) {
        Integer quantityEnableToStore = getQuantityEnableToStore(itemAndQuantity.item);
        Integer quantityToStore = Math.min(quantityEnableToStore, itemAndQuantity.quantity);
        itemAndQuantities.add(new ItemAndQuantity(itemAndQuantity.item, quantityToStore));
        return quantityToStore;
    }
}
