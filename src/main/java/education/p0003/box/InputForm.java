package education.p0003.box;

import education.p0003.common.Utils;
import education.p0003.common.entity.Item;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class InputForm implements Serializable {
    private static final long serialVersionUID = 1L;

    public List<InputRow> rows = new ArrayList<>();

    public Boolean isBadRequest = false;
    public Boolean isInvalidFormatOfItemQuantity = false;

    public String getItemQuantity(String itemId) {
        for (InputRow inputRow : rows) {
            if (itemId.equals(inputRow.itemId)) {
                return inputRow.quantity;
            }
        }
        return "0";
    }

    public void validate(Map<Integer, Item> itemMap) {
        clearErrors();

        if (rows.stream().anyMatch(it -> !Utils.isIntegerOrEmpty(it.quantity))) {
            isInvalidFormatOfItemQuantity = true;
        }

        if (rows.stream().anyMatch(it -> !Utils.isInteger(it.itemId))) {
            isBadRequest = true;
        } else if (rows.stream().anyMatch(it -> !itemMap.containsKey(Integer.parseInt(it.itemId)))) {
            isBadRequest = true;
        }
    }

    private void clearErrors() {
        isBadRequest = false;
        isInvalidFormatOfItemQuantity = false;
    }

    public Boolean hasError() {
        return isBadRequest || isInvalidFormatOfItemQuantity;
    }

    @Data
    public static class InputRow implements Serializable {
        private static final long serialVersionUID = 1L;

        public String itemId;
        public String quantity;
    }

}
