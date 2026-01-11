package cosmo_memories.Balamb.config.Jsoup;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public class JsoupCleanFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        JsoupCleanRequestWrapper cleanRequest = new JsoupCleanRequestWrapper((HttpServletRequest) request);
        chain.doFilter(cleanRequest, response);
    }
}
