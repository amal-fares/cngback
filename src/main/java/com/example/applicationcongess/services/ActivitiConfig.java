package com.example.applicationcongess.services;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;

import org.activiti.engine.repository.DeploymentBuilder;

import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ActivitiConfig  {

    @Autowired
    private RepositoryService repositoryService;

    public void deployMyProcess() {

    Deployment deployment = repositoryService.createDeployment()
            .addClasspathResource("processes/my-process.bpmn20 (4).bpmn")
            .deploy();
            String deploymentId = deployment.getId();
        DeploymentQuery deploymentQuery = repositoryService.createDeploymentQuery();
        Deployment deployedProcess = deploymentQuery.deploymentId(deploymentId).singleResult();

        if (deployedProcess != null) {
            System.out.println("Process is deployed.");
        } else {
            System.out.println("Process is not deployed.");
        }
        }


    @Autowired
     PlatformTransactionManager transactionManager;

    @Autowired
     JavaMailSender mailSender;

    @Value("${spring.activiti.database-schema-update}")
     String databaseSchemaUpdate;



    @Bean
    public TaskService taskService(SpringProcessEngineConfiguration processEngineConfiguration) {
        return processEngineConfiguration.getTaskService();
    }


}


