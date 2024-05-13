package com.example.applicationcongess.services;


import com.example.applicationcongess.models.Personnel;
import com.example.applicationcongess.repositories.PersonnelRepository;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class initiateurTaskListener implements TaskListener {
    @Autowired
    TaskService taskService;
    @Autowired
    PersonnelRepository personnelRepository;
    @Override
    public void notify(DelegateTask delegateTask) {

        System.out.println("inititor");
        String taskId = delegateTask.getId();
        Long initiator = (Long) delegateTask.getVariable("initiator");
        System.out.println(initiator);
        String initiatorId = Long.toString(initiator);
        taskService.addUserIdentityLink(taskId, initiatorId, "starter");
 System.out.println("fin");
    }
}
