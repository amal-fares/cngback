package com.example.applicationcongess.controller;

import com.example.applicationcongess.PlayLoad.Response.GestionnaireDTO;
import com.example.applicationcongess.PlayLoad.request.*;
import com.example.applicationcongess.enums.*;
import com.example.applicationcongess.models.*;
//import com.example.applicationcongess.services.ActivitiConfig;
import com.example.applicationcongess.repositories.*;
import com.example.applicationcongess.services.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
//import org.activiti.engine.RuntimeService;
//import org.activiti.engine.runtime.ProcessInstance;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.w3c.dom.NodeList;
import springfox.documentation.annotations.ApiIgnore;

import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.plaf.synth.SynthTextAreaUI;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/demandeconge")

public class Demande_congecontr {
    final Demande_congeserv demande_congeserv;
    final Personnelserv personnelserv;
    @Autowired
    PersonnelRepository personnelRepository;
    @Autowired
    deadlinedutraitement deadlinedutraitement;

    @Autowired
    RuntimeService runtimeService;
    @Autowired
    TaskService taskService;
    private String currentProcessInstanceId;
    @Autowired
    CloudinaryService cloudinaryService;
    @Autowired
    Imageserv imageserv;
    @Autowired
    Chatcontroller chatcontroller;
    @Autowired
    private Demande_congebRepository demande_congebRepository;
    @Autowired
    private ObjectMapper objectMapper;


    /*  @PostMapping("/envoidemande/{date_deb}/{date_fin}/{type_conge}/{id_personnel}")
      public Demande_conge envoidemande(@PathVariable("date_deb") @DateTimeFormat(pattern="yyyy-MM-dd") Date  date_deb, @PathVariable("date_fin") @DateTimeFormat(pattern="yyyy-MM-dd") Date date_fin, @PathVariable("type_conge") Type_conge type_conge, @RequestParam(value = "motif", required = false) String motif, @PathVariable("id_personnel") Long id_personnel){
          if (Type_conge.exceptionnel.equals(type_conge) && (motif == null || motif.isEmpty())) {
              throw new IllegalArgumentException("Le motif est obligatoire pour les congés exceptionnels.");
          }
          return demande_congeserv.envodemandecongeexeptionnel(date_deb,date_fin,type_conge,motif,id_personnel);
      }*/
    @PostMapping("/addpersonnel")
    public Personnel addpersonnel(@RequestBody Personnel personnel) {
        return personnelserv.addpersoneel(personnel);
    }

    @PostMapping("updatedem_cng/{id_demande_conge}/{date_db}/{date_fn}")
    public Demande_conge updatedmcng(@PathVariable("id_demande_conge") Long id_demande_conge, @PathVariable("date_db") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date_db, @PathVariable("date_fn") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date_fn) {
        return demande_congeserv.updatedemande_conge(id_demande_conge, date_db, date_fn);
    }

    @DeleteMapping("deletedem/{id_demande_conge}")
    public String deletedem(@PathVariable("id_demande_conge") Long id_demande_conge) {
        return demande_congeserv.deletedemandeconge(id_demande_conge);
    }

    @PostMapping("/changepassword ")
    public ResponseEntity<String> changePassword(ChangePasswordRequest request) {
        return personnelserv.changePassword(request);
    }
    // @Autowired
    //private RuntimeService runtimeService;

    //@GetMapping("/startProcess")
    //  public String startProcess() {
    //    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("leave");
    //  return "Process started with id: " + processInstance.getId();
    //}


    @GetMapping("/get-tasks/{processInstanceId}")
    public List<TaskRepresentation> getTasks(@PathVariable("processInstanceId") String processInstanceId) {
        List<Task> usertasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .list();

        return usertasks.stream()
                .map(task -> new TaskRepresentation(task.getId(), task.getName(), task.getProcessInstanceId()))
                .collect(Collectors.toList());
    }

