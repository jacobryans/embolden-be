package com.lambdaschool.tempEC;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableWebMvc
@EnableJpaAuditing
@SpringBootApplication
@EnableScheduling
@EnableSwagger2
public class EmpoweredConversationApplication
{

    public static void main(String[] args)
    {
        ApplicationContext ctx = SpringApplication.run(EmpoweredConversationApplication.class, args);

        DispatcherServlet dispatcherServlet = (DispatcherServlet)ctx.getBean("dispatcherServlet");
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);

    }

}
