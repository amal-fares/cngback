package com.example.applicationcongess.services;

import com.example.applicationcongess.controller.Demande_congecontr;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class deadlinedutraitement implements ExecutionListener {
    @Autowired
    TaskService taskService;
    @Autowired
    Demande_congecontr demande_congecontr;
    public Date getTaskCreationDate( ) {
        String proessInstanceID=demande_congecontr.getCurrentProcessInstanceId();
        System.out.println(proessInstanceID);
        Task task = taskService.createTaskQuery()
                .processInstanceId(proessInstanceID)
                .taskName("Remplir les champs de forumlaire de demande de conges")
                .singleResult();

        if (task != null) {
            // Récupérer la date de création de la tâche
            Date createDate = task.getCreateTime();
            System.out.println("Date de création de la tâche '"  + "' : " + createDate);
            return createDate;
        } else {
            System.out.println("La tâche '" + "' n'existe pas pour le processus avec l'ID '"  + "'.");
            return null;
        }
    }
    public Date getTaskCompletionDate() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        HistoricTaskInstanceQuery historicTaskQuery = processEngine.getHistoryService().createHistoricTaskInstanceQuery()
                //je veux compter la date de completion de tache de traitement de la demande
                .taskName("Remplir les champs de forumlaire de demande de conges")
                .orderByHistoricTaskInstanceEndTime().desc();

        HistoricTaskInstance historicTaskInstance = historicTaskQuery.singleResult();

        if (historicTaskInstance != null) {
            // date de end de tahe
            Date completionDate = historicTaskInstance.getEndTime();
            System.out.println("Date de complétion de la tâche  : " + completionDate);
            return completionDate;
        } else {
            System.out.println("La tâche '"  + "' n'existe pas ou n'a pas encore été complétée.");
            return null;
        }
    }
    public Date taskcompletiondate;
    @Autowired
    checkdonneesdeform checkdonneesdeform;
    @Override
    public void notify(DelegateExecution delegateExecution) throws Exception {
        System.out.println("deadlinelimite");
        if (checkdonneesdeform.resultat.equals("vous avez le droit de conges et les jours sont valides")) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(getTaskCreationDate());
            System.out.print(getTaskCreationDate());
            // j ajoute 5 jours a la date de reation de task
            int workingDaysToAdd = 5;
            while (workingDaysToAdd > 0) {
                calendar.add(Calendar.DAY_OF_WEEK, 1);

                // je verifie si il sagit d un jours ouvrable ou non
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                if (dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.FRIDAY) {
                    workingDaysToAdd--;
                }
            }

            // Récupérer la date calculée
            System.out.println(calendar.getTime());
            //si la tache est éxécute avant cette date alors je suis dans la limite sinon rallonger la date et l utilisateur fait une relance
            if (calendar.getTime().after(getTaskCompletionDate())) {
                System.out.println("La tache est executée dans les bons delais");
            } else {
                System.out.println("la tache n'est pas éxécute dans les bons delais ");
            }
            taskcompletiondate=getTaskCompletionDate();

        }
        else {
            System.out.println("rien");
        }
    }

}
