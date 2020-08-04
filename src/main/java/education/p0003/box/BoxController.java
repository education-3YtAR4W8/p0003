package education.p0003.box;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class BoxController {

    @Autowired
    BoxSession boxSession;

    @GetMapping(path = "box/input")
    String input(Model model) {
        InputPage page = new InputPage();
        model.addAttribute("page", page);
        return "input";
    }

    @PostMapping(path = "box/check")
    String check(@RequestParam("bigProduct")double inputBigProduct,
                 @RequestParam("mediumProduct")double inputMediumProduct,
                 @RequestParam("smallProduct")double inputSmallProduct) {

        BoxController.maxBoxNumber = 0;
        int bigProduct = (int) inputBigProduct;
        int mediumProduct = (int) inputMediumProduct;
        int smallProduct = (int) inputSmallProduct;

        List<BoxInfo> boxInfoResult = new ArrayList<>();
        while(bigProduct > 0){
            if(smallProduct > 0){
                BoxController.boxInfoResultAdd(boxInfoResult,"商品(大):1, 商品(小):1");
                smallProduct--;
            }else{
                BoxController.boxInfoResultAdd(boxInfoResult,"商品(大):1");
            }
            bigProduct--;
        }

        while(mediumProduct > 2){
            if(smallProduct > 0){
                BoxController.boxInfoResultAdd(boxInfoResult,"商品(中):3, 商品(小):1");
                smallProduct--;
            }else{
                BoxController.boxInfoResultAdd(boxInfoResult,"商品(中):3");
            }
            mediumProduct = mediumProduct -3;
        }
        if(mediumProduct == 2){
            if(smallProduct >= 4){
                BoxController.boxInfoResultAdd(boxInfoResult,"商品(中):2, 商品(小):4");
                smallProduct = smallProduct - 4;
            }else if(smallProduct > 0 && smallProduct <= 3){
                BoxController.boxInfoResultAdd(boxInfoResult,"商品(中):2, 商品(小):" + smallProduct);
                smallProduct = 0;
            }else{
                BoxController.boxInfoResultAdd(boxInfoResult,"商品(中):2");
            }
        }else if(mediumProduct == 1){
            if(smallProduct >= 7){
                BoxController.boxInfoResultAdd(boxInfoResult,"商品(中):1, 商品(小):7");
                smallProduct = smallProduct - 7;
            }else if(smallProduct > 0 && smallProduct <= 6){
                BoxController.boxInfoResultAdd(boxInfoResult,"商品(中):1, 商品(小):" + smallProduct);
                smallProduct = 0;
            }else{
                BoxController.boxInfoResultAdd(boxInfoResult,"商品(中):1");
            }
        }

        mediumProduct = 0;

        while(smallProduct > 0){
            if(smallProduct / 10 >= 1){
                BoxController.boxInfoResultAdd(boxInfoResult,"商品(小):10");
                smallProduct = smallProduct - 10;
            }else if(smallProduct > 0 && smallProduct <= 9){
                BoxController.boxInfoResultAdd(boxInfoResult,"商品(小):" + smallProduct);
                smallProduct = 0;
            }
        }

        boxSession.setBoxInfo(boxInfoResult);
        // 以下のルールのとおり、箱詰めの計算をしてください。
        // - 1箱にはサイズが1.00になるまで商品を入れられます。
        // - 出来るだけ箱の個数は少なくなるように詰めてください。
        //
        // PRGパターンを想定しています。
        // 計算結果をセッションに入れてリダイレクトし、リダイレクト先でセッションから取り出すようにしてください。
        // POST時にリダイレクトせずにページを表示すると、F5等でページを更新するとPOSTデータを再送信するため、警告ダイアログが表示されます。
        // またCSRF対策で不正なページ遷移のエラーになる可能性もあります。
        return "redirect:/box/result/";
    }
    
    @GetMapping(path = "box/result")
    String result(Model model) {
        ResultPage page = new ResultPage();
        model.addAttribute("boxInfos", boxSession.getBoxInfo());
        return "result";
    }

    @Getter
    @Setter
    static public class InputPage {
    }

    @Getter
    @Setter
    static public class ResultPage {
    }

    static int maxBoxNumber = 0;

    public static void boxInfoResultAdd(List<BoxInfo> boxInfoResult,String setBoxInfoMessage){
        BoxInfo boxInfo = new BoxInfo();
        boxInfo.setBoxNumber(++BoxController.maxBoxNumber);
        boxInfo.setBoxInfoMessage(setBoxInfoMessage);
        boxInfoResult.add(boxInfo);
    }
}
