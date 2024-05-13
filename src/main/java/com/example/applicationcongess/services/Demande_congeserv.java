package com.example.applicationcongess.services;


import com.example.applicationcongess.enums.Type_conge;
import com.example.applicationcongess.models.Demande_conge;
import com.example.applicationcongess.models.Personnel;
import com.example.applicationcongess.repositories.Demande_congebRepository;
import com.example.applicationcongess.repositories.PersonnelRepository;
import lombok.AllArgsConstructor;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Service
@AllArgsConstructor
public class Demande_congeserv implements IDemande_conge {

    @Autowired
    Demande_congebRepository demande_congebRepository;
    @Autowired
    PersonnelRepository personnelRepository;
    @Autowired
    TaskService taskService;

    /*public Demande_conge envodemandecongeexeptionnel(Date date_deb, Date date_fin, Type_conge type_conge, Long id_personnel) {
        Demande_conge demande_conge = new Demande_conge();
        Personnel personnel = personnelRepository.findById(id_personnel).orElse(null);
        demande_conge.setDate_debut(date_deb);
        demande_conge.setDate_fin(date_fin);
        demande_conge.setType_conge(type_conge);
        if (demande_conge.getType_conge().equals(Type_conge.exceptionnel)) {
            if (type_conge == null || type_conge()) {
                throw new IllegalArgumentException("Le motif est obligatoire pour les congés exceptionnels.");
            }
            demande_conge.setType_conge(motif);

        }
        System.out.println("here");
        Instant instantDebut = date_deb.toInstant();
        Instant instantFin = date_fin.toInstant();
        LocalDate localDate = LocalDate.now();
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        demande_conge.setDate_demande_congés(date);
        Duration dureeTotale = Duration.between(instantDebut, instantFin);
        float nombrejourspris = dureeTotale.toDays();
        demande_conge.setDuree(nombrejourspris);
        if (nombrejourspris < personnel.getJours_restants()) {
            Demande_conge demcongeenvoye = demande_congebRepository.save(demande_conge);
        }
      else {
            throw new IllegalArgumentException("il ne vous reste plus de jours à déposer ");
        }
        return demande_conge;
    }
*/
    public String deletedemandeconge(Long id_demande_conge) {
        demande_congebRepository.deleteById(id_demande_conge);
        String chaine = "Supprimé avec succés ";
        return chaine;
    }

    public Demande_conge updatedemande_conge(Long id_demande_conge, Date date_db, Date date_fn) {
        Demande_conge demande_conge = demande_congebRepository.findById(id_demande_conge).orElse(null);
        demande_conge.setDate_fin(date_fn);
        demande_conge.setDate_debut(date_db);
        return demande_congebRepository.save(demande_conge);
    }
   }


