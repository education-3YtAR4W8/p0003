package education.p0003.box;

import education.p0003.common.entity.ItemBox;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Component
@Scope(value="session", proxyMode= ScopedProxyMode.TARGET_CLASS)
public class BoxSession implements Serializable {
    private static final long serialVersionUID = 1L;

    List<ItemBox> itemBoxList;

}
