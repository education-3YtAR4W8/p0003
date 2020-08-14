package education.p0003.box;

import education.p0003.common.dao.ItemDao;
import education.p0003.common.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class BoxController {

    @Autowired
    ItemDao itemDao;

    @Autowired
    BoxSession boxSession;

    @GetMapping(path = "box/input")
    public String input(Model model) {
        InputForm inputForm = Optional.ofNullable(boxSession.inputForm).orElse(new InputForm());
        InputPage page = new InputPage(itemDao.selectAllSortedWithName(), inputForm);
        model.addAttribute("page", page);
        return "input";
    }

    @PostMapping(path = "box/check")
    public String check(@ModelAttribute InputForm inputForm) {
        boxSession.inputForm = inputForm;

        Map<Integer, Item> itemMap = itemDao.selectAllSortedWithName()
                .stream()
                .collect(Collectors.toMap(it -> it.getId(), it -> it));

        inputForm.validate(itemMap);
        if (inputForm.hasError()) {
            return "redirect:/box/input/";
        }

        List<Box> boxes = new ArrayList<>();
        List<ItemAndQuantity> itemAndQuantities = convertToItemAndQuantities(
                inputForm.itemIds,
                inputForm.itemQuantities,
                itemMap);
        for (ItemAndQuantity itemAndQuantity : itemAndQuantities) {
            for (Box box : boxes) {
                Integer storedQuantity = box.storeItems(itemAndQuantity);
                itemAndQuantity.quantity -= storedQuantity;

                if (itemAndQuantity.quantity == 0) {
                    break;
                }
            }

            while (itemAndQuantity.quantity > 0) {
                Box box = new Box();
                Integer storedQuantity = box.storeItems(itemAndQuantity);
                itemAndQuantity.quantity -= storedQuantity;
                boxes.add(box);
            }
        }

        boxSession.boxes = boxes;
        return "redirect:/box/result/";
    }

    @GetMapping(path = "box/result")
    public String result(Model model) {
        ResultPage page = new ResultPage(boxSession.boxes);
        model.addAttribute("page", page);

        return "result";
    }

    private List<ItemAndQuantity> convertToItemAndQuantities(List<String> itemIds, List<String> itemQuantities, Map<Integer, Item> itemMap) {
        List<ItemAndQuantity> itemAndQuantities = new ArrayList<>();
        for (Integer i = 0; i < itemIds.size(); i++ ) {
            Item item = itemMap.get(Integer.parseInt(itemIds.get(i)));
            ItemAndQuantity itemAndQuantity = new ItemAndQuantity(item, Integer.parseInt(itemQuantities.get(i)));
            itemAndQuantities.add(itemAndQuantity);
        }
        return itemAndQuantities;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    static public class InputPage {
        private List<Item> items;
        private InputForm inputForm;

        public static final DecimalFormat SIZE_FORMATTER = new DecimalFormat("0.00");
    }

    @Getter
    @Setter
    @AllArgsConstructor
    static public class ResultPage {
        private List<Box> boxes;
    }
}
