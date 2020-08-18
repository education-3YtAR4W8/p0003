package education.p0003.box;

import education.p0003.common.Utils;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

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

        boxSession.inputForm.validate(itemMap);
        if (boxSession.inputForm.hasError()) {
            return "redirect:/box/input/";
        }

        List<Box> boxes = new ArrayList<>();
        List<ItemAndQuantity> inputValidItemAndQuantitiesSortedInDescendingWithSize = inputForm.rows
                .stream()
                .map(it -> new ItemAndQuantity(
                        itemMap.get(Integer.parseInt(it.itemId)),
                        Utils.isInteger(it.quantity) ? Integer.parseInt(it.quantity) : 0
                ))
                .filter(it -> it.quantity > 0)
                .sorted((itemAndQuantity1, itemAndQuantity2) -> {
                    return itemAndQuantity2.item.getSize().compareTo(itemAndQuantity1.item.getSize());
                })
                .collect(Collectors.toList());

        for (ItemAndQuantity itemAndQuantity : inputValidItemAndQuantitiesSortedInDescendingWithSize) {
            Integer quantity = itemAndQuantity.quantity;
            for (Box box : boxes) {
                Integer storedQuantity = box.storeItems(itemAndQuantity.item, quantity);
                quantity -= storedQuantity;

                if (quantity == 0) {
                    break;
                }
            }

            while (quantity > 0) {
                Box box = new Box();
                Integer storedQuantity = box.storeItems(itemAndQuantity.item, quantity);
                quantity -= storedQuantity;
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
