package cosmo_memories.Balamb.config.Jsoup;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<JsoupCleanFilter> jsoupFilter() {
        FilterRegistrationBean<JsoupCleanFilter> bean = new FilterRegistrationBean<>();

        bean.setFilter(new JsoupCleanFilter());
        bean.addUrlPatterns("/*");
        bean.setOrder(1);
        return bean;
    }
}
