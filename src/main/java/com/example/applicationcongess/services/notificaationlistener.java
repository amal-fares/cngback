package com.example.applicationcongess.services;


import com.example.applicationcongess.controller.Demande_congecontr;
import com.example.applicationcongess.models.Personnel;
import com.example.applicationcongess.repositories.PersonnelRepository;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.management.Notification;
import javax.management.NotificationListener;
import java.util.HashMap;
import java.util.Map;

@Service
public class notificaationlistener implements TaskListener, NotificationListener {
    @Autowired
    PersonnelRepository personnelRepository;
    @Autowired
    Demande_congecontr demande_congecontr;
    @Autowired
    RuntimeService runtimeService;
    private Long receiver ;
    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println("notif");
        Map<String, Object> variablesmanager = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        System.out.println(userDetails.getUsername());
        Personnel personnel = personnelRepository.findById(userDetails.getCin()).orElse(null);

        String proessInstanceID=demande_congecontr.getCurrentProcessInstanceId();
        runtimeService.setVariables(proessInstanceID, variablesmanager);
        String type = "NotificationType";
        Object source = delegateTask.getId();
        long sequenceNumber = 2;
        String message = "Notification de tâche : " + delegateTask.getName();
        Notification notification = new Notification(type, source, sequenceNumber,message);
receiver=personnel.getCin();
        handleNotification(notification, receiver);
    }

    @Override
    public void handleNotification(Notification notification, Object handback) {
        String notificationContent = notification.getMessage();



        String recipient = (String) handback;
        System.out.println("Notification envoyée à : " + recipient);
    }
}
