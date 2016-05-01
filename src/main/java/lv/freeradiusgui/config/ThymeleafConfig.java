package lv.freeradiusgui.config;

/**
 * Created by Dan on 30.04.2016.
 */
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import java.util.HashSet;
import java.util.Set;


@Configuration
public class ThymeleafConfig {

    @Bean
    public ThymeleafViewResolver viewResolver() {

        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(getTemplateEngine());

        return resolver;
    }

    private SpringTemplateEngine getTemplateEngine() {

        SpringTemplateEngine engine = new SpringTemplateEngine();

        Set<ServletContextTemplateResolver> resolvers = new HashSet<ServletContextTemplateResolver>();
        resolvers.add(createResolver("/WEB-INF/views/", 1));
        engine.setTemplateResolvers(resolvers);

        engine.addDialect(new SpringSecurityDialect());

        return engine;
    }

    private ServletContextTemplateResolver createResolver(String prefix, int order) {

        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver();
        templateResolver.setPrefix(prefix);
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setCacheable(false);
        templateResolver.setOrder(order);

        return templateResolver;
    }

}
