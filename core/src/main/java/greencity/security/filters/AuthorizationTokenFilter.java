package greencity.security.filters;

import greencity.security.request.MutableHttpServletRequest;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class AuthorizationTokenFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {
        MutableHttpServletRequest mutableHttpServletRequest =
            new MutableHttpServletRequest((HttpServletRequest) servletRequest);
        if (mutableHttpServletRequest.getRequestURI().contains("management")) {
            String header = "Bearer " + Storage.get("accessToken");
            mutableHttpServletRequest.putHeader("Authorization", header);
        }
        filterChain.doFilter(mutableHttpServletRequest, servletResponse);
    }
}
