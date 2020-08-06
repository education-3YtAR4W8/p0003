package education.p0003.box;

import education.p0003.common.dao.ItemDao;
import education.p0003.common.entity.Item;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class BoxController {

    @Autowired
    BoxSession boxSession;
    @Autowired
    ItemDao itemDao;

    @GetMapping(path = "box/input")
    String input(Model model) {
        // item_tblの内容をname順に表示してください。
        InputPage page = new InputPage();
        page.itemList = itemDao.selectAllOrderByName();
        model.addAttribute("page", page);
        model.addAttribute("products", new ProductForm());
        return "input";
    }
    
    @PostMapping(path = "box/check")
    String check(@ModelAttribute ProductForm products, Model model) {
        // 以下のルールのとおり、箱詰めの計算をしてください。
        // - 1箱にはサイズが1.00になるまで商品を入れられます。
        // - 出来るだけ箱の個数は少なくなるように詰めてください。
        //
        // PRGパターンを想定しています。
        // 計算結果をセッションに入れてリダイレクトし、リダイレクト先でセッションから取り出すようにしてください。
        // POST時にリダイレクトせずにページを表示すると、F5等でページを更新するとPOSTデータを再送信するため、警告ダイアログが表示されます。
        // またCSRF対策で不正なページ遷移のエラーになる可能性もあります。
//        boxSession.temp = 100; // セッションに100を保存

        Map<Integer,Integer> inputQuantityMap = products.getProducts().stream()
                .filter(it -> it.quantity > 0)
                .collect(Collectors.toMap(it -> it.id, it->it.quantity));
        List<Integer> inputIds = new ArrayList<>(inputQuantityMap.keySet());

        List<Item> itemList = itemDao.selectByIdOrderBySize(inputIds);
        List<Integer> sortedIdList = new ArrayList<>();
        for(Item item : itemList) {
            Integer quantity = inputQuantityMap.get(item.getId());
            for (int i = 0; i < quantity; i++) {
                sortedIdList.add(item.getId());
            }
        }

        Map<Integer, Double> sizeMap = itemList.stream().collect(Collectors.toMap(Item::getId, Item::getSize));
        Map<Integer, String> nameMap = itemList.stream().collect(Collectors.toMap(Item::getId, Item::getName));
        List<CulcBox> culcList = new ArrayList<>();
        ListIterator<CulcBox> iteratorList;

        for (Integer sortedId : sortedIdList) {
            boolean isAdd = false;
            Double itemSize = sizeMap.get(sortedId);
            iteratorList = culcList.listIterator();
            while(iteratorList.hasNext()){
                CulcBox iteratorBox = iteratorList.next();
                if (iteratorBox.capacity + itemSize <= 1) {
                    iteratorBox.packedIdList.add(sortedId);
                    iteratorBox.capacity += itemSize;
                    isAdd = true;
                    break;
                }
            }
            if (!isAdd) {
                CulcBox newBox = new CulcBox();
                newBox.packedIdList.add(sortedId);
                newBox.capacity = itemSize;
                iteratorList.add(newBox);
            }
        }

        List<Box> resultBoxList = new ArrayList<>();
        for (CulcBox culcBox : culcList) {
            Map<Integer, Long> counts = culcBox.packedIdList.stream().collect(Collectors.groupingBy(it -> it, Collectors.counting()));
            String productContent = counts.entrySet().stream().map(it -> nameMap.get(it.getKey()) + ":" + it.getValue().intValue()).collect(Collectors.joining(", "));
            Box box = new Box();
            box.setProductContent(productContent);
            resultBoxList.add(box);
        }

        boxSession.boxList = resultBoxList;
        return "redirect:/box/result/";
    }
    
    @GetMapping(path = "box/result")
    String result(Model model) {
        ResultPage page = new ResultPage();
        page.boxList =boxSession.boxList;
        model.addAttribute("page", page);
        return "result";
    }

    @Getter
    @Setter
    static public class InputPage {
        List<Item> itemList;
    }

    @Getter
    @Setter
    static public class ResultPage {
        List<Box> boxList;
    }

    @Getter
    @Setter
    static public class ProductForm {
        private List<Product> products;
    }

    @Getter
    @Setter
    static public class Product {
        private Integer id;
        private Integer quantity;
        private Double size;
        private String name;
    }

    @Getter
    @Setter
    static public class Box {
        String productContent;
    }

    @Getter
    @Setter
    static public class CulcBox {
        List<Integer> packedIdList = new ArrayList<>();
        Double capacity = 0.0;
    }
}
