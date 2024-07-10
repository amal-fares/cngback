package com.example.applicationcongess;

//import org.activiti.spring.SpringProcessEngineConfiguration;
//import org.activiti.spring.boot.SecurityAutoConfiguration;
//import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import javax.sql.DataSource;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)

@Configuration
public class ApplicationcongessApplication {

    @Primary
    @Bean
    public TaskExecutor primaryTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // add necessary properties to the executor
        return executor;
    }
    public static void main(String[] args) {

        ApplicationContext apc = SpringApplication.run(ApplicationcongessApplication.class, args);
    for ( String s : apc.getBeanDefinitionNames()){
        System.out.println(s);
    }
    }
    /*@Bean
    public SpringProcessEngineConfiguration processEngineConfiguration(
            DataSource dataSource,
            PlatformTransactionManager transactionManager) {
        SpringProcessEngineConfiguration config = new SpringProcessEngineConfiguration();
        config.setDataSource(dataSource);
        config.setTransactionManager(transactionManager);
        config.setDatabaseSchemaUpdate("true");
        config.setJobExecutorActivate(true);
        return config;
    }*/

}
