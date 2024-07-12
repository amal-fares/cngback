package com.example.applicationcongess.services.serviceexceptionnel;


import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class initiateurcollabwithjustif implements TaskListener {
    @Autowired
    TaskService taskService;
    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println("intiateurcollabwithjustif");
        String taskId = delegateTask.getId();
        Long initiator = (Long) delegateTask.getVariable("collabwithjustif");
        System.out.println(initiator);
        String initiatorId = Long.toString(initiator);
        taskService.addUserIdentityLink(taskId, initiatorId, "starter");
        System.out.println("fin");
    }

}
