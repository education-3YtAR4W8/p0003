package education.p0003.box;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Scope(value="session", proxyMode= ScopedProxyMode.TARGET_CLASS)
public class BoxSession implements Serializable {
    private static final long serialVersionUID = 1L;

    public Boolean isInvalidFormatForCount = false;
    public Boolean isBadRequest = false;

    public void clearErrors() {
        isInvalidFormatForCount = false;
        isBadRequest = false;
    }

    public Boolean hasError() {
        return isInvalidFormatForCount || isBadRequest;
    }
}
