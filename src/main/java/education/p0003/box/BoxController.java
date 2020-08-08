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
import education.p0003.common.entity.ItemBox;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class BoxController {

    @Autowired
    BoxSession boxSession;
    @Autowired
    ItemDao itemDao;

    // リクエストパラメータ格納用のマップ（仮想棚）
    Map<String, Integer> virtualShelf = new TreeMap<>(Comparator.reverseOrder());
    Map<String, Item> itemMap = new HashMap<>();
    int maxItemId = 8;

    @GetMapping(path = "box/input")
    String input(Model model) {
        // item_tblの内容をname順に表示してください。
        InputPage page = new InputPage();

        itemMap = itemDao.selectAll()
                .stream()
                .collect(Collectors.toMap(Item::getId, it -> it));

        page.setItemList(new ArrayList<>(itemMap.values()));
        model.addAttribute("page", page);
        return "input";
    }
    
    @PostMapping(path = "box/check")
    String check(@RequestParam("amount[]")Integer[] numbers,
                 @RequestParam("id[]")String[] ids) {
        // 以下のルールのとおり、箱詰めの計算をしてください。
        // - 1箱にはサイズが1.00になるまで商品を入れられます。
        // - 出来るだけ箱の個数は少なくなるように詰めてください。
        //
        // PRGパターンを想定しています。
        // 計算結果をセッションに入れてリダイレクトし、リダイレクト先でセッションから取り出すようにしてください。
        // POST時にリダイレクトせずにページを表示すると、F5等でページを更新するとPOSTデータを再送信するため、警告ダイアログが表示されます。
        // またCSRF対策で不正なページ遷移のエラーになる可能性もあります。

        for (int i = 0; i < ids.length ; i++) {
            // キー:商品ID、値:在庫数
            virtualShelf.put(ids[i], numbers[i]);
        }

        List<ItemBox> itemBoxList = new ArrayList<>();
        // 最初から在庫がない場合は計算処理を行わない
        if (!isEmptyShelf()) {

            // サイズが大きい順に計算したいので最大商品IDで初期化
            int itemId = maxItemId;
            int num;
            ItemBox itemBox = null;
            StringBuilder sbForItemName = new StringBuilder();
            boolean empFlg = false;

            //
            // 計算処理
            //
            while (true) {

                if (itemBox == null) {
                    itemBox = new ItemBox();
                }

                // 商品の在庫を取得
                num = virtualShelf.get(String.valueOf(itemId));
                if (num > 0) {

                    // 個数が0以上の場合
                    // 商品IDからサイズを取得
                    Item item = itemMap.get(String.valueOf(itemId));
                    BigDecimal itemSize = new BigDecimal(item.getSize());

                    if (itemBox.getCapacity().compareTo(itemSize) >= 0) {

                        // 現在の容量が取得したサイズより大きい場合
                        // 箱詰め処理呼び出し
                        String itemName = item.getName();
                        packagingItems(itemId, num, itemSize, itemName, itemBox, sbForItemName);
                        empFlg = isEmptyShelf();

                    }
                }

                if (empFlg) {

                    // 箱詰めによりすべての在庫が払い出された場合
                    itemBoxList.add(itemBox);
                    break;

                } else {
                    if (itemBox.isMax()) {

                        // 現在の箱が満杯の場合
                        itemBoxList.add(itemBox);
                        itemId = maxItemId;
                        itemBox = null;
                        sbForItemName.delete(0, sbForItemName.length());

                    } else {
                        // 満杯ではない場合
                        itemId--;
                    }
                }
            }
        }

        boxSession.itemBoxList = itemBoxList;
        return "redirect:/box/result/";
    }

    @GetMapping(path = "box/result")
    String result(Model model) {
        ResultPage page = new ResultPage();
        page.setItemBoxList(boxSession.itemBoxList);

        model.addAttribute("page", page);
        return "result";
    }

    /*
     箱詰め処理
     */
    private void packagingItems(int id, int num, BigDecimal size, String name,
                                ItemBox itemBox, StringBuilder sb){

        int itemCnt = 0;
        // 箱の容量がサイズより小さくなるまでループ
        // 途中で在庫が0個になればループを抜ける
        while (itemBox.getCapacity().compareTo(size) >= 0) {
            if (num == 0) {
                break;
            }
            // 容量 - サイズ
            itemBox.setCapacity(itemBox.getCapacity().subtract(size));
            num -= 1;
            itemCnt++;
        }

        // 在庫の更新
        virtualShelf.put(String.valueOf(id), num);

        // 商品内訳を設定
        if (sb.length() > 0) {
            sb.append(", ");
        }
        sb.append(name);
        sb.append(":");
        sb.append(itemCnt);
        sb.append("個");

        if (isMax(itemBox)) {
            itemBox.setItems(sb.toString());
            itemBox.setMax(true);
        }

    }

    /*
     空棚判定
     */
    private boolean isEmptyShelf() {
        int num;
        for (String id : virtualShelf.keySet()) {
            num = virtualShelf.get(id);
            if (num > 0) {
                return false;
            }
        }
        return true;
    }

    /*
     満杯判定
     */
    private boolean isMax(ItemBox itemBox) {

        // 残容量より小さい商品のIDを取得
        List<Integer> itemIdList = itemDao.selectIdsBySize(itemBox.getCapacity());
        boolean maxFlag = true;
        if (itemIdList != null && !itemIdList.isEmpty()) {
            // 残個数を調査
            for (int itemId : itemIdList) {
                int remainingNum = virtualShelf.get(String.valueOf(itemId));
                if (remainingNum > 0) {
                    maxFlag = false;
                    break;
                }
            }
        }
        return maxFlag;
    }

    @Getter
    @Setter
    static public class InputPage {
        List<Item> itemList;
    }

    @Getter
    @Setter
    static public class ResultPage {
        List<ItemBox> itemBoxList;
    }
}
