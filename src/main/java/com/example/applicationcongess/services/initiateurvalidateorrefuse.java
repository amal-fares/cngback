package com.example.applicationcongess.services;


import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class initiateurvalidateorrefuse implements TaskListener {
    @Autowired
    TaskService taskService;
    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println("inititeur");
        String taskId = delegateTask.getId();
        Long initiateur = (Long) delegateTask.getVariable("initiateur");
        System.out.println(initiateur);
        String initiatorId = Long.toString(initiateur);
        taskService.addUserIdentityLink(taskId, initiatorId, "starter");
    }
}
