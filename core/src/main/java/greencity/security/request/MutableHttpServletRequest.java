package greencity.security.request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.HashMap;
import java.util.Map;

public class MutableHttpServletRequest extends HttpServletRequestWrapper {
    private final Map<String, String> headers;

    /**
     * Constructor.
     * */
    public MutableHttpServletRequest(HttpServletRequest request) {
        super(request);
        this.headers = new HashMap<>();
    }

    /**
     * Put header to map.
     * */
    public void putHeader(String name, String value) {
        this.headers.put(name, value);
    }

    /**
     * Get header from map.
     * */
    public String getHeader(String name) {
        String headerValue = headers.get(name);
        if (headerValue != null) {
            return headerValue;
        }

        return ((HttpServletRequest) getRequest()).getHeader(name);
    }
}
