package com.example.applicationcongess.services;

import com.example.applicationcongess.controller.Demande_congecontr;
import com.example.applicationcongess.enums.Statut_conge;
import com.example.applicationcongess.models.Demande_conge;
import com.example.applicationcongess.repositories.Demande_congebRepository;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class traiterlademande2 implements TaskListener {
    @Autowired
    Demande_congecontr demande_congecontr;
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    Demande_congebRepository demande_congebRepository;
    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println("traiterdemande2");
        String proessInstanceID=demande_congecontr.getCurrentProcessInstanceId();

        Long idDemandeConge = (Long) runtimeService.getVariable(proessInstanceID, "idDemandeConge");
        System.out.println(idDemandeConge);
        Demande_conge demande_conge =demande_congebRepository.findById(idDemandeConge).orElse(null);
        int nombreutilisateurchevauchant=demande_congebRepository.planningequipe(demande_conge.getDate_fin(),demande_conge.getDate_debut(),demande_conge.getCollaborateur().getCin());
        if (nombreutilisateurchevauchant>3){
            System.out.println("impossible de prendre le congé , les periodes de conges de collab se chevauchent2");
            demande_conge.setStatut_conge(Statut_conge.rejette);
            demande_congebRepository.save(demande_conge);
        }else
        {
            System.out.println("vous pouvez prendre des conges2");
            demande_conge.setStatut_conge(Statut_conge.valide2);
            demande_congebRepository.save(demande_conge);
        }
    }
    }

