package education.p0003.box;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Component
@Scope(value="session", proxyMode= ScopedProxyMode.TARGET_CLASS)
public class BoxSession implements Serializable {
    private static final long serialVersionUID = 1L;

    Map<String, String> inputSessionMap;

    Integer temp = 0;
}
