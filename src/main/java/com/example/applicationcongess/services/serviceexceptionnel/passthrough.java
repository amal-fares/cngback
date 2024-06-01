package com.example.applicationcongess.services.serviceexceptionnel;


import com.example.applicationcongess.controller.Demande_congecontr;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class passthrough implements JavaDelegate {
    @Autowired
    Demande_congecontr demande_congecontr;
    @Autowired
    RuntimeService runtimeService;


    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("decision accepte avec missingattachement false ");
runtimeService.setVariable(demande_congecontr.getCurrentProcessInstanceId(), "passthrough", true);
    }
}
