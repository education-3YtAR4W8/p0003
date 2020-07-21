package education.p0003.box;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BoxController {

    @GetMapping(path = "box/input")
    String input(Model model) {
        InputPage page = new InputPage();
        model.addAttribute("page", page);
        return "input";
    }
    
    @PostMapping(path = "box/check")
    String check() {
        return "redirect:/box/result/";
    }
    
    @GetMapping(path = "box/result")
    String result(Model model) {
        ResultPage page = new ResultPage();
        model.addAttribute("page", page);
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
}
