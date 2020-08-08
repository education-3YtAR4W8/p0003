package education.p0003.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@Data
public class ItemBox {
    BigDecimal capacity = new BigDecimal(1.00);
    String items = "";
    boolean isMax = false;
}
