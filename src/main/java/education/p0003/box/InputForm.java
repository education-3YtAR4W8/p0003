package education.p0003.box;

import education.p0003.common.entity.Item;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class InputForm implements Serializable {
    private static final long serialVersionUID = 1L;

    List<String> itemIds;
    List<String> itemQuantities;

    Boolean isBadRequest = false;
    Boolean isInvalidFormatOfItemQuantities = false;

    public String getItemQuantity(Integer itemId) {
        for (Integer i = 0; i < itemIds.size(); i++) {
            if (itemIds.get(i).equals(itemId.toString())) {
                return i < itemQuantities.size() ? itemQuantities.get(i) : "0";
            }
        }
        return "0";
    }

    public void validate(Map<Integer, Item> itemMap) {
        clearErrors();

        if (itemIds.size() != itemQuantities.size()) {
            isBadRequest = true;
        }

        if (itemQuantities.stream().anyMatch(it -> !isIntegerOrEmpty(it))) {
            isInvalidFormatOfItemQuantities = true;
        }

        if (itemIds.stream().anyMatch(it -> !isInteger(it))) {
            isBadRequest = true;
        }

        if (itemIds.stream().anyMatch(it -> !itemMap.containsKey(it))) {
            isBadRequest = true;
        }
    }

    private void clearErrors() {
        isBadRequest = false;
        isInvalidFormatOfItemQuantities = false;
    }

    public Boolean hasError() {
        return isBadRequest || isInvalidFormatOfItemQuantities;
    }

    private Boolean isInteger(String value) {
        if (!value.matches("\\d{1,10}")) {
            return false;
        }
        if (Long.parseLong(value) > Integer.MAX_VALUE) {
            return false;
        }
        return true;
    }

    private Boolean isIntegerOrEmpty(String value) {
        if (value == null) {
            return true;
        }
        return isInteger(value);
    }
}
