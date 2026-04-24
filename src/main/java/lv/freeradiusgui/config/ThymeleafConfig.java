package lv.freeradiusgui.config;

import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Configuration
public class ThymeleafConfig {

    @Autowired private ApplicationContext applicationContext;

    @Bean
    public ThymeleafViewResolver viewResolver() {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(getTemplateEngine());

        return resolver;
    }

    private SpringTemplateEngine getTemplateEngine() {
        SpringTemplateEngine engine = new SpringTemplateEngine();

        Set<ITemplateResolver> resolvers = new LinkedHashSet<ITemplateResolver>();
        resolvers.add(createResolver("/WEB-INF/views/", 1));
        engine.setTemplateResolvers(resolvers);

        engine.addDialect(new SpringSecurityDialect());

        return engine;
    }

    private SpringResourceTemplateResolver createResolver(String prefix, int order) {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(applicationContext);
        templateResolver.setPrefix(prefix);
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCacheable(false);
        templateResolver.setOrder(order);

        return templateResolver;
    }
}
