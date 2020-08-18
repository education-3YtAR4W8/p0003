package education.p0003.common;

import org.springframework.util.StringUtils;

public class Utils {
    public static Boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Boolean isIntegerOrEmpty(String value) {
        if (StringUtils.isEmpty(value)) {
            return true;
        }
        return isInteger(value);
    }

}