    @PostMapping("remplirchampvalidator2/{commentaire2}/{decision2}/{iddemand2}")
    public Map<String, Object> remplirFormulaireActivitiusertask2(@PathVariable("commentaire2") String commentaire, @PathVariable("decision2") boolean decision, @PathVariable("iddemand2") long iddeamnd) {
        Map<String, Object> variablesvalidator = new HashMap<>();
        try {
            System.out.println(currentProcessInstanceId);
            String validator_commentt = commentaire;
            variablesvalidator.put("decision2", decision);
            Demande_conge demande_conge = demande_congebRepository.findById(iddeamnd).orElse(null);
            Personnel personnesoumisdemande = personnelRepository.findById(demande_conge.getCollaborateur().getCin()).orElse(null);
            if (personnesoumisdemande.getRoles().stream().anyMatch(role -> role.getName().equals(ERole.Role_collaborateur))) {
                Personnel managerresponsable2 = personnelRepository.findById(personnesoumisdemande.getManagerdeuxiemeniveau().getCin()).orElse(null);
                variablesvalidator.put("validatorname2", managerresponsable2.getUsername());
                Long cin = managerresponsable2.getCin();
                variablesvalidator.put("initiateur2", cin);
                String signalName = "Notif Signal";
                String activityId = "catchdeux";
                Execution waitingExecution = runtimeService.createExecutionQuery()
                        .processInstanceId(currentProcessInstanceId)
                        .activityId(activityId)
                        .signalEventSubscriptionName(signalName)
                        .singleResult();
                System.out.println(waitingExecution);
                runtimeService.setVariables(currentProcessInstanceId, variablesvalidator);
                runtimeService.signalEventReceived(signalName, waitingExecution.getId());


            }
            if (personnesoumisdemande.getRoles().stream().anyMatch(role -> role.getName().equals(ERole.Role_manager))) {
                System.out.println("j ai atteint la partie manager");
                Personnel managerresponsable2 = personnelRepository.findById(personnesoumisdemande.getManagerdeuxiemeniveau().getCin()).orElse(null);
                variablesvalidator.put("validatorname2", managerresponsable2.getUsername());
                Long cin = managerresponsable2.getCin();
                variablesvalidator.put("initiateur2", cin);
                String signalName = "Notif Signal";
                String activityId = "catchdeux";
                Execution waitingExecution = runtimeService.createExecutionQuery()
                        .processInstanceId(currentProcessInstanceId)
                        .activityId(activityId)
                        .signalEventSubscriptionName(signalName)
                        .singleResult();
                System.out.println(waitingExecution);
                runtimeService.setVariables(currentProcessInstanceId, variablesvalidator);
                runtimeService.signalEventReceived(signalName, waitingExecution.getId());


            }
            if (personnesoumisdemande.getRoles().stream().anyMatch(role -> role.getName().equals(ERole.Role_manager2))) {
                Long cinmanager = personnesoumisdemande.getCin();
                variablesvalidator.put("initiateur2", cinmanager);
                String signalName = "Notif Reguliermanager";
                String activityId = "reguliermanagerdeux";
                Execution waitingExecution = runtimeService.createExecutionQuery()
                        .processInstanceId(currentProcessInstanceId)
                        .activityId(activityId)
                        .signalEventSubscriptionName(signalName)
                        .singleResult();
                System.out.println(waitingExecution);
                runtimeService.setVariables(currentProcessInstanceId, variablesvalidator);
                runtimeService.signalEventReceived("Notif Reguliermanager", waitingExecution.getId());
            }

            Task usertask2 = taskService.createTaskQuery()
                    .processInstanceId(currentProcessInstanceId).taskName("mangerdeuxrefuseor accept decision")
                    .singleResult();
            ;
            System.out.println(usertask2);

            System.out.println(usertask2.getId());
            taskService.complete(usertask2.getId());
        } catch (Exception e) {
            System.out.println("Erreur lors de remplissage de Mangager2 : " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println(variablesvalidator);
        return variablesvalidator;
    }

    @GetMapping("process/{processInstanceId}/{activityId}/{signalName}")
    public boolean isProcessWaitingForSignal(@PathVariable("processInstanceId") String processInstanceId, @PathVariable("activityId") String activityId, @PathVariable("signalName") String signalName) {
        Execution waitingExecution = runtimeService.createExecutionQuery()
                .processInstanceId(processInstanceId)
                .activityId(activityId)
                .signalEventSubscriptionName(signalName)
                .singleResult();

        return waitingExecution != null;
    }

    @PostMapping("remplirchampvalidator/{commentaire}/{decision}/{iddemand}")
    public Map<String, Object> remplirFormulaireActivitiusertask(@PathVariable("commentaire") String commentaire, @PathVariable("decision") boolean decision, @PathVariable("iddemand") Long iddeamnd) {

        Map<String, Object> variablesvalidator = new HashMap<>();
        try {
            System.out.println(currentProcessInstanceId);

            variablesvalidator.put("decision", decision);

            Demande_conge demande_conge = demande_congebRepository.findById(iddeamnd).orElse(null);

            Personnel personnesoumisdenade = personnelRepository.findById(demande_conge.getCollaborateur().getCin()).orElse(null);
            if (personnesoumisdenade.getRoles().stream().anyMatch(role -> role.getName().equals(ERole.Role_collaborateur))) {
                Personnel managerresponsable = personnelRepository.findById(personnesoumisdenade.getManager().getCin()).orElse(null);
                variablesvalidator.put("validatorname", managerresponsable.getUsername());
                Long cin = managerresponsable.getCin();
                variablesvalidator.put("initiateur", cin);
            }
            if (personnesoumisdenade.getRoles().stream().anyMatch(role -> role.getName().equals(ERole.Role_manager))) {
                Long cin = personnesoumisdenade.getCin();
                variablesvalidator.put("validatorname", personnesoumisdenade.getUsername());
                variablesvalidator.put("initiateur", cin);
            }
            LocalDate today = LocalDate.now();
            Date date = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());


            demande_conge.setDatedecision(date);


            String signalName = "Notification Signal";
            String activityId = "waitForCondition";
            Execution waitingExecution = runtimeService.createExecutionQuery()
                    .processInstanceId(currentProcessInstanceId)
                    .activityId(activityId)
                    .signalEventSubscriptionName(signalName)
                    .singleResult();
            System.out.println(waitingExecution);
            runtimeService.setVariables(currentProcessInstanceId, variablesvalidator);
            runtimeService.signalEventReceived("Notification Signal", waitingExecution.getId());
            Task usertask2 = taskService.createTaskQuery()
                    .processInstanceId(currentProcessInstanceId).taskName("Manger decision")
                    .singleResult();
            ;

            System.out.println(usertask2.getId());
            taskService.complete(usertask2.getId());
        } catch (Exception e) {
            System.out.println("Erreur lors de remplissage de usertask : " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println(variablesvalidator);
        return variablesvalidator;
    }

    @Autowired
    checkdonneesdeform checkdonneesdeform;

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("remplirchamp/{iduserconnete}")
    public Demande_conge remplirFormulaireActiviti(@RequestBody Demand_congerequest demande_conge, @PathVariable("iduserconnete") Long iduser) {
        Map<String, Object> variables = new HashMap<>();
        Demande_conge demandeconge = new Demande_conge();
        try {

            Date start = demande_conge.getDate_debut();
            Boolean pausevariable = Boolean.FALSE;
            Boolean deuxiemevalidation = Boolean.FALSE;
            Boolean mail = Boolean.FALSE;
            variables.put("variablepause", pausevariable);
            variables.put("mail", mail);
            variables.put("deuxiemevalidation", deuxiemevalidation);
            Date end = demande_conge.getDate_fin();
            Type_conge type_conge = demande_conge.getTypeconge();
            Type_conge_exceptionnel type_conge_exceptionnel = demande_conge.getType_conge_exceptionnel();
            variables.put("start", start);
            variables.put("end", end);
            variables.put("motif", type_conge);
            variables.put("type_congeexceptionnel", type_conge_exceptionnel);

            variables.put("personnesoumisdemande", iduser);
            Personnel personnel = personnelRepository.findById(iduser).orElse(null);
            for (Role role : personnel.getRoles()) {
                System.out.println(role.getName());
                if (ERole.valueOf("Role_manager2").equals(role.getName())) {
                    System.out.println("Je suis manager2");
                    variables.put("rolesoumetter", "Role_manager2");
                }
                if (ERole.valueOf("Role_manager").equals(role.getName())) {
                    System.out.println("Je suis manager");
                    variables.put("rolesoumetter", "Role_manager");
                }
                if (ERole.valueOf("Role_collaborateur").equals(role.getName())) {
                    System.out.println("Je suis collaborateur");
                    variables.put("rolesoumetter", "Role_collaborateur");
                }
                if (ERole.valueOf("Role_gestionnaire").equals(role.getName())) {
                    System.out.println("Je suis gestionnaire");
                    variables.put("rolesoumetter", "Role_gestionnaire");
                }
            }


            System.out.println(variables);
            System.out.println("variables");
            List<Type_conge_exceptionnel> elementsrequierjustif = Arrays.asList(Type_conge_exceptionnel.Conge_maternite, Type_conge_exceptionnel.Conge_de_Demangement, Type_conge_exceptionnel.Conge_demariage, Type_conge_exceptionnel.Conge_paternite, Type_conge_exceptionnel.Conge_Enfant_malade, Type_conge_exceptionnel.Conge_en_cas_de_décès,
                    Type_conge_exceptionnel.Conge_pour_service_militaire_ou_civil);
            LocalDate today = LocalDate.now();
            Date date = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());

            demandeconge = new Demande_conge(start, end, type_conge, type_conge_exceptionnel, personnel, demandeconge.getJustificatifs_requis(), date);

            if (elementsrequierjustif.contains(demande_conge.getType_conge_exceptionnel())) {
                demandeconge.setJustificatifs_requis(true);
                demandeconge.setStatutconge(Statut_conge.enattentedejustificatifs);
                demandeconge.setJustificatifPresent(false);
                demande_congebRepository.save(demandeconge);
                variables.put("id_demande_conge", demandeconge.getId_demandeconge());
            } else {
                demandeconge.setJustificatifs_requis(false);
                demandeconge.setStatutconge(Statut_conge.Enattente_de_validation);
                demande_congebRepository.save(demandeconge);
                variables.put("id_demande_conge", demandeconge.getId_demandeconge());
            }
            if (demandeconge.getTypeconge().equals(Type_conge.regulier)) {
                demandeconge.setStatutconge(Statut_conge.Enattente_de_validation);
                demande_congebRepository.save(demandeconge);
                variables.put("id_demande_conge", demandeconge.getId_demandeconge());
            }
            if(demandeconge.getTypeconge().equals(Type_conge.regulier)&&personnel.isConfirmsoldeprev()){
                demandeconge.setTypecongeprev(Typecongeprev.previsionnel);
                demande_congebRepository.save(demandeconge);
            }else if(demandeconge.getTypeconge().equals(Type_conge.regulier)&& personnel.getSolde_conges() > 0 ){
                demandeconge.setTypecongeprev(Typecongeprev.normal);
                demande_congebRepository.save(demandeconge);
            }

            variables.put("initiator", personnel.getCin());
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process");
            System.out.println("c est ici null ");
            System.out.println(processInstance.getProcessInstanceId());
            currentProcessInstanceId = processInstance.getProcessInstanceId();

            Task usertasks = taskService.createTaskQuery()
                    .processInstanceId(processInstance.getProcessInstanceId()).taskName("Remplir les champs de forumlaire de demande de conges")
                    .singleResult();

            System.out.println(usertasks);
            System.out.println(usertasks.getId());
            taskService.setVariables(usertasks.getId(), variables);
            taskService.complete(usertasks.getId());

            System.out.println(checkdonneesdeform.resultat);
            System.out.println("resultat");
            if (checkdonneesdeform.resultat.equals("vous avez le droit de conges et les jours sont valides")) {
                demande_congebRepository.save(demandeconge);


            }

        } catch (Exception e) {
            System.out.println("Erreur lors de l'analyse de la  : " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println(variables);
        return demandeconge;
    }
    /*@Transactional
    @GetMapping("/start-process")
    public String startProcess() {

        try {
            Map<String, Object> variables = remplirFormulaireActiviti();
            System.out.println(variables);
            Map<String, Object> variablesvalidator = remplirFormulaireActivitiusertask();
            Map<String, Object> variablesvalidator2 = remplirFormulaireActivitiusertask2();
            Long initiator = (Long) variables.get("initiator");
            System.out.println(initiator);
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process");
            Task usertasks = taskService.createTaskQuery()
                    .processInstanceId(processInstance.getProcessInstanceId()).taskName("Remplir les champs de forumlaire de demande de conges")
                    .singleResult();
            System.out.println(usertasks);


            taskService.setVariables(usertasks.getId(), variables);
            runtimeService.setVariables(processInstance.getProcessInstanceId(), variablesvalidator);
            runtimeService.setVariables(processInstance.getProcessInstanceId(), variablesvalidator2);


            System.out.println(processInstance.getProcessInstanceId());
            currentProcessInstanceId = processInstance.getProcessInstanceId();

            taskService.complete(usertasks.getId());
            Task usertask2 = taskService.createTaskQuery()
                    .processInstanceId(processInstance.getProcessInstanceId()).taskName("Manger validateorrefuse")
                    .singleResult();
            ;
            System.out.println(usertask2);
            taskService.complete(usertask2.getId());
            Task usertask3 = taskService.createTaskQuery()
                    .processInstanceId(processInstance.getProcessInstanceId()).taskName("mangerdeuxrefuseor accept")
                    .singleResult();
            ;
            System.out.println(usertask3);
            taskService.complete(usertask3.getId());
            return currentProcessInstanceId;
        } catch (Exception ex) {
            System.out.println("Erreur  : " + ex.getMessage());
            // Gérer l'exception ou la journaliser
            ex.printStackTrace();
            return null;
        }

    }

*/

    public String getCurrentProcessInstanceId() {
        return currentProcessInstanceId;
    }


    @GetMapping("planningequipe/{date_fin_utilisateur}/{date_debut_utilisateur}")
    public int planningequipe(@PathVariable("date_fin_utilisateur") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date_fin_utilisateur, @PathVariable("date_debut_utilisateur") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date_debut_utilisateur) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        System.out.println(userDetails.getUsername());
        Personnel personnel = personnelRepository.findById(userDetails.getCin()).orElse(null);
        return demande_congebRepository.planningequipe(date_fin_utilisateur, date_debut_utilisateur, personnel.getCin());
    }

    @PostMapping("uploadimage")
    public ResponseEntity<Map> uploadimage(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        Map Data = cloudinaryService.
                upload(multipartFile);
        return new ResponseEntity<>(Data, HttpStatus.OK);
    }

    @PostMapping("addandassignimage/{iddemande}/{chatroomid}")
    public Image_justificatif AddandAssig(@RequestParam("image") MultipartFile image, @PathVariable("iddemande") long iddemande, @PathVariable(name = "chatroomid", required = false) long idchatroom) throws IOException {
        return imageserv.AddandAssig(image, iddemande, idchatroom);
    }

    @PostMapping("addandassignwithoutchatroom/{iddemande}")
    public Image_justificatif AddandAssign(@RequestParam("image") MultipartFile image, @PathVariable("iddemande") long iddemande) throws IOException {
        return imageserv.AddandAssig(image, iddemande);
    }

    @GetMapping("getdemandescongesuser/{iduser}")
    public List<Demande_conge> getdemandecongesdeuser(@PathVariable("iduser") Long iduser) {
        Personnel personnel = personnelRepository.findById(iduser).orElse(null);
        return demande_congebRepository.getdemandecongesdeuser(iduser);
    }

    @GetMapping("getalldem")
    public List<Demande_conge> getalldemand() {
        return demande_congebRepository.findAll();
    }

    @GetMapping("gettaskcompletion")
    public Date taskcompletion() {
        return deadlinedutraitement.getTaskCompletionDate();
    }

    @PutMapping("assign/{iddemand}/{gestionnaireid}")
    public Demande_conge assignDemande(@PathVariable("iddemand") Long demandeId, @PathVariable("gestionnaireid") Long gestionnaireId) {
        Demande_conge demande = demande_congebRepository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));
        Personnel gestionnaire = personnelRepository.findById(gestionnaireId).orElse(null);
        demande.setCollaborateur(gestionnaire);
        return demande_congebRepository.save(demande);
    }

    private Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }

