package education.p0003.box;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import education.p0003.common.dao.ItemDao;
import education.p0003.common.entity.Item;

import java.util.*;

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

        // item取得
        List<Item> itemList = itemDao.selectAll();

        page.inputMap = new TreeMap<>();
        for (Item item : itemList) {
            page.inputMap.put(item.getName(), item.getSize());
            page.inputMap.put(item.getName(), item.getId());
//            inputSessionMap.put(item.getName(), item.getSize());
        }

//        System.out.println(page.inputMap.getKey());

        model.addAttribute("page", page);
        return "input";
    }
    
    @PostMapping(path = "box/check")
    String check() {
        // 以下のルールのとおり、箱詰めの計算をしてください。
        // - 1箱にはサイズが1.00になるまで商品を入れられます。
        // - 出来るだけ箱の個数は少なくなるように詰めてください。
        //
        // PRGパターンを想定しています。
        // 計算結果をセッションに入れてリダイレクトし、リダイレクト先でセッションから取り出すようにしてください。
        // POST時にリダイレクトせずにページを表示すると、F5等でページを更新するとPOSTデータを再送信するため、警告ダイアログが表示されます。
        // またCSRF対策で不正なページ遷移のエラーになる可能性もあります。

        boxSession.temp = 100; // セッションに100を保存

//        System.out.println(inputSessionMap.inputMap);

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
    static public class InputPage {
        Map<String, String> inputMap;
        Map<String, Integer> idMap;
        Map<String, Boolean> flagMap;
    }

    @Getter
    @Setter
    static public class ResultPage {
    }
}
