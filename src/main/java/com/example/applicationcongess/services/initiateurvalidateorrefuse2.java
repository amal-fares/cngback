package com.example.applicationcongess.services;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class initiateurvalidateorrefuse2  implements TaskListener {
@Autowired
    TaskService taskService;
    @Override
    public void notify(DelegateTask delegateTask) {

        System.out.println("initiateur2");
        String taskId = delegateTask.getId();

        Long initiateur2 = (Long) delegateTask.getVariable("initiateur2");
        System.out.println(initiateur2);
        String initiatorId = Long.toString(initiateur2);
        taskService.addUserIdentityLink(taskId, initiatorId, "starter");
    }
}
