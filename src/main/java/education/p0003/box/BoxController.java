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


        /*
            item取得
         */
        List<Item> itemList = itemDao.selectAll();

        /*
            View用データの作成
         */
        page.inputMap = new TreeMap<>();
        for (Item item : itemList) {
            page.inputMap.put(item.getName(), Arrays.asList(item.getSize(), String.format("%04d",Integer.valueOf(item.getId()).intValue())));
        }

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

        /*
            input画面より受け取った引数より取得したデータを配列に保存する。
         */
        String[] arrayData = getDispData.toArray(new String[getDispData.size()]);
        String[] arrayItem = getDispItem.toArray(new String[getDispItem.size()]);
        String[] arraySize = getDispSize.toArray(new String[getDispSize.size()]);

        Integer countLoop1;
        Integer countLoop2;
        Integer countData;
        Map<Integer, CalculationInformation> calcInfo = new HashMap<>();

        /*
            Item,Size,FlagのMap領域を作成し、値の設定を行う。
            この時同時に処理するデータの件数を取得する。
         */
        countData = 0;
        for (countLoop1 = 0; countLoop1 < arrayData.length; countLoop1++){
            if (arrayData[countLoop1].equals("0")) {
            } else {
                for (countLoop2 = 0; countLoop2 < Integer.valueOf(arrayData[countLoop1]); countLoop2++){
                    CalculationInformation workCalcInfo = new CalculationInformation();
                    workCalcInfo.item = arrayItem[countLoop1];
                    workCalcInfo.size = Double.parseDouble(arraySize[countLoop1]);
                    workCalcInfo.flag = 0;
                    calcInfo.put(countData, workCalcInfo);
                    countData++;
                }
            }
        }

        Set<Integer> keys = calcInfo.keySet();
        System.out.println(keys.size());
        System.out.println(keys);

        Collection<CalculationInformation> values = calcInfo.values();
        System.out.println(values.size());
        System.out.println(values);

        Double calcResult;
        Double calcResultBackup;
        Integer maxCount = 0;

        /*
            サイズの合計が1.0以下になるデータを取得する。
         */
        for (countLoop1 = 1; countLoop1 < countData; countLoop1++){
            calcResult = 0.0;
            for (countLoop2 = countData - 1; countLoop2 >= 0; countLoop2--){
                CalculationInformation workCalcInfo;
                if (calcInfo.get(countLoop2).flag == 0) {
                    calcResultBackup = calcResult;
                    calcResult += calcInfo.get(countLoop2).size;
                    if (calcResult <= 1.0) {
                        workCalcInfo = calcInfo.get(countLoop2);
                        workCalcInfo.setFlag(countLoop1);
                        maxCount = countLoop1;
                        if (calcResult == 1.0) break;
                    } else {
                        calcResult = calcResultBackup;
                    }
                }
            }
        }

        String workItem;
        Map<Integer, String> aggregateResult = new HashMap<>();
        Map<String, Integer> workItemCount = new TreeMap<>();
        /*
            同一の箱でのItem毎の件数を抽出する。
            その後、出力用データを作成する。
         */
        for (countLoop1 = 1; countLoop1 <= maxCount; countLoop1++){
            for (countLoop2 = 0; countLoop2 < countData; countLoop2++){
                if (calcInfo.get(countLoop2).flag == countLoop1) {
                    if (workItemCount.get(String.format("%02d", countLoop1) + calcInfo.get(countLoop2).item) == null) {
                        workItemCount.put(String.format("%02d", countLoop1) + calcInfo.get(countLoop2).item, 1);
                    } else {
                        workItemCount.put(String.format("%02d", countLoop1) + calcInfo.get(countLoop2).item, workItemCount.get(String.format("%02d", countLoop1) + calcInfo.get(countLoop2).item) + 1);
                    }
                }
            }
            workItem = "";
            for (String key : workItemCount.keySet()) {
                if (key.substring(0,2).equals(String.format("%02d", countLoop1))) {
                    workItem += key.substring(2) + ":" + workItemCount.get(key) + ", ";
                }
            }
            aggregateResult.put(countLoop1, workItem.substring(0, workItem.length() - 2));
        }

        System.out.println("aggregateResult : " + aggregateResult);

        boxSession.inputSessionMap = aggregateResult;

        return "redirect:/box/result/";
    }
    
    @GetMapping(path = "box/result")
    String result(Model model) {
        ResultPage page = new ResultPage();
        model.addAttribute("page", page);

        System.out.println(boxSession.temp); // セッションから100を取得

        page.resultMap = boxSession.inputSessionMap;

        return "result";
    }

    @Getter
    @Setter
    static public class InputPage {
        Map<String, List> inputMap;
    }

    @Getter
    @Setter
    static public class ResultPage {
        Map<Integer, String> resultMap;
    }

    /* Start Simplification */
    @Getter
    @Setter
    static public class CalculationInformation {
        String item;
        Double size;
        Integer flag;
    }
    /* End Simplification */
}
