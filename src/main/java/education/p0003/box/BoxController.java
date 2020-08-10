package education.p0003.box;

import education.p0003.common.dao.ItemDao;
import education.p0003.common.entity.Item;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        page.items = itemDao.nameSortSelectAll();

        model.addAttribute("page", page);
        return "input";
    }

    @PostMapping(path = "box/check")
    String check(@RequestParam Map<String, String> queryParam, Model model) {
        // 以下のルールのとおり、箱詰めの計算をしてください。
        // - 1箱にはサイズが1.00になるまで商品を入れられます。
        // - 出来るだけ箱の個数は少なくなるように詰めてください。
        //
        // PRGパターンを想定しています。
        // 計算結果をセッションに入れてリダイレクトし、リダイレクト先でセッションから取り出すようにしてください。
        // POST時にリダイレクトせずにページを表示すると、F5等でページを更新するとPOSTデータを再送信するため、警告ダイアログが表示されます。
        // またCSRF対策で不正なページ遷移のエラーになる可能性もあります。

        //入力値チェック
        ArrayList<Map.Entry<String,String>> inputCheck = new ArrayList<Map.Entry<String,String>>();
        inputCheck = (ArrayList<Map.Entry<String, String>>) queryParam.entrySet().stream()
                                          .filter(it -> !(it.getValue().matches("^[0-9]+$")))
                                          .collect(Collectors.toList());

        if(inputCheck.size() != 0){
            boxSession.text = "半角数字以外の文字が入力されていたため、計算できません。";
            return "redirect:/box/result/";
        }


        int itemSize;
        List<Box> boxes = new ArrayList<Box>();
        int maxBoxFreeSpace = 0;
        List<Item> items = new ArrayList<Item>();
        items = itemDao.sizeDescSortSelectAll();

        //入力が0のデータを削除
        queryParam = queryParam.entrySet().stream()
                .filter(it -> !(it.getValue().equals("0")))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1,HashMap::new));

        for(Item item : items){

            for(Map.Entry<String, String> entry : queryParam.entrySet()){
                if(item.getId().contains(entry.getKey())){
                    itemSize = (int) (item.getSize() * 100);

                    //ここからサイズの大きい順にボックスに入れ込む
                    int itemNum = Integer.parseInt(entry.getValue());
                    int freeSpace = 100;
                    for(int itemCount = 1; itemCount <= itemNum; itemCount++){

                        //既に箱があり、入れるスペースのある場合の処理
                        if(boxes.size() != 0 && itemSize <= maxBoxFreeSpace){
                            int putInPossibleElement = getPutInPossibleElement(boxes, itemSize);
                            int putInNum = (int)(boxes.get(putInPossibleElement).freeSpace / itemSize);

                            if(putInNum > itemNum){
                                putInNum = itemNum;
                                itemNum = 0;
                            }else{
                                itemNum = itemNum - putInNum;
                                itemCount = 0;
                            }

                            BoxItemInfo boxItemInfo;
                            boxItemInfo = boxItemInfoRegister(item.getName(), itemSize, putInNum);

                            boxes.get(putInPossibleElement).boxItemsInfo.add(boxItemInfo);
                            boxes.get(putInPossibleElement).setFreeSpace(boxes.get(putInPossibleElement).freeSpace - (putInNum * itemSize));

                            maxBoxFreeSpace = getMaxBoxFreeSpace(boxes);

                        //物を入れるスペースがない場合の処理（事前処理）
                        }else{
                            freeSpace = freeSpace - itemSize;

                            boolean lastItemJudge = false;
                            if(itemCount == itemNum){
                                lastItemJudge = true;

                                BoxItemInfo boxItemInfo;
                                boxItemInfo = boxItemInfoRegister(item.getName(), itemSize, itemCount);

                                Box box = new Box();
                                box.boxItemsInfo.add(boxItemInfo);
                                box.setFreeSpace(freeSpace);

                                boxes.add(box);
                            }

                            if(freeSpace - itemSize < 0 && !lastItemJudge){
                                BoxItemInfo boxItemInfo;
                                boxItemInfo = boxItemInfoRegister(item.getName(), itemSize, itemCount);

                                Box box = new Box();
                                box.setFreeSpace(freeSpace);
                                box.boxItemsInfo.add(boxItemInfo);

                                boxes.add(box);

                                if(freeSpace > maxBoxFreeSpace){
                                    maxBoxFreeSpace = freeSpace;
                                }

                                itemNum = itemNum - itemCount ;
                                itemCount = 0;

                                freeSpace = 100;
                            }
                        }
                    }
                }
            }
        }

        List<DisplayBox> displayBoxList = new ArrayList<DisplayBox>();

        int displayNum = 0;

        for(Box box : boxes){
            String displayItemInfo = "";

            for(BoxItemInfo displayBoxItemInfo : box.boxItemsInfo){
                displayItemInfo = displayItemInfo + displayBoxItemInfo.getItemName() + ":" + displayBoxItemInfo.getItemCount() + ",";
            }

            DisplayBox displayBox = new DisplayBox();
            displayBox.setNum(++displayNum);
            displayBox.setItemInfo(displayItemInfo.substring(0, displayItemInfo.length() - 1));

            displayBoxList.add(displayBox);
        }

        boxSession.displayBoxList = displayBoxList;
        return "redirect:/box/result/";
    }

    BoxItemInfo boxItemInfoRegister(String name, int size , int itemCount){
        BoxItemInfo boxItemInfo = new BoxItemInfo();
        boxItemInfo.setItemName(name);
        boxItemInfo.setSize(size);
        boxItemInfo.setItemCount(itemCount);

        return boxItemInfo;
    }

    int getPutInPossibleElement(List<Box> boxes, int itemSize){
        int elementNum = 0;
        for(Box box : boxes){
            if(box.freeSpace >= itemSize){
            break;
            }
            elementNum++;
        }
        return elementNum;
    }

    int getMaxBoxFreeSpace(List<Box> boxes){
        int maxFreeSpace = 0;
        for(Box box : boxes){
            if(maxFreeSpace < box.freeSpace){
                maxFreeSpace = box.freeSpace;
            }
        }
        return maxFreeSpace;
    }

    @GetMapping(path = "box/result")
    String result(Model model) {
        ResultPage page = new ResultPage();
        page.displayBoxes = boxSession.displayBoxList;
        page.text = boxSession.text;
        model.addAttribute("page", page);

        System.out.println(boxSession.displayBoxList);
        return "result";
    }

    @Getter
    @Setter
    static public class InputPage {
        private List<Item> items;
    }

    @Getter
    @Setter
    static public class ResultPage {
        private List<DisplayBox> displayBoxes;
        private String text;
    }

    @Data
    static public class DisplayBox{
        private int num;
        private String itemInfo;
    }

    @Data
    static public class Box {
        private List<BoxItemInfo> boxItemsInfo = new ArrayList<>();
        private int freeSpace = 100;
    }

    @Data
    static public class BoxItemInfo {
        private String itemName;
        private int size;
        private int itemCount;

    }
}
