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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class BoxController {

    @Autowired
    ItemDao itemDao;

    @Autowired
    BoxSession boxSession;

    @GetMapping(path = "box/input")
    String input(Model model) {
        InputPage page = new InputPage(itemDao.selectAllSortedWithName());
        model.addAttribute("page", page);
        return "input";
    }

    private void validate(List<String> ids, List<String> counts, Map<Integer, Item> itemMap) {
        boxSession.clearErrors();

        if (ids.size() != counts.size()) {
            boxSession.isBadRequest = true;
        }

        if (counts.stream().anyMatch(it -> !isIntegerOrEmpty(it))) {
            boxSession.isInvalidFormatForCount = true;
        }

        if (ids.stream().anyMatch(it -> !isInteger(it))) {
            boxSession.isBadRequest = true;
        }

        if (ids.stream().anyMatch(it -> !itemMap.containsKey(it))) {
            boxSession.isBadRequest = true;
        }
    }
    
    @PostMapping(path = "box/check")
    String check(@RequestParam("ids") List<String> ids, @RequestParam("counts") List<String> counts) {
        // 以下のルールのとおり、箱詰めの計算をしてください。
        // - 1箱にはサイズが1.00になるまで商品を入れられます。
        // - 出来るだけ箱の個数は少なくなるように詰めてください。
        //
        // PRGパターンを想定しています。
        // 計算結果をセッションに入れてリダイレクトし、リダイレクト先でセッションから取り出すようにしてください。
        // POST時にリダイレクトせずにページを表示すると、F5等でページを更新するとPOSTデータを再送信するため、警告ダイアログが表示されます。
        // またCSRF対策で不正なページ遷移のエラーになる可能性もあります。
        Map<Integer, Item> itemMap = itemDao.selectAllSortedWithName().stream().collect(Collectors.toMap(it->it.getId(), it->it));

        validate(ids, counts, itemMap);
        if (boxSession.hasError()) {
            return "redirect:/box/input/";
        }

        




        return "redirect:/box/result/";
    }
    
    @GetMapping(path = "box/result")
    String result(Model model) {
        ResultPage page = new ResultPage();
        model.addAttribute("page", page);

        System.out.println(boxSession.temp); // セッションから100を取得
        return "result";
    }

    @Getter
    @Setter
    @AllArgsConstructor
    static public class InputPage {
        private List<Item> items;

        public static final DecimalFormat SIZE_FORMATTER = new DecimalFormat("0.00");
    }

    @Getter
    @Setter
    static public class ResultPage {


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
