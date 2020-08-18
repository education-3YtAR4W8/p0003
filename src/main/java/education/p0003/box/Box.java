package education.p0003.box;

import education.p0003.common.entity.Item;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Box implements Serializable {
    private static final long serialVersionUID = 1L;

    public List<ItemAndQuantity> itemAndQuantities = new ArrayList<>();

    public Integer getQuantityEnableToStore(Item item) {
        BigDecimal space = new BigDecimal("1.00");
        for (ItemAndQuantity itemAndQuantity : itemAndQuantities) {
            space = space.subtract(itemAndQuantity.item.getSize().multiply(BigDecimal.valueOf(itemAndQuantity.quantity)));
        }
        return space.divide(item.getSize(), 0, RoundingMode.FLOOR).intValue();
    }

    public Integer storeItems(Item item, Integer quantity) {
        Integer quantityEnableToStore = getQuantityEnableToStore(item);
        Integer quantityToStore = Math.min(quantityEnableToStore, quantity);
        if (quantityToStore > 0) {
            itemAndQuantities.add(new ItemAndQuantity(item, quantityToStore));
        }
        return quantityToStore;
    }

    public String getItemAndQuantitiesText() {
        return itemAndQuantities
                .stream()
                .map(it -> String.format("%s:%d", it.item.name, it.quantity))
                .collect(Collectors.joining(","));
    }
}

