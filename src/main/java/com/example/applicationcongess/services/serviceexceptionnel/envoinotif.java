package com.example.applicationcongess.services.serviceexceptionnel;

import com.example.applicationcongess.controller.Chatcontroller;
import com.example.applicationcongess.controller.Demande_congecontr;
import com.example.applicationcongess.enums.Statut_conge;
import com.example.applicationcongess.models.Demande_conge;
import com.example.applicationcongess.repositories.Demande_congebRepository;
import com.example.applicationcongess.services.CloudinaryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class envoinotif implements TaskListener {
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    Demande_congecontr demande_congecontr;
    @Autowired
    CloudinaryService cloudinaryService;
    @Autowired
    Demande_congebRepository demande_congebRepository;
    @Autowired
    Chatcontroller chatcontroller;
    @Override
    public void notify(DelegateTask delegateTask) {

        long  iduser = (long ) runtimeService.getVariable(demande_congecontr.getCurrentProcessInstanceId(), "initiator");
        String str = Long.toString(iduser);
        chatcontroller.ajoutjustif("Veuiller joindre vos justifs dans les plus bref delais", str);

        runtimeService.setVariable(demande_congecontr.getCurrentProcessInstanceId(), "hasReminded", true);


        }

}
