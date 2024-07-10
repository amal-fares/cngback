package com.example.applicationcongess.services.serviceexceptionnel;


import com.example.applicationcongess.controller.Demande_congecontr;
import org.activiti.engine.RuntimeService;

import org.activiti.engine.delegate.DelegateTask;

import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class passthroughimpl implements TaskListener {
    @Autowired
    Demande_congecontr demande_congecontr;
    @Autowired
    RuntimeService runtimeService;


    @Override
    public void notify(DelegateTask delegateTask) {
        Boolean missingAttachemnt = ((Boolean) runtimeService.getVariable(demande_congecontr.getCurrentProcessInstanceId(), "missingAttachment"));
        Boolean decision = ((Boolean) runtimeService.getVariable(demande_congecontr.getCurrentProcessInstanceId(), "decision"));
        System.out.println(missingAttachemnt);
        System.out.println(decision);
if(!missingAttachemnt && decision || !missingAttachemnt && !decision){
    System.out.println("premeirecondit");
  runtimeService.setVariable(demande_congecontr.getCurrentProcessInstanceId(), "passthrough",true);

}
else if (!decision && missingAttachemnt) {
    System.out.println("deuxiemecond");
    runtimeService.setVariable(demande_congecontr.getCurrentProcessInstanceId(), "passthrough",false);

}
    }
}