    private List<Date> getDatesBetween(Date startDate, Date endDate) {
        List<Date> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        while (!calendar.getTime().after(endDate)) {
            dates.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return dates;
    }

    Set<Long> demandesTraitees = new HashSet<>();

    @GetMapping("/soldeconges/{iduser}")
    public Map<String, Float> soldeConges(@PathVariable("iduser") Long iduser) {
        Personnel personnel = personnelRepository.findById(iduser).orElse(null);

        if (personnel == null) {
            // Gérer le cas où l'utilisateur est introuvable
            Map<String, Float> result = new HashMap<>();
            result.put("soldePrevisionnel", 0f);
            result.put("soldeConges", 0f);
            return result;
        }

        float soldeConge = personnel.getSolde_conges();
        float soldePrevisionnel = personnel.getSoldeprevisonnel();
        LocalDate now = LocalDate.now();

        // Si c'est le 1er janvier, réinitialiser le solde de congés à 22 jours
        if (now.getMonth() == Month.JANUARY && now.getDayOfMonth() == 1) {
            personnel.setSolde_conges(22);

        }

        // Récupérer toutes les demandes de congé validées pour cet utilisateur
        List<Demande_conge> demandesConges = demande_congebRepository.findDemande_congeByCollaborateurAndStatutcongeAndTypeconge(personnel, Statut_conge.valide2,Type_conge.regulier);

        for (Demande_conge demande : demandesConges) {
            Long demandeId = demande.getId_demandeconge(); // Identifier unique de la demande

            // Vérifier si cette demande a déjà été traitée
            if (demandesTraitees.contains(demandeId)) {
                continue; // Passer à la demande suivante si elle a déjà été traitée
            }

            Date startDate = demande.getDate_debut();
            Date endDate = demande.getDate_fin();

            // Vérifier que les dates sont valides
            if (startDate == null || endDate == null) {
                continue; // Passer à la demande suivante si les dates sont nulles
            }

            // Calculer la durée en jours de la demande de congé
            long differenceInMillis = endDate.getTime() - startDate.getTime();
            long differenceInDays = differenceInMillis / (1000 * 60 * 60 * 24) + 1; // +1 pour inclure le dernier jour
            if (demande.getTypeconge() == Type_conge.exceptionnel) {
                continue;
            }

            if ( soldeConge > 0 && demande.getTypeconge().equals(Type_conge.regulier)) {
                soldeConge -= differenceInDays;
            } else if ( demande.getTypeconge().equals(Type_conge.regulier)&&demande.getTypecongeprev().equals(Typecongeprev.previsionnel)) {
                soldePrevisionnel -= differenceInDays;
                if( soldePrevisionnel <0){
                    soldePrevisionnel = 0;
                }
                personnel.setSoldeprevisonnel((long) soldePrevisionnel);


            }

            // Si le solde de congés devient négatif, le mettre à zéro
            if (soldeConge < 0 || soldePrevisionnel <0 ) {
                soldeConge = 0;
            }

            // Ajouter cette demande à l'ensemble des demandes traitées
            demandesTraitees.add(demandeId);
        }

        // Mettre à jour le solde de congés dans l'objet personnel une seule fois
        personnel.setSolde_conges(soldeConge);
        personnelRepository.save(personnel);

        // Retourner un Map avec les deux soldes
        Map<String, Float> result = new HashMap<>();
        result.put("soldePrevisionnel", soldePrevisionnel);
        result.put("soldeConges", soldeConge);
        return result;
    }


    @GetMapping("getdemandevalidesprem")
    public List<Demande_conge> getalldemandvalideprem() {
        List<Demande_conge> listdemandconges = getalldemand();
        return listdemandconges.stream()
                .filter(demande -> demande.getStatutconge().equals(Statut_conge.valide1))
                .collect(Collectors.toList());
    }

    @GetMapping("getgestionnaire/{erole}")
    public Personnel getgestionnaire(@PathVariable ERole erole) {
        return personnelRepository.findByRoles(erole);
    }

    @Autowired
    ChatService chatService;

    @GetMapping("getemployes/{idsender}/{idreceiver}/{iddemande}")
    public Chatroom getchatrrom(@PathVariable("idsender") long idsender, @PathVariable("idreceiver") long idreceiver, @PathVariable("iddemande") long iddemande) {
        return chatService.findchat(idsender, idreceiver, iddemande);
    }

    @GetMapping("getgestionnaireuser/{username}")
    public GestionnaireDTO getUserDTOByUsername(@PathVariable("username") String username) {
        Personnel user = personnelRepository.findByUsername(username).orElse(null);
        // Chargez les informations du gestionnaire

        GestionnaireDTO userDTO = new GestionnaireDTO();
        userDTO.setCin(user.getCin());

        return userDTO;
    }

    @Autowired
    ChatMessagerepo chatMessagerepo;

    /*  @GetMapping("getmessagebyreceiver/{idchattroom}/{idreceiver}")
      public List<ChatMessage> getmsgbyreceiver(@PathVariable("icchatroom") long idchatroom,@PathVariable("idreceiver")long idreceiver ){
         List<Chatroom> chatrrom= chatMessagerepo.findChatMessageByChat(idchatroom);
          List<ChatMessage> myList = new ArrayList<>();
           for( Chatroom chatroom:chatrrom){

               if(chatroom.getReceiver().getCin().equals(idreceiver)){
       myList.add()
               }
           }
      }*/
    @GetMapping("getdembyid/{iddem}")
    public Demande_conge getdembyid(@PathVariable("iddem") long iddem) {
        return demande_congebRepository.findById(iddem).orElse(null);
    }

    @Autowired
    ChatRoomrepo chatRoomrepo;

    @GetMapping("getmessagebyhatroom/{idchatroom}")
    public List<ChatMessage> getmessagebychatrrom(@PathVariable("idchatroom") long idchatroom) {
        Chatroom chatrrom = chatRoomrepo.findById(idchatroom).orElse(null);
        return chatMessagerepo.findChatMessageByChat(chatrrom);
    }

    @GetMapping("getchtaroombyidch/{chatroomid}")
    public Chatroom getchatroombyyidch(@PathVariable("chatroomid") long idchatroom) {
        return chatRoomrepo.getChatroomByChatroomId(idchatroom);
    }

    @PutMapping("/putsetatdemande/{iddemande}")
    public Demande_conge putetatdemande(@PathVariable("iddemande") long iddemande) {
        Demande_conge demandeconge = demande_congebRepository.findById(iddemande).orElse(null);
        demandeconge.setStatutconge(Statut_conge.Enattente_de_validation);
        return demande_congebRepository.save(demandeconge);
    }

    @GetMapping("/deadlinedemande/{iddemande}")
    public Date deadlinedeamnde(@PathVariable("iddemande") long iddemande) {
        Demande_conge demande_conge = demande_congebRepository.findById(iddemande).orElse(null);
        Date datedebut = demande_conge.getDate_debut();


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(datedebut);
        calendar.add(Calendar.DAY_OF_MONTH, -2);


        Date deadline = calendar.getTime();
        demande_conge.setDeadline(deadline);
        demande_congebRepository.save(demande_conge);
        return deadline;
    }

    @GetMapping("/enretard")
    public int demandeenretard() {
        int demande = 0;
        List<Demande_conge> listdemandecongeexistantes = demande_congebRepository.findAll();
        List<Demande_conge> listdemand_retard = new ArrayList<>();
        LocalDateTime maintenant = LocalDateTime.now();
        for (Demande_conge demande_conge : listdemandecongeexistantes) {
            LocalDateTime dateLimite = demande_conge.getDeadline().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            if (maintenant.isAfter(dateLimite)) {
                if (demande_conge.getStatutconge().equals(Statut_conge.Enattente_de_validation)) {
                    listdemand_retard.add(demande_conge);
                    demande++;
                }

            }

        }
        return demande;
    }

    @GetMapping("notif")
    public String notif() {
        String Chaine = "";
        return Chaine;
    }

    @GetMapping("/troisprochainsjours")
    public int getDemandesDansTroisProchainsJours() {
        int nombre = 0;
        LocalDate aujourdHui = LocalDate.now();
        LocalDate dateDansTroisJours = aujourdHui.plus(3, ChronoUnit.DAYS);
        Date aujourdhuiDate = java.sql.Date.valueOf(aujourdHui);
        Date dateDansTroisJoursDate = java.sql.Date.valueOf(dateDansTroisJours);

        // Afficher les dates conv
        // Récupérer les demandes de congé dont la date limite est dans les trois prochains jours
        for (Demande_conge demande_conge : demande_congebRepository.findByDeadlineBetween(aujourdhuiDate, dateDansTroisJoursDate)) {
            if (demande_conge.getTypeconge() == Type_conge.exceptionnel) {
                nombre++;
            }

        }
        return nombre;
    }

    @GetMapping("/typedemandeconge/{typeconge}")
    public int geetnombretypeconge(@PathVariable("typeconge") Type_conge type_conge) {
        return demande_congebRepository.countByTypeconge(type_conge);
    }

    @GetMapping("/typecongexcep/{typecongeexcep}")
    public int geetnombretypecongeexce(@PathVariable("typecongeexcep") Type_conge_exceptionnel type_congeexce) {
        return demande_congebRepository.countByTypecongeexceptionnel(type_congeexce);
    }

    @GetMapping("/gettypecongexeppresent")
    public Map<Type_conge_exceptionnel, Long> getTypesCongeAttribues() {
        List<Demande_conge> demandesConge = demande_congebRepository.findAll();

        return demandesConge.stream().filter(demande -> demande.getTypecongeexceptionnel() != null)
                .collect(Collectors.groupingBy(Demande_conge::getTypecongeexceptionnel, Collectors.counting()));
    }

    @GetMapping("listdemexep/{typeconge}")
    public List<Demande_conge> listedemande(@PathVariable("typeconge") Type_conge typeconge) {
        List<Demande_conge> demande_conges = new ArrayList<>();
        for (Demande_conge demande_conge : demande_congebRepository.getDemande_congeByTypeconge(typeconge)) {
            System.out.println(demande_conge);
            if (demande_conge.getStatutconge().equals(Statut_conge.Enattente_de_validation)  ||
                    demande_conge.getStatutconge().equals(Statut_conge.enattentedejustificatifs) ||
                    (demande_conge.getStatutconge().equals(Statut_conge.valide2) && demande_conge.getJustificatifPresent() ) ||
                    (demande_conge.getStatutconge().equals(Statut_conge.rejette) )
                    ) {
                demande_conges.add(demande_conge);
                System.out.println(demande_conge);
            }


        }
        return demande_conges;
    }

    @GetMapping("listdemanagerun/{typeconge}")
    public List<Demande_conge> listedemandemanagerun(@PathVariable("typeconge") Type_conge typeconge) {
        List<Demande_conge> demande_conges = new ArrayList<>();
        for (Demande_conge demande_conge : demande_congebRepository.getDemande_congeByTypeconge(typeconge)) {
            System.out.println(demande_conge);
            if (demande_conge.getStatutconge().equals(Statut_conge.Enattente_de_validation) && !demande_conge.getCollaborateur().getRoles().stream().anyMatch(role -> role.getName().equals(ERole.Role_manager2))) {
                demande_conges.add(demande_conge);
                System.out.println(demande_conge);
            }

        }
        return demande_conges;
    }

    @GetMapping("listevalideun/{typeconge}")
    public List<Demande_conge> listedemandevalide(@PathVariable("typeconge") Type_conge typeconge) {
        List<Demande_conge> demande_conges = new ArrayList<>();
        for (Demande_conge demande_conge : demande_congebRepository.getDemande_congeByTypeconge(typeconge)) {
            System.out.println(demande_conge);
            if ((demande_conge.getStatutconge().equals(Statut_conge.Enattente_de_validation) && demande_conge.getCollaborateur().getRoles().stream().anyMatch(role -> role.getName().equals(ERole.Role_manager2))) || demande_conge.getStatutconge().equals(Statut_conge.valide1)) {
                demande_conges.add(demande_conge);
                System.out.println(demande_conge);
            }


        }
        return demande_conges;
    }

    @PutMapping("/modifierjustifetat/{iddemande}")
    public Demande_conge modifieretatjustif(@PathVariable("iddemande") long iddemande) {
        Demande_conge demande_conge = demande_congebRepository.findById(iddemande).orElse(null);
        demande_conge.setJustificatifPresent(true);
        return demande_congebRepository.save(demande_conge);
    }

    @GetMapping("/getvacances")
    public List<JsonNode> getVacancesScolaires() {
        try {
            ClassPathResource resource = new ClassPathResource("frcalendrier.json");
            File jsonFile = resource.getFile();
            byte[] jsonData = Files.readAllBytes(jsonFile.toPath());
            JsonNode vacancesNode = objectMapper.readTree(jsonData);
            Iterator<JsonNode> iterator = vacancesNode.elements();
            List<JsonNode> vacances = new ArrayList<>();
            while (iterator.hasNext()) {
                vacances.add(iterator.next());
            }
            return vacances;
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier JSON des vacances scolaires", e);
        }
    }

    @GetMapping("/verfificationdechevauchement/{iduser}")
    public List<String> verificationdemandeconges(@PathVariable("iduser") long iduser) {
        List<String> messages = new ArrayList<>();
        Personnel personnel = personnelRepository.findById(iduser).orElse(null);
        List<Demande_conge> demandesconges = getdemandecongesdeuser(iduser);

        Date dateAujourdhui = new Date();
        // Récupérer les demandes de congé de l'utilisateur
        List<Demande_conge> demandesConge = getdemandecongesdeuser(iduser);

        // Filtrer les demandes de congé antérieures à aujourd'hui et avec un statut validé
        List<Demande_conge> demandesCongesAnterieurValidees = demandesConge.stream()
                .filter(demande -> demande.getDate_fin().before(dateAujourdhui))
                .filter(demande -> demande.getStatutconge().equals(Statut_conge.valide2))
                .filter(demande -> demande.getTypeconge().equals(Type_conge.regulier))
                .collect(Collectors.toList());

        for (Demande_conge demande_conge : demandesCongesAnterieurValidees) {
            System.out.println(demande_conge);
            Iterator<JsonNode> it = getVacancesScolaires().iterator();
            while (it.hasNext()) {
                JsonNode vacance = it.next();
                String typeVacances = vacance.get("description").asText();
                System.out.println(typeVacances);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx");
                OffsetDateTime debutVacance = OffsetDateTime.parse(vacance.get("start_date").asText(), formatter);
                OffsetDateTime finVacance = OffsetDateTime.parse(vacance.get("end_date").asText(), formatter);
                System.out.println(debutVacance);
                System.out.println(finVacance);
                java.sql.Date datedebutsql = (java.sql.Date) demande_conge.getDate_debut();
                LocalDate datdebulocal = datedebutsql.toLocalDate();
                OffsetDateTime debutCongedem;
                debutCongedem = datdebulocal.atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();

                java.sql.Date dateFinSql = (java.sql.Date) demande_conge.getDate_fin();
                LocalDate dateFinLocal = dateFinSql.toLocalDate();
                OffsetDateTime finCongedem;
                finCongedem = dateFinLocal.atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
                System.out.println(debutCongedem);
                System.out.println(finCongedem);
                if ((debutCongedem.isBefore(finVacance) && debutCongedem.isAfter(debutVacance)) ||
                        (finCongedem.isBefore(finVacance) && finCongedem.isAfter(debutVacance)) ||
                        (debutCongedem.isBefore(debutVacance) && finCongedem.isAfter(finVacance) ||
                                (debutCongedem.isEqual(debutVacance) && finCongedem.isEqual(finVacance)))) {

                    StringBuilder message = new StringBuilder();
                    message.append("L'utilisateur ").append(personnel.getUsername()).append(" a pris un congé qui chevauche  avec  les ").append("\n").append(typeVacances).append("  et qui tombe   ").append(debutVacance.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append(" au ").append(finVacance.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append(".\n");
                    messages.add(message.toString());
                }
            }
        }
        return messages;
    }

    @GetMapping("/getdoctors")
    public List<String> scrapeDoctors() throws IOException {
        List<String> doctorNames = new ArrayList<>();
        List<String> names = new ArrayList<>();
        String url = "http://197.13.14.115:90/AnnuairesMedecins/IndexAnnuairesMedecins?strGuidSpecialite=value53d0a972-f534-4f95-8749-7f2853e9cca3";
        Document doc = Jsoup.connect(url).timeout(600000).get();

        // Exemple : Supposons que les noms des médecins sont dans des éléments <div class="nom_medecin">
        Elements elements = doc.select(".dxgvDataRow_MetropolisBlue");
        int maxResults = 8;
        int count = 0;

        for (Element element : elements) {
            if (count > maxResults) break; // Stopper après avoir atteint la limite
            count++;

            doctorNames.add(element.text());

        }
        for (String chaine : doctorNames) {

            if (chaine == null || chaine.trim().isEmpty()) {
                continue;
            }

            String[] words = chaine.split("\\s+");
            if (words.length >= 2) {
                // Check if the second word is uppercase, indicating it's part of the last name
                if (words[1].equals(words[1].toUpperCase())) {
                    if (words.length >= 3) {
                        names.add(words[0] + " " + words[1] + " " + words[2]);
                    } else {
                        names.add(words[0] + " " + words[1]);
                    }
                } else {
                    names.add(words[0] + " " + words[1]);
                }
            }
        }

        names.add("Jean-Paul");

        return names;
    }

    public List<String> extractDoctorNames(List<String> details) {
        List<String> names = new ArrayList<>();

        for (String detail : details) {
            if (detail == null || detail.trim().isEmpty()) {
                continue;
            }

            String[] words = detail.split("\\s+");
            if (words.length >= 2) {
                // Check if the second word is uppercase, indicating it's part of the last name
                if (words[1].equals(words[1].toUpperCase())) {
                    if (words.length >= 3) {
                        names.add(words[0] + " " + words[1] + " " + words[2]);
                    } else {
                        names.add(words[0] + " " + words[1]);

                    }
                } else {
                    names.add(words[0] + " " + words[1]);
                }
            }
        }


        return names;
    }

    @Autowired
    Imagerepository imagerepository;
    ////traiter justif

    ///convertfiletomultipart
    public static MultipartFile convertFileToMultipartFile(File file) throws IOException {
        FileItem fileItem = new DiskFileItem("file",
                "image/jpeg", true, file.getName(), (int) file.length(), file.getParentFile());

        try (InputStream input = new FileInputStream(file);
             OutputStream os = fileItem.getOutputStream()) {
            IOUtils.copy(input, os);
        }

        return new CommonsMultipartFile(fileItem);
    }


    @PostMapping("/traiterJustif/{idjustif}")
    public String traiterjustificatif(@PathVariable("idjustif") Long idimage_justif) throws IOException {
        String resultatTrait = "";
        try {

            Image_justificatif image_justificatif = imagerepository.findById(idimage_justif).orElse(null);
            Demande_conge demandeConge = demande_congebRepository.findById(image_justificatif.getDemandecngjustif().getId_demandeconge()).orElse(null);
            if (demandeConge == null || image_justificatif == null) {
                return "Demande or Image not found";
            }

            // Download image from URL
            URL url = new URL(image_justificatif.getImagenUrl());
            InputStream inputStream = url.openStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            byte[] imageBytes = outputStream.toByteArray();

            // Create MultipartFile
            MultipartFile multipartFile = new MultipartFile() {
                @Override
                public String getName() {
                    return "file";
                }

                @Override
                public String getOriginalFilename() {
                    return image_justificatif.getName();
                }

                @Override
                public String getContentType() {
                    return "image/jpeg"; // Adjust the content type if necessary
                }

                @Override
                public boolean isEmpty() {
                    return imageBytes.length == 0;
                }

                @Override
                public long getSize() {
                    return imageBytes.length;
                }

                @Override
                public byte[] getBytes() throws IOException {
                    return imageBytes;
                }

                @Override
                public InputStream getInputStream() throws IOException {
                    return inputStream;
                }

                @Override
                public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
                    Files.write(dest.toPath(), imageBytes);
                }
            };
            System.out.println("multipart");
            // Your business logic
            if (demandeConge.getTypecongeexceptionnel().equals(Type_conge_exceptionnel.Conge_Enfant_malade)) {
                System.out.println("conge enfant malade");
                System.out.println(scrapeDoctors());
                System.out.println(extractTextFromJustificatif(multipartFile));
                System.out.println(scrapeDoctors().contains(extractTextFromJustificatif(multipartFile)));
                if (scrapeDoctors().contains(extractTextFromJustificatif(multipartFile))) {
                    System.out.println("Justificatif_valid");
                    resultatTrait += "Justificatif traité et Validé avec succès ";
                } else {
                    System.out.println("pasdentre");
                    resultatTrait += "Justificatif traité mais il n est pas validé  ";
                }
            }
        } catch (IOException e) {
            // Handle the case when the image cannot be retrieved from the URL
            System.out.println("Error retrieving image from URL: " + e.getMessage());
            // You can either provide a default image or handle the case in another way
        }
        return resultatTrait;
    }

    private String getExtensionFromContentType(String contentType) {
        switch (contentType) {
            case "image/jpeg":
                return ".jpg";
            case "image/png":
                return ".png";
            case "image/gif":
                return ".gif";
            default:
                return "";
        }
    }


    /////update image ////
    @PutMapping("modifierimage/{idjustifancienne}")
    public Image_justificatif modfierimage(@PathVariable("idjustifancienne") long idjustifancienne, @RequestParam("file") MultipartFile multipartFile) {
        Map<String, Object> variables = new HashMap<>();
        Image_justificatif image = null;
        Long iddemandeconge = ((Long) runtimeService.getVariable(currentProcessInstanceId, "id_demande_conge"));
        Demande_conge demande_conge = demande_congebRepository.findById(iddemandeconge).orElse(null);
        Boolean missingattachment = ((Boolean) runtimeService.getVariable(currentProcessInstanceId, "missingAttachment"));
        Boolean decision = ((Boolean) runtimeService.getVariable(currentProcessInstanceId, "decision"));
        runtimeService.setVariable(currentProcessInstanceId, "mail",false);

        try {
            Image_justificatif image_justificatif = imagerepository.findById(idjustifancienne).orElse(null);
            String oldpublciimageid = image_justificatif.getImagenId();

            if (demande_conge == null) {
                throw new RuntimeException("La demande de congé avec l'ID " + iddemandeconge + " n'a pas été trouvée.");
            }

            Long idcollab = demande_conge.getCollaborateur().getCin();
            variables.put("collabwithjustif", idcollab);

            String newurl = cloudinaryService.replaceCloudinaryImage(oldpublciimageid, multipartFile);
            image = imagerepository.findImage_justificatifByImagenId(oldpublciimageid);
            image.setImagenUrl(newurl);
            imagerepository.save(image);
            variables.put("imagepublicnewcid", image.getImagenId());
            System.out.println(image.getImagenUrl());
            runtimeService.setVariables(currentProcessInstanceId, variables);

            if (!decision && missingattachment) {
                String signalName = "Notif Withjustif";
                String activityId = "catchwithjustif";
                Execution waitingExecution = runtimeService.createExecutionQuery()
                        .processInstanceId(currentProcessInstanceId)
                        .activityId(activityId)
                        .signalEventSubscriptionName(signalName)
                        .singleResult();

                if (waitingExecution != null) {
                    System.out.println("Signal event reçu avec succès pour l'activité 'exceptionnelcatch'.");
                    runtimeService.signalEventReceived(signalName, waitingExecution.getId());
                } else {
                    throw new RuntimeException("Aucune exécution en attente trouvée pour l'événement de signal : " + signalName);
                }
                Task userTask = taskService.createTaskQuery()
                        .processInstanceId(currentProcessInstanceId)
                        .taskName("AjoutAutreJustif")
                        .singleResult();

                if (userTask != null) {
                    System.out.println("Tâche 'AjoutAutreJustif' trouvée avec l'ID : " + userTask.getId());
                    runtimeService.setVariable(currentProcessInstanceId, "missingAttachment", false);
                    taskService.complete(userTask.getId());
                    System.out.println("Tâche complétée avec succès.");
                } else {
                    throw new RuntimeException("La tâche 'AjoutAutreJustif' n'a pas été trouvée.");
                }
            } else if (decision && missingattachment) {
                String signalName = "Notif Withoutjustif";
                String activityId = "catchwithoutjustif";
                Execution waitingExecution = runtimeService.createExecutionQuery()
                        .processInstanceId(currentProcessInstanceId)
                        .activityId(activityId)
                        .signalEventSubscriptionName(signalName)
                        .singleResult();

                if (waitingExecution != null) {
                    System.out.println("Signal event reçu avec succès pour l'activité 'exceptionnelcatch'.");
                    runtimeService.signalEventReceived(signalName, waitingExecution.getId());
                } else {
                    throw new RuntimeException("Aucune exécution en attente trouvée pour l'événement de signal : " + signalName);
                }
                Task userTask = taskService.createTaskQuery()
                        .processInstanceId(currentProcessInstanceId)
                        .taskName("Ajoutjustif")
                        .singleResult();

                if (userTask != null) {
                    System.out.println("Tâche 'Ajoutjustif' trouvée avec l'ID : " + userTask.getId());
                    runtimeService.setVariable(currentProcessInstanceId, "missingAttachment", false);
                    taskService.complete(userTask.getId());
                    System.out.println("Tâche complétée avec succès.");
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de l'ajout : " + e.getMessage());
            e.printStackTrace();
        }
        return imagerepository.save(image);
    }


    @PostMapping("validatebygest/{iduser}")
    public List<Integer> refusereasonnbr(@PathVariable("iduser") long iduser) throws IOException {
        List<Integer> nombrejourlist = new ArrayList<>();
        Personnel personnelSoumisDemande = personnelRepository.findById(iduser).orElse(null);
        int nbJoursCongesExcepSur1Mois = 0;
        ;
        LocalDate dateActuelle = LocalDate.now();
        Month moisActuel = dateActuelle.getMonth();
        int anneeActuelle = dateActuelle.getYear();
        List<Demande_conge> listDemCongesExcepUser = demande_congebRepository.findDemande_congeByCollaborateurAndTypeconge(personnelSoumisDemande, Type_conge.exceptionnel);
        for (Demande_conge demandeConge : listDemCongesExcepUser) {
            java.sql.Date datedebutsql = (java.sql.Date) demandeConge.getDate_debut();
            LocalDate datdebulocal = datedebutsql.toLocalDate();

            java.sql.Date datefindatefinlocal = (java.sql.Date) demandeConge.getDate_fin();
            LocalDate datefinloc = datefindatefinlocal.toLocalDate();

            if (datdebulocal.getMonth() == moisActuel && datdebulocal.getYear() == anneeActuelle
                    && datefinloc.getMonth() == moisActuel && datefinloc.getYear() == anneeActuelle) {
                // Si oui, on incrémente le compteur de congés
                nbJoursCongesExcepSur1Mois++;

            }

        }
        nombrejourlist.add(nbJoursCongesExcepSur1Mois);

        return nombrejourlist;
    }


    public String extractTextFromImage(File imageFile) {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata-main"); // Spécifiez le chemin vers les fichiers de données Tesseract

        try {


            tesseract.setTessVariable("user_defined_dpi", "96");
            return tesseract.doOCR(imageFile);
        } catch (TesseractException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'extraction du texte OCR");

        }
    }


    @PostMapping("/extract-text")
    public Map<String, String> extractTextFromJustificatif(@RequestParam("image") MultipartFile image) {
        Map<String, String> resultMap = new HashMap<>();

        try {
            File tempFile = File.createTempFile("justificatif", ".jpg");
            image.transferTo(tempFile);

            String extractedText = extractTextFromImage(tempFile);

            // Expression régulière pour extraire le nom "Jean-Paul"
            Pattern patternNom = Pattern.compile("Je soussigné\\(e\\) (\\w+-\\w+)");
            Matcher matcherNom = patternNom.matcher(extractedText);
            String nom = matcherNom.find() ? matcherNom.group(1) : "";

            // Expression régulière pour extraire le nom "Sarra Fares"
            Pattern patternPrenom = Pattern.compile("Mr/Mme (\\w+ \\w+)");
            Matcher matcherPrenom = patternPrenom.matcher(extractedText);
            String prenom = matcherPrenom.find() ? matcherPrenom.group(1) : "";

            // Expression régulière pour extraire le nombre "3"
            Pattern patternDuree = Pattern.compile("Duree : (\\d+) jours");
            Matcher matcherDuree = patternDuree.matcher(extractedText);
            String duree = matcherDuree.find() ? matcherDuree.group(1) : "";
            resultMap.put("medecin", nom);
            resultMap.put("patient", prenom);
            resultMap.put("duree", duree);

            System.out.println("Nom: " + nom);
            System.out.println("Prénom: " + prenom);
            System.out.println("Durée: " + duree);

            return resultMap;

        } catch (IOException e) {
            resultMap.put("Erreur", "Erreur lors du traitement de l'image");
            return resultMap;
        }
    }


    @PostMapping("/validatedecison")
    public Demande_conge validatedecision() {
        Map<String, Object> variables = new HashMap<>();
        Long iddemandeconge = ((Long) runtimeService.getVariable(currentProcessInstanceId, "id_demande_conge"));
        Demande_conge demande_conge = demande_congebRepository.findById(iddemandeconge).orElse(null);
        Boolean deuxiemevalidion = ((Boolean) runtimeService.getVariable(currentProcessInstanceId, "deuxiemevalidation"));
        runtimeService.setVariable(currentProcessInstanceId, "mail",false);
        runtimeService.getVariable(currentProcessInstanceId, "id_demande_conge");

        try {
            if (!deuxiemevalidion) {
                if (demande_conge == null) {
                    throw new RuntimeException("La demande de congé avec l'ID " + iddemandeconge + " n'a pas été trouvée.");
                }

                Long idgestionnaire = demande_conge.getCollaborateur().getGestionnaire().getCin();
                variables.put("gestionnaire", idgestionnaire);

                if (demande_conge.getJustificatifPresent()) {
                    variables.put("missingAttachment", Boolean.FALSE);
                } else {
                    variables.put("missingAttachment", Boolean.TRUE);
                }

                demande_conge.setStatutconge(Statut_conge.valide2);
                boolean decision1 = true;
                variables.put("decision", decision1);

                Long idAyantSoumis = ((Long) runtimeService.getVariable(currentProcessInstanceId, "initiator"));
                Personnel personnelSoumis = personnelRepository.findById(idAyantSoumis).orElse(null);
                personnelSoumis.setEtatmail("false");
                personnelRepository.save(personnelSoumis);
                System.out.println("etatmailvalidate");
                System.out.println(personnelSoumis.getEtatmail());
                if (personnelSoumis == null) {
                    throw new RuntimeException("Le personnel ayant soumis la demande avec l'ID " + idAyantSoumis + " n'a pas été trouvé.");
                }
                Personnel Gestionnaire = personnelSoumis.getGestionnaire();
                variables.put("validator_name", Gestionnaire.getUsername());

                // Mettre à jour les variables du processus avec les nouvelles valeurs
                runtimeService.setVariables(currentProcessInstanceId, variables);
                Boolean decision = ((Boolean) runtimeService.getVariable(currentProcessInstanceId, "decision"));
                Boolean missingattacvement = ((Boolean) runtimeService.getVariable(currentProcessInstanceId, "missingAttachment"));

                if(decision && !missingattacvement ||  !decision && !missingattacvement || decision &&missingattacvement){
                    runtimeService.setVariable(currentProcessInstanceId, "mail",true);

                }else{
                    runtimeService.setVariable(currentProcessInstanceId, "mail",false);

                }
                System.out.println("Variables du processus mises à jour : " + variables);

                // Signal event
                String signalName = "Notif Signall";
                String activityId = "exceptionnelcatch";
                Execution waitingExecution = runtimeService.createExecutionQuery()
                        .processInstanceId(currentProcessInstanceId)
                        .activityId(activityId)
                        .signalEventSubscriptionName(signalName)
                        .singleResult();

                if (waitingExecution != null) {
                    System.out.println("Signal event reçu avec succès pour l'activité 'exceptionnelcatch'.");
                    runtimeService.signalEventReceived(signalName, waitingExecution.getId());
                } else {
                    throw new RuntimeException("Aucune exécution en attente trouvée pour l'événement de signal : " + signalName);
                }


                // Récupérer et compléter la tâche utilisateur
                Task userTask = taskService.createTaskQuery()
                        .processInstanceId(currentProcessInstanceId)
                        .taskName("gest validateOrrefuse")
                        .singleResult();

                if (userTask != null) {
                    System.out.println("Tâche 'gest validateOrrefuse' trouvée avec l'ID : " + userTask.getId());
                    taskService.complete(userTask.getId());
                    System.out.println("Tâche complétée avec succès.");
                } else {
                    throw new RuntimeException("La tâche 'gest validateOrrefuse' n'a pas été trouvée.");
                }

                // Sauvegarder les changements dans la demande de congé
                demande_congebRepository.save(demande_conge);
                System.out.println("Demande de congé sauvegardée avec succès.");
            }  else {
                    throw new RuntimeException("La tâche 'gest validateOrrefuse' n'a pas été trouvée.");
                }

        } catch (Exception e) {
            System.out.println("Erreur lors de la validation de la décision : " + e.getMessage());
            e.printStackTrace();
        }
        return demande_conge;

    }

    @PostMapping("/refuserdemande")
    public Demande_conge refuserdemande() {

        Map<String, Object> variables = new HashMap<>();
        Long iddemandeconge = ((Long) runtimeService.getVariable(currentProcessInstanceId, "id_demande_conge"));
        Demande_conge demande_conge = demande_congebRepository.findById(iddemandeconge).orElse(null);
        Boolean deuxiemevalidion = ((Boolean) runtimeService.getVariable(currentProcessInstanceId, "deuxiemevalidation"));
        System.out.println("deuxieme");
        System.out.println(deuxiemevalidion);
        runtimeService.setVariable(currentProcessInstanceId, "mail",false);
        runtimeService.getVariable(currentProcessInstanceId, "id_demande_conge");

        try {
            if (!deuxiemevalidion) {

                Long idgestionnaire = demande_conge.getCollaborateur().getGestionnaire().getCin();
                variables.put("gestionnaire", idgestionnaire);

                if (demande_conge.getJustificatifPresent()) {
                    variables.put("missingAttachment", Boolean.FALSE);
                } else {
                    variables.put("missingAttachment", Boolean.TRUE);
                }

                demande_conge.setStatutconge(Statut_conge.rejette);
                boolean decision1 = false;
                variables.put("decision", decision1);
                if (demande_conge == null) {
                    throw new RuntimeException("La demande de congé avec l'ID " + iddemandeconge + " n'a pas été trouvée.");
                }

                Long idAyantSoumis = ((Long) runtimeService.getVariable(currentProcessInstanceId, "initiator"));
                Personnel personnelSoumis = personnelRepository.findById(idAyantSoumis).orElse(null);
                personnelSoumis.setEtatmail("false");
                personnelRepository.save(personnelSoumis);
                System.out.println("etatmailvalidate");
                System.out.println(personnelSoumis.getEtatmail());
                if (personnelSoumis == null) {
                    throw new RuntimeException("Le personnel ayant soumis la demande avec l'ID " + idAyantSoumis + " n'a pas été trouvé.");
                }
                Personnel Gestionnaire = personnelSoumis.getGestionnaire();
                variables.put("validator_name", Gestionnaire.getUsername());
                Boolean decision = ((Boolean) runtimeService.getVariable(currentProcessInstanceId, "decision"));
                Boolean missingattacvement = ((Boolean) runtimeService.getVariable(currentProcessInstanceId, "missingAttachment"));

                // Mettre à jour les variables du processus avec les nouvelles valeurs
                runtimeService.setVariables(currentProcessInstanceId, variables);

                System.out.println("Variables du processus mises à jour : " + variables);

                // Signal event
                String signalName = "Notif Signall";
                String activityId = "exceptionnelcatch";
                Execution waitingExecution = runtimeService.createExecutionQuery()
                        .processInstanceId(currentProcessInstanceId)
                        .activityId(activityId)
                        .signalEventSubscriptionName(signalName)
                        .singleResult();

                if (waitingExecution != null) {
                    System.out.println("Signal event reçu avec succès pour l'activité 'exceptionnelcatch'.");
                    runtimeService.signalEventReceived(signalName, waitingExecution.getId());
                } else {
                    throw new RuntimeException("Aucune exécution en attente trouvée pour l'événement de signal : " + signalName);
                }

                // Récupérer et compléter la tâche utilisateur
                Task userTask = taskService.createTaskQuery()
                        .processInstanceId(currentProcessInstanceId)
                        .taskName("gest validateOrrefuse")
                        .singleResult();

                if (userTask != null) {
                    System.out.println("Tâche 'gest validateOrrefuse' trouvée avec l'ID : " + userTask.getId());
                    taskService.complete(userTask.getId());

System.out.println(decision);

                    System.out.println("Tâche complétée avec succès.");
                } else {
                    throw new RuntimeException("La tâche 'gest validateOrrefuse' n'a pas été trouvée.");
                }

                // Sauvegarder les changements dans la demande de congé
                demande_congebRepository.save(demande_conge);
                System.out.println("Demande de congé sauvegardée avec succès.");
            } else {
                Boolean mssingattachement = ((Boolean) runtimeService.getVariable(currentProcessInstanceId, "missingAttachment"));
                Boolean decision2 = ((Boolean) runtimeService.getVariable(currentProcessInstanceId, "decision"));
                System.out.println(mssingattachement);
                System.out.println(decision2);

                Task userTask = taskService.createTaskQuery()
                        .processInstanceId(currentProcessInstanceId)
                        .taskName("gest validateOrrefuse")
                        .singleResult();

                if (userTask != null) {
                    System.out.println("Tâche 'gest validateOrrefuse' trouvée avec l'ID : " + userTask.getId());
                    taskService.complete(userTask.getId());
                    System.out.println("Tâche complétée avec succès.");
                } else {
                    throw new RuntimeException("La tâche 'gest validateOrrefuse' n'a pas été trouvée.");
                }
            }


        } catch (Exception e) {
            System.out.println("Erreur lors de la validation de la décision : " + e.getMessage());
            e.printStackTrace();
        }
        return demande_conge;
    }

    /*  @PutMapping("/ajoutjustficatif")
      public Image_justificatif ajoutjustificatif(@RequestParam("image") MultipartFile image) {
          Map<String, Object> variables = new HashMap<>();
          Long iddemandeconge = ((Long) runtimeService.getVariable(currentProcessInstanceId, "id_demande_conge"));
          Demande_conge demande_conge = demande_congebRepository.findById(iddemandeconge).orElse(null);
          try {
              if (demande_conge == null) {
                  throw new RuntimeException("La demande de congé avec l'ID " + iddemandeconge + " n'a pas été trouvée.");
              AddandAssign(image,iddemandeconge);


              }
          } catch (Exception e) {
              System.out.println("Erreur lors de la validation de la décision : " + e.getMessage());
              e.printStackTrace();
          }

      }*/

    @GetMapping("/getjustificatifpresent/{iddemande}")
    public String verifierpresencejustif(@PathVariable("iddemande") long iddemande) {
        Demande_conge demande_conge = demande_congebRepository.findById(iddemande).orElse(null);
        String justificatifpresent = "";
        if (demande_conge.getJustificatifPresent()) {
            justificatifpresent = "Justificatif fourni";
        } else {
            justificatifpresent = "Justifcatif absent ";
        }
        return justificatifpresent;
    }

    //////getmetadata
    @GetMapping("/getmetadata/{publicid}")
    public Map getmetadata(@PathVariable("publicid") String publicid) {
        return cloudinaryService.getImageMetadata(publicid);
    }

    /////Mon dossier numerique
    @GetMapping("/getjustifbyidem/{iduser}")
    public List<DemandeJustificatifDTO> getJustificatifsByDemande(@PathVariable("iduser") long iduseer) {
        List<DemandeJustificatifDTO> result = new ArrayList<>();
        Personnel personnelconnecte = personnelRepository.findById(iduseer).orElse(null);
        // Récupérer toutes les demandes de congé
        List<Demande_conge> demandes = demande_congebRepository.findDemande_congeByCollaborateur(personnelconnecte);

        // Pour chaque demande de congé, récupérer son justificatif associé (hypothèse : un seul justificatif par demande)
        for (Demande_conge demande : demandes) {
            List<Image_justificatif> justificatif = imagerepository.findImage_justificatifByDemandecngjustif(demande);
            List<String> imageUrls = new ArrayList<>();
            List<Long> imageids = new ArrayList<>();
            List<String> publicids = new ArrayList<>();
            for (Image_justificatif justificatifs : justificatif) {
                imageUrls.add(justificatifs.getImagenUrl());
                imageids.add(justificatifs.getId());
                publicids.add(justificatifs.getImagenId());
            }
            // Créer un DTO pour encapsuler l'ID de la demande et le justificatif
            DemandeJustificatifDTO dto = new DemandeJustificatifDTO(demande.getId_demandeconge(), imageUrls, imageids, publicids, demande.getTypeconge(), demande.getTypecongeexceptionnel());

            result.add(dto);
        }

        return result;
    }

    @GetMapping("findimagesbudemn/{iddemande}")
    public List<Image_justificatif> getimagesbydem(@PathVariable("iddemande") long iddemande) {
        Demande_conge demande_conge = demande_congebRepository.findById(iddemande).orElse(null);
        return imagerepository.findImage_justificatifByDemandecngjustif(demande_conge);
    }

    @PostMapping("/ajoutjustifwithout")
    public Image_justificatif ajouterwithoutjustif(@RequestParam("image") MultipartFile image) throws IOException {
        Map<String, Object> variables = new HashMap<>();
        Long iddemandeconge = ((Long) runtimeService.getVariable(currentProcessInstanceId, "id_demande_conge"));
        Image_justificatif imagess=new Image_justificatif();
        runtimeService.setVariable(currentProcessInstanceId, "mail",false);
        runtimeService.getVariable(currentProcessInstanceId, "id_demande_conge");
        Demande_conge demandeconge =demande_congebRepository.findById(iddemandeconge).orElse(null);
        Personnel personnesoumisdenade = personnelRepository.findById(demandeconge.getCollaborateur().getCin()).orElse(null);

        personnesoumisdenade.setEtatmail("false");
        personnelRepository.save(personnesoumisdenade);
        System.out.println("etatmailwithout");
        System.out.println(personnesoumisdenade.getEtatmail());
        try {
            System.out.println("get");
            Boolean missingatathemnt = ((Boolean) runtimeService.getVariable(currentProcessInstanceId, "missingAttachment"));
            System.out.println(missingatathemnt);

            Boolean decision = ((Boolean) runtimeService.getVariable(currentProcessInstanceId, "decision"));
            System.out.println(decision);
            if (missingatathemnt && decision) {
                System.out.println(" missingatch ==Trueand Decision == true");
                 imagess = imageserv.AddandAssig(image, iddemandeconge);
                variables.put("imagepublicnewcid", imagess.getImagenId());
                System.out.println(imagess.getImagenUrl());
                runtimeService.setVariable(currentProcessInstanceId, "missingAttachment", false);
                Demande_conge demande_conge = demande_congebRepository.findById(iddemandeconge).orElse(null);
                demande_conge.setJustificatifPresent(true);
                demande_congebRepository.save(demande_conge);
                runtimeService.setVariables(currentProcessInstanceId, variables);
                String signalName = "Notif Withoutjustif";
                String activityId = "catchwithoutjustif";
                Execution waitingExecution = runtimeService.createExecutionQuery()
                        .processInstanceId(currentProcessInstanceId)
                        .activityId(activityId)
                        .signalEventSubscriptionName(signalName)
                        .singleResult();

                if (waitingExecution != null) {
                    System.out.println("Signal event reçu avec succès pour l'activité 'watchwithoutjutif'.");
                    runtimeService.signalEventReceived(signalName, waitingExecution.getId());
                } else {
                    throw new RuntimeException("Aucune exécution en attente trouvée pour l'événement de signal : " + signalName);
                }

                // Récupérer et compléter la tâche utilisateur
                Task userTask = taskService.createTaskQuery()
                        .processInstanceId(currentProcessInstanceId)
                        .taskName("Ajoutjustif")
                        .singleResult();

                if (userTask != null) {
                    System.out.println("Tâche 'Ajoutjustif' trouvée avec l'ID : " + userTask.getId());
                    taskService.complete(userTask.getId());
                    System.out.println("Tâche complétée avec succès.");
                } else {
                    throw new RuntimeException("La tâche 'Ajoutjustif' n'a pas été trouvée.");
                }
            }
            if (missingatathemnt == true && !decision) {
                System.out.println(" missingatch ==Trueand Decision == false");
                 imagess = imageserv.AddandAssig(image, iddemandeconge);
                variables.put("imagepublicnewcid", imagess.getImagenId());
                System.out.println(imagess.getImagenUrl());
                runtimeService.setVariable(currentProcessInstanceId, "missingAttachment", false);
                Demande_conge demande_congee = demande_congebRepository.findById(iddemandeconge).orElse(null);
                System.out.println("justifpresentsettrue");
                demande_congee.setJustificatifPresent(true);
                demande_congebRepository.save(demande_congee);
                runtimeService.setVariables(currentProcessInstanceId, variables);
                String signalNamee = "Notif Withjustif";
                String activityIdd = "catchwithjustif";
                Execution waitingExecutionn = runtimeService.createExecutionQuery()
                        .processInstanceId(currentProcessInstanceId)
                        .activityId(activityIdd)
                        .signalEventSubscriptionName(signalNamee)
                        .singleResult();

                if (waitingExecutionn != null) {
                    System.out.println("Signal event reçu avec succès pour l'activité 'watchwithjustif'.");
                    runtimeService.signalEventReceived(signalNamee, waitingExecutionn.getId());
                } else {
                    throw new RuntimeException("Aucune exécution en attente trouvée pour l'événement de signal : " + signalNamee);
                }

                // Récupérer et compléter la tâche utilisateur
                Task userTaskk = taskService.createTaskQuery()
                        .processInstanceId(currentProcessInstanceId)
                        .taskName("AjoutAutreJustif")
                        .singleResult();

                if (userTaskk != null) {
                    System.out.println("Tâche 'AjoutAutreJustif' trouvée avec l'ID : " + userTaskk.getId());
                    taskService.complete(userTaskk.getId());
                    System.out.println("Tâche complétée avec succès.");
                } else {
                    throw new RuntimeException("La tâche 'AjoutAutreJustif' n'a pas été trouvée.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imagess;
    }
@PostMapping("/rappeler")
public void rappeleraccepter(){
    long  iduser = (long ) runtimeService.getVariable(currentProcessInstanceId, "initiator");

    String str = Long.toString(iduser);
        chatcontroller.ajoutjustif("Veuiller verifier votre espace",str);
    Task userTaskk = taskService.createTaskQuery()
            .processInstanceId(currentProcessInstanceId)
            .taskName("rappel")
            .singleResult();
  runtimeService.setVariable(currentProcessInstanceId, "hasReminded",true);

    if (userTaskk != null) {
        System.out.println("Tâche 'rappel' trouvée avec l'ID : " + userTaskk.getId());
        taskService.complete(userTaskk.getId());
        System.out.println("Tâche rappel avec succès.");
    } else {
        throw new RuntimeException("La tâche 'AjoutAutreJustif' n'a pas été trouvée.");
    }

}
    @PostMapping("/nepasrappeler")
    public void nepasrappeler(){
        long  iduser = (long ) runtimeService.getVariable(currentProcessInstanceId, "initiator");

        String str = Long.toString(iduser);
        chatcontroller.ajoutjustif("Veuiller verifier votre espace",str);
        Task userTaskk = taskService.createTaskQuery()
                .processInstanceId(currentProcessInstanceId)
                .taskName("rappel")
                .singleResult();
        runtimeService.setVariable(currentProcessInstanceId, "hasReminded",false);

        if (userTaskk != null) {
            System.out.println("Tâche 'rappel' trouvée avec l'ID : " + userTaskk.getId());
            taskService.complete(userTaskk.getId());
            System.out.println("Tâche rappel avec succès.");
        } else {
            throw new RuntimeException("La tâche 'AjoutAutreJustif' n'a pas été trouvée.");
        }}
    @GetMapping("/getaaluser")
    public List<Personnel> getallusers() {
        return personnelRepository.findAll();
    }

    @GetMapping("/demandestraitesemcour")
    public int demandetraitessemcourante() {
        LocalDate now = LocalDate.now();
        LocalDate debutSemaine = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate finSemaine = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        Date datedeb = Date.from(debutSemaine.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date datefin = Date.from(finSemaine.atStartOfDay(ZoneId.systemDefault()).toInstant());

        return demande_congebRepository.demandesemainecourantes(datedeb, datefin);
    }


    @GetMapping("datewithmax")
    public List<DataValidation> getValidationsByDayOfWeek() {
        LocalDate now = LocalDate.now();
        LocalDate debutSemaine = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate finSemaine = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        Date datedeb = Date.from(debutSemaine.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date datefin = Date.from(finSemaine.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Récupérer les demandes de congé validées dans la semaine courante
        List<Demande_conge> demandes = demande_congebRepository.findDemande_congeByDatedecisionBetween(datedeb, datefin);

        // Compter les validations par jour de la semaine
        Map<DayOfWeek, Integer> validationsParJour = new HashMap<>();
        for (Demande_conge demande : demandes) {
            java.sql.Date datedec = (java.sql.Date) demande.getDatedecision();
            LocalDate dateDecision = datedec.toLocalDate();

            if (dateDecision.getDayOfWeek() != DayOfWeek.SATURDAY
                    && dateDecision.getDayOfWeek() != DayOfWeek.SUNDAY
                    && demande.getStatutconge().equals(Statut_conge.valide2)) {
                validationsParJour.put(dateDecision.getDayOfWeek(),
                        validationsParJour.getOrDefault(dateDecision.getDayOfWeek(), 0) + 1);
            }
        }

        // Créer une liste de tous les jours de la semaine avec leurs validations correspondantes
        List<DataValidation> allDays = new ArrayList<>();
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                int validations = validationsParJour.getOrDefault(dayOfWeek, 0);
                allDays.add(new DataValidation(dayOfWeek, validations));
            }
        }

        return allDays;
    }

    @GetMapping("refus")
    public List<DataValidation> getrefus() {
        LocalDate now = LocalDate.now();
        LocalDate debutSemaine = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate finSemaine = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        Date datedeb = Date.from(debutSemaine.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date datefin = Date.from(finSemaine.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Récupérer les demandes de congé validées dans la semaine courante
        List<Demande_conge> demandes = demande_congebRepository.findDemande_congeByDatedecisionBetween(datedeb, datefin);

        // Compter les validations par jour de la semaine
        Map<DayOfWeek, Integer> validationsParJour = new HashMap<>();
        for (Demande_conge demande : demandes) {
            java.sql.Date datedec = (java.sql.Date) demande.getDatedecision();
            LocalDate dateDecision = datedec.toLocalDate();

            if (dateDecision.getDayOfWeek() != DayOfWeek.SATURDAY
                    && dateDecision.getDayOfWeek() != DayOfWeek.SUNDAY
                    && demande.getStatutconge().equals(Statut_conge.rejette)) {
                validationsParJour.put(dateDecision.getDayOfWeek(),
                        validationsParJour.getOrDefault(dateDecision.getDayOfWeek(), 0) + 1);
            }
        }

        // Créer une liste de tous les jours de la semaine avec leurs validations correspondantes
        List<DataValidation> allDays = new ArrayList<>();
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                int validations = validationsParJour.getOrDefault(dayOfWeek, 0);
                allDays.add(new DataValidation(dayOfWeek, validations));
            }
        }

        return allDays;
    }

    @GetMapping("countbytype")
    public Map<Type_conge_exceptionnel, Integer> countDemandesParType() {
        LocalDate now = LocalDate.now();
        LocalDate debutMois = LocalDate.of(now.getYear(), now.getMonth(), 1);
        LocalDate finMois = debutMois.plusMonths(1).minusDays(1);
        Date datedeb = Date.from(debutMois.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date datefin = Date.from(finMois.atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<Demande_conge> demandes = demande_congebRepository.foundbydatdeby(datedeb, datefin);
        Map<Type_conge_exceptionnel, Integer> countByType = new HashMap<>();
        for (Demande_conge demande : demandes) {
            Type_conge_exceptionnel typeConge = demande.getTypecongeexceptionnel();
            if (typeConge != null) {
                countByType.put(typeConge, countByType.getOrDefault(typeConge, 0) + 1);
            }
        }
        return countByType;
    }

    @GetMapping("countbytypeconge")
    public Map<Type_conge, Integer> countbytypeonge() {
        LocalDate now = LocalDate.now();
        LocalDate debutMois = LocalDate.of(now.getYear(), now.getMonth(), 1);
        LocalDate finMois = debutMois.plusMonths(1).minusDays(1);
        Date datedeb = Date.from(debutMois.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date datefin = Date.from(finMois.atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<Demande_conge> demandes = demande_congebRepository.foundbydatdeby(datedeb, datefin);
        Map<Type_conge, Integer> countByType = new HashMap<>();
        for (Demande_conge demande : demandes) {
            Type_conge typeConge = demande.getTypeconge();
            if (typeConge != null) {
                countByType.put(typeConge, countByType.getOrDefault(typeConge, 0) + 1);
            }
        }
        return countByType;
    }

    Set<Long> demandesTrait = new HashSet<>();

    @GetMapping("/soumissionparvac")
    public Map<String, Integer> compareVacationsAndConges() throws IOException {
        Map<String, Integer> soumissionsParVacances = new HashMap<>();
        List<Demande_conge> demandeconges = demande_congebRepository.findAll();

        List<JsonNode> vacances = getVacancesScolaires();

        // Parcourir chaque demande de congé
        for (Demande_conge demande_conge : demandeconges) {
            System.out.println("dem");
            java.sql.Date datedebutsql = (java.sql.Date) demande_conge.getDate_debut();
            LocalDate datdebulocal = datedebutsql.toLocalDate();
            OffsetDateTime debutCongedem = datdebulocal.atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();

            java.sql.Date dateFinSql = (java.sql.Date) demande_conge.getDate_fin();
            LocalDate dateFinLocal = dateFinSql.toLocalDate();
            OffsetDateTime finCongedem = dateFinLocal.atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();

            // Comparer avec chaque période de vacances
            for (JsonNode vacance : vacances) {
                String descriptionVacance = vacance.get("description").asText();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx");
                OffsetDateTime debutVacance = OffsetDateTime.parse(vacance.get("start_date").asText(), formatter);
                OffsetDateTime finVacance = OffsetDateTime.parse(vacance.get("end_date").asText(), formatter);

                // Vérifier si la demande de congé se trouve dans la période de vacances
                if ((debutCongedem.isBefore(finVacance) && debutCongedem.isAfter(debutVacance)) ||
                        (finCongedem.isBefore(finVacance) && finCongedem.isAfter(debutVacance)) ||
                        (debutCongedem.isBefore(debutVacance) && finCongedem.isAfter(finVacance) ||
                                (debutCongedem.isEqual(debutVacance) && finCongedem.isEqual(finVacance)))) {
                    if (demandesTrait.contains(demande_conge.getId_demandeconge())) {
                        continue; // Passer à la demande suivante si elle a déjà été traitée
                    }
                    soumissionsParVacances.put(descriptionVacance, soumissionsParVacances.getOrDefault(descriptionVacance, 0) + 1);
                    demandesTrait.add(demande_conge.getId_demandeconge());
                    System.out.println(demandesTrait);
                }
            }
        }

        return soumissionsParVacances;
    }
   @PostMapping("/getmapjson")
    public  Map<String, Integer> verifierduresyntec(){

       Map<String, Integer> typesCongesLegaux = new LinkedHashMap<>();
        // Accéder aux valeurs du Map pour les comparer ou les utiliser

        Iterator<JsonNode> it = gettypesconges().iterator();
        while (it.hasNext()) {
            JsonNode typeconge = it.next();
            System.out.println(typeconge);
            String typeconges = typeconge.get("type").asText();
            int dureejrs = typeconge.get("duree_en_jours").asInt();
            System.out.println(typeconges);
            System.out.println(dureejrs);
            typesCongesLegaux.put(typeconges, dureejrs);
    }
       return typesCongesLegaux;
    }

    @PostMapping("/maanagenexatatchement/{idjustif}")
    public List<String> managenewattachement(@PathVariable("idjustif") long idimage_justif) {
        String resultatTrait = "";
        List<String > resultat = new ArrayList<>();
        Boolean missingatathemnt = ((Boolean) runtimeService.getVariable(currentProcessInstanceId, "missingAttachment"));
        Boolean decision = ((Boolean) runtimeService.getVariable(currentProcessInstanceId, "decision"));
System.out.println(missingatathemnt);
System.out.println(decision);
try {
            Image_justificatif image_justificatif = imagerepository.findById(idimage_justif).orElse(null);

            Demande_conge demandeConge = demande_congebRepository.findById(image_justificatif.getDemandecngjustif().getId_demandeconge()).orElse(null);
            if (demandeConge == null || image_justificatif == null) {
                resultatTrait+= "Demande or Image not found";
                resultat.add(resultatTrait);
            }

            // Download image from URL
            URL url = new URL(image_justificatif.getImagenUrl());
            InputStream inputStream = url.openStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            byte[] imageBytes = outputStream.toByteArray();

            // Create MultipartFile
            MultipartFile multipartFile = new MultipartFile() {
                @Override
                public String getName() {
                    return "file";
                }

                @Override
                public String getOriginalFilename() {
                    return image_justificatif.getName();
                }

                @Override
                public String getContentType() {
                    return "image/jpeg"; // Adjust the content type if necessary
                }

                @Override
                public boolean isEmpty() {
                    return imageBytes.length == 0;
                }

                @Override
                public long getSize() {
                    return imageBytes.length;
                }

                @Override
                public byte[] getBytes() throws IOException {
                    return imageBytes;
                }

                @Override
                public InputStream getInputStream() throws IOException {
                    return inputStream;
                }

                @Override
                public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
                    Files.write(dest.toPath(), imageBytes);
                }
            };
System.out.println(scrapeDoctors());
System.out.println(extractTextFromJustificatif(multipartFile));

            resultatTrait+=  verifierDemandeConge(multipartFile, demandeConge.getId_demandeconge());
resultat.add(resultatTrait);
    Map<String, String> resultMap = extractTextFromJustificatif(multipartFile);
    String nomedecin = resultMap.get("medecin");
    System.out.println(scrapeDoctors().contains(nomedecin));
    System.out.println(resultatTrait.equals("duree legale bien appliquée"));

            if (demandeConge.getTypecongeexceptionnel().equals(Type_conge_exceptionnel.Conge_Enfant_malade)) {
                if (scrapeDoctors().contains(nomedecin)&& resultatTrait.equals("duree legale bien appliquée")) {
                   System.out.println("scrape");
                    resultatTrait = "Justificatif de l enfant malade traité et Validé avec succès";
                    resultat.add(resultatTrait);
                    runtimeService.setVariable(currentProcessInstanceId, "passthrough", true);
                    System.out.println("passthrough");
                    System.out.println(runtimeService.getVariable(currentProcessInstanceId, "passthrough"));
                    runtimeService.setVariable(currentProcessInstanceId, "missingAttachment", false);
                    runtimeService.setVariable(currentProcessInstanceId, "decision", true);

                } else {
                    resultatTrait =  "Justificatif  de l enfant malade traité et  n est pas Validé avec succès";
                    runtimeService.setVariable(currentProcessInstanceId, "missingAttachment", true);
resultat.add(resultatTrait);
                    runtimeService.setVariable(currentProcessInstanceId, "passthrough", false);

                }
                if(decision&&!missingatathemnt){
                    Task userTask = taskService.createTaskQuery()
                            .processInstanceId(currentProcessInstanceId)
                            .taskName("GestionnaireManageNewAttachemnt")
                            .singleResult();

                    if (userTask != null) {
                        taskService.complete(userTask.getId());
                        System.out.println("Tâche complétée avec succès.");
                    } else {
                        throw new RuntimeException("La tâche 'collabupdateattahement' n'a pas été trouvée.");
                    }}
                if(!decision && !missingatathemnt){
                    Task userTask = taskService.createTaskQuery()
                            .processInstanceId(currentProcessInstanceId)
                            .taskName("maangeattah")
                            .singleResult();

                    if (userTask != null) {
                        taskService.complete(userTask.getId());
                        System.out.println("Tâche complétée avec succès.");
                    } else {
                        throw new RuntimeException("La tâche 'manageattach' n'a pas été trouvée.");
                    }}




            }

} catch (MalformedURLException ex) {
    throw new RuntimeException(ex);
} catch (IOException ex) {
    throw new RuntimeException(ex);
}


        return resultat;
    }
    @PostMapping ("/taille")
    public String DemandeCongetaille ( @RequestParam("file") MultipartFile multipartFile) {
        long  fileSize = multipartFile.getSize();

        String resultat = "";

        // Vérifier la taille de l'image

        long  maxImageSize = 6000000;
        if (  fileSize>maxImageSize) {
            resultat  += "La taille du justificatif doit  satisfaire les normes .Veuillez joindre un autre justificatif qui satisfait les normes";
             }

        else if ( maxImageSize> fileSize) {
            resultat+= "les normes sont satisfaits . Justificatif ajouté avec succés";

        }
        return resultat;
    }
    @DeleteMapping("/delete/{iddemande}")
    public void delete(@PathVariable("iddemande") long iddemande) {
        demande_congebRepository.deleteById(iddemande);
    }

    @PutMapping("/update/{iddemande}")

    public Demande_conge updatedem(@PathVariable("iddemande") long iddemande) {
        Demande_conge demande_conge = demande_congebRepository.findById(iddemande).orElse(null);
        return demande_congebRepository.save(demande_conge);
    }

    @GetMapping("/get/{iddemande}")

    public Demande_conge getde(@PathVariable("iddemande") long iddemande) {
        return demande_congebRepository.findById(iddemande).orElse(null);
    }

    @GetMapping("/recherche/{carac}")
    public List<Demande_conge> getdemande(@PathVariable("carac") String character) {
        return demande_congebRepository.rechercheDynamique(character);
    }
    @PostMapping("/confirmsoldeprev/{idpersonnel}")
    public Personnel confirmsoldeprev(@PathVariable("idpersonnel") long idpersonnel){
       Personnel personnel =personnelRepository.findById(idpersonnel).orElse(null);
       personnel.setConfirmsoldeprev(true);



        return personnelRepository.save(personnel);
    }
    @PostMapping("/nepasconfirmsoldeprev/{idpersonnel}")
    public Personnel nepasconfirmersoldeprev(@PathVariable("idpersonnel") long idpersonnel){
        Personnel personnel =personnelRepository.findById(idpersonnel).orElse(null);
        personnel.setConfirmsoldeprev(false);
        return personnelRepository.save(personnel);
    }
    @GetMapping("/getdureedeconges")
    public List<JsonNode> gettypesconges() {
        try {
            ClassPathResource resource = new ClassPathResource("fichier.json");
            File jsonFile = resource.getFile();
            byte[] jsonData = Files.readAllBytes(jsonFile.toPath());
            JsonNode types = objectMapper.readTree(jsonData);
            Iterator<JsonNode> iterator = types.elements();
            List<JsonNode> dureeenjours = new ArrayList<>();
            while (iterator.hasNext()) {
                dureeenjours.add(iterator.next());
            }
            return dureeenjours;
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier JSON des vacances scolaires", e);
        }
    }

    @PostMapping("/veriferdem/{iddemande}")
     public String  verifierDemandeConge(@RequestParam("image") MultipartFile image , @PathVariable("iddemande") long iddemande ) {
       String  resultat ="" ;
        Demande_conge demande_conge=demande_congebRepository.findById(iddemande).orElse(null);
         String typeCongeDemande = demande_conge.getTypecongeexceptionnel().toString();

         Map<String, String> resultMap = extractTextFromJustificatif(image);
         String duree = resultMap.get("duree");
         Integer dureee= Integer.parseInt(duree);

         Map<String, Integer> joursLegauxParTypeConge=verifierduresyntec();

         for (Map.Entry<String, Integer> entry : joursLegauxParTypeConge.entrySet()) {
             String typeconge = entry.getKey();

             Integer dureelegale = entry.getValue();

             if ( typeCongeDemande.equals(typeconge)) {

                 System.out.println("Type de congé trouvé : " + typeCongeDemande);
                 System.out.println("Durée légale pour ce type de congé : " + dureelegale);


                 if (dureee <= dureelegale) {
                     runtimeService.setVariable(currentProcessInstanceId, "missingAttachment", false);
                     runtimeService.setVariable(currentProcessInstanceId, "decision", true);
                     runtimeService.setVariable(currentProcessInstanceId, "passthrough", true);
                     resultat+= "duree legale bien appliquée";
                 } else {
                     System.out.println("La durée de la demande de congé dépasse les jours légaux pour ce type de congé.");
                     runtimeService.setVariable(currentProcessInstanceId, "missingAttachment", true);

                     runtimeService.setVariable(currentProcessInstanceId, "passthrough", false);
                     runtimeService.setVariable(currentProcessInstanceId, "deuxiemevalidation", true);
                     resultat+= "duree legale non  appliquée";
                 }

                 break;
             }
         }
return resultat;
     }
@GetMapping("/mail/{idpersonnel}")
    public String mail(@PathVariable("idpersonnel") long idpersonnel){
      Personnel personnel =personnelRepository.findById(idpersonnel).orElse(null);

    return personnel.getEtatmail();}
    @GetMapping("/getdecision")
    public String getdecision() {
        Boolean decision = (Boolean) runtimeService.getVariable(currentProcessInstanceId, "decision");
        Boolean missingattachment = (Boolean) runtimeService.getVariable(currentProcessInstanceId, "missingAttachment");
        String resultat = "";
        System.out.println(decision);
        System.out.println("here");
        System.out.println(missingattachment);
        if (decision && !missingattachment) {
            resultat += "Demande validée";
        } else if (!decision && !missingattachment) {
            resultat = "demande non validée";
        } else if (decision && missingattachment) {
            resultat += "il manque les justifs";
        }
    return resultat;
    }
    @GetMapping("/getuniquecode/{mail}")
    public Personnel getuserbypassword(@PathVariable("mail") String  mail ){
        return personnelRepository.findPersonnelByEmail(mail);

    }
}











