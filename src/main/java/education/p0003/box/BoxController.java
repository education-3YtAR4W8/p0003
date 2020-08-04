package education.p0003.box;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
//            page.inputMap.put(item.getName(), item.getSize());
//            System.out.println(String.format("%04d",Integer.valueOf(item.getId()).intValue()));
            page.inputMap.put(item.getName(), Arrays.asList(item.getSize(), String.format("%04d",Integer.valueOf(item.getId()).intValue())));
//            page.idMap.put(item.getName(), item.getId());
//            inputSessionMap.put(item.getName(), item.getSize());
        }

//        System.out.println(page.inputMap.getKey());

        model.addAttribute("page", page);
        return "input";
    }
    
    @PostMapping(path = "box/check")
    String check(@RequestParam("inputData") List<String> getDispData, @RequestParam("item") List<String> getDispItem, @RequestParam("size") List<String> getDispSize) {
        // 以下のルールのとおり、箱詰めの計算をしてください。
        // - 1箱にはサイズが1.00になるまで商品を入れられます。
        // - 出来るだけ箱の個数は少なくなるように詰めてください。
        //
        // PRGパターンを想定しています。
        // 計算結果をセッションに入れてリダイレクトし、リダイレクト先でセッションから取り出すようにしてください。
        // POST時にリダイレクトせずにページを表示すると、F5等でページを更新するとPOSTデータを再送信するため、警告ダイアログが表示されます。
        // またCSRF対策で不正なページ遷移のエラーになる可能性もあります。

        boxSession.temp = 100; // セッションに100を保存

        boxSession.inputSessionMap = new TreeMap<>();
        for (String inData : getDispData) {
            System.out.println(inData);
        }
        for (String inItem : getDispItem) {
            System.out.println(inItem);
        }
        for (String inSize : getDispSize) {
            System.out.println(inSize);
        }

        String[] arrayData = getDispData.toArray(new String[getDispData.size()]);
        System.out.println("List = " + getDispData);
        System.out.println("配列 = " + Arrays.toString(arrayData));
        String[] arrayItem = getDispItem.toArray(new String[getDispItem.size()]);
        System.out.println("List = " + getDispItem);
        System.out.println("配列 = " + Arrays.toString(arrayItem));
        String[] arraySize = getDispSize.toArray(new String[getDispSize.size()]);
        System.out.println("List = " + getDispSize);
        System.out.println("配列 = " + Arrays.toString(arraySize));

        Integer i, j;
        Integer maxNumber;
        maxNumber = 0;
        for (i = 0; i < arrayData.length; i++){
            maxNumber += Integer.valueOf(arrayData[i]);
        }

        Double sizeNumber[] = new Double[100];
        Integer numberCount = 0;
        for (i = 0; i < arrayData.length; i++){
            if (arrayData[i].equals("0")) {

            } else {
                for (j = 0; j < Integer.valueOf(arrayData[i]); j++){
                    sizeNumber[numberCount] = Double.parseDouble(arraySize[i]);
                    numberCount++;
                }
            }
        }




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
//        Map<String, String> inputMap;
        Map<String, List> inputMap;
        Map<String, Integer> idMap;
        Map<String, Boolean> flagMap;
    }

    @Getter
    @Setter
    static public class ResultPage {
    }
}
