package com.example.applicationcongess.services.serviceexceptionnel;

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

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;
@Service
public class verificationDesJustificatifsTaille implements TaskListener {
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    Demande_congecontr demande_congecontr;
    @Autowired
    CloudinaryService cloudinaryService;
    @Autowired
    Demande_congebRepository demande_congebRepository;
    @Override
    public void notify(DelegateTask delegateTask) {

        String  imageid = (String ) runtimeService.getVariable(demande_congecontr.getCurrentProcessInstanceId(), "imagepublicnewcid");
        Map metadata =cloudinaryService.getImageMetadata(imageid);
        int imageSizeInBytes = (int) metadata.get("bytes");
        Long iddemandeconge = ((Long) runtimeService.getVariable(demande_congecontr.getCurrentProcessInstanceId(), "id_demande_conge"));
        Demande_conge demande_conge = demande_congebRepository.findById(iddemandeconge).orElse(null);
       runtimeService.setVariable(demande_congecontr.getCurrentProcessInstanceId(), "missingAttachment",false );
demande_conge.setJustificatifPresent(true);
demande_congebRepository.save(demande_conge);
        int maxSizeInBytes= 2000000;
        if (  imageSizeInBytes <= maxSizeInBytes){
            System.out.println("taille de l image dans les normes ");



        }else {
            System.out.println("taille de l image pas dans les normes ");

        }
    }
}
