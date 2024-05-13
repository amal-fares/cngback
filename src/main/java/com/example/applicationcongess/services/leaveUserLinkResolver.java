package com.example.applicationcongess.services;

import com.example.applicationcongess.models.Demande_conge;
import com.example.applicationcongess.models.Personnel;
import com.example.applicationcongess.repositories.Demande_congebRepository;
import com.example.applicationcongess.repositories.PersonnelRepository;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class leaveUserLinkResolver implements TaskListener {
    @Autowired
    PersonnelRepository personnelRepository;
    @Autowired
    Demande_congebRepository demande_congebRepository;
    @Override
    public void notify(DelegateTask delegateTask) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        System.out.println(userDetails.getUsername());
        Personnel personnel = personnelRepository.findById(userDetails.getCin()).orElse(null);
        Personnel manager = personnel.getManager();
        Personnel managerInfos = personnelRepository.findById(manager.getCin()).orElse(null);
        Demande_conge demanddecongeduser=demande_congebRepository.findByCollaborateur_Cin(userDetails.getCin());


    }
}
