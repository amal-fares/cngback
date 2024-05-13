package com.example.applicationcongess.controller;

import com.example.applicationcongess.PlayLoad.Response.GestionnaireDTO;
import com.example.applicationcongess.PlayLoad.request.ChangePasswordRequest;
import com.example.applicationcongess.PlayLoad.request.Demand_congerequest;
import com.example.applicationcongess.PlayLoad.request.Validationmaangerprem;
import com.example.applicationcongess.enums.ERole;
import com.example.applicationcongess.enums.Statut_conge;
import com.example.applicationcongess.enums.Type_conge;
import com.example.applicationcongess.enums.Type_conge_exceptionnel;
import com.example.applicationcongess.models.*;
//import com.example.applicationcongess.services.ActivitiConfig;
import com.example.applicationcongess.repositories.ChatMessagerepo;
import com.example.applicationcongess.repositories.ChatRoomrepo;
import com.example.applicationcongess.repositories.Demande_congebRepository;
import com.example.applicationcongess.repositories.PersonnelRepository;
import com.example.applicationcongess.services.*;
import lombok.RequiredArgsConstructor;
//import org.activiti.engine.RuntimeService;
//import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
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
private  String valeur ;
    @Autowired
    private ActivitiConfig activitiConfig;
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
 private Demande_congebRepository demande_congebRepository;

  /*  @PostMapping("/envoidemande/{date_deb}/{date_fin}/{type_conge}/{id_personnel}")
    public Demande_conge envoidemande(@PathVariable("date_deb") @DateTimeFormat(pattern="yyyy-MM-dd") Date  date_deb, @PathVariable("date_fin") @DateTimeFormat(pattern="yyyy-MM-dd") Date date_fin, @PathVariable("type_conge") Type_conge type_conge, @RequestParam(value = "motif", required = false) String motif, @PathVariable("id_personnel") Long id_personnel){
        if (Type_conge.exceptionnel.equals(type_conge) && (motif == null || motif.isEmpty())) {
            throw new IllegalArgumentException("Le motif est obligatoire pour les congés exceptionnels.");
        }
        return demande_congeserv.envodemandecongeexeptionnel(date_deb,date_fin,type_conge,motif,id_personnel);
    }*/
    @PostMapping("/addpersonnel")
    public Personnel addpersonnel(@RequestBody Personnel personnel){
        return personnelserv.addpersoneel(personnel);
    }
    @PostMapping("updatedem_cng/{id_demande_conge}/{date_db}/{date_fn}")
    public Demande_conge updatedmcng(@PathVariable("id_demande_conge")Long id_demande_conge,@PathVariable("date_db")@DateTimeFormat(pattern="yyyy-MM-dd") Date  date_db,@PathVariable("date_fn")@DateTimeFormat(pattern="yyyy-MM-dd") Date  date_fn){
        return demande_congeserv.updatedemande_conge(id_demande_conge,date_db,date_fn);
    }
    @DeleteMapping("deletedem/{id_demande_conge}")
    public String deletedem(@PathVariable("id_demande_conge") Long id_demande_conge){
        return demande_congeserv.deletedemandeconge(id_demande_conge);
    }
    @PostMapping("/changepassword ")
    public ResponseEntity<String> changePassword(ChangePasswordRequest request){
        return  personnelserv.changePassword(request);
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
    public Map<String, Object>  remplirFormulaireActivitiusertask2(@PathVariable("commentaire2") String  commentaire,@PathVariable("decision2") boolean decision, @PathVariable("iddemand2") long  iddeamnd) {
        Map<String, Object> variablesvalidator = new HashMap<>();
        try {
            System.out.println(currentProcessInstanceId);
            String  validator_commentt = commentaire;
            variablesvalidator.put("decision2", decision);
            Demande_conge demande_conge=demande_congebRepository.findById(iddeamnd).orElse(null);
            Personnel collaborateur=personnelRepository.findById(demande_conge.getCollaborateur().getCin()).orElse(null);
            Personnel managerresponsable2=personnelRepository.findById(collaborateur.getManagerdeuxiemeniveau().getCin()).orElse(null);
            variablesvalidator.put("validatorname2",managerresponsable2.getUsername());
            Long cin =managerresponsable2.getCin();
            variablesvalidator.put("initiateur2",cin);
            String signalName = "Notif Signal";
            String activityId = "catchdeux";
            Execution waitingExecution = runtimeService.createExecutionQuery()
                    .processInstanceId(currentProcessInstanceId)
                    .activityId(activityId)
                    .signalEventSubscriptionName(signalName)
                    .singleResult();
            System.out.println(waitingExecution);
            runtimeService.setVariables(currentProcessInstanceId, variablesvalidator);
            runtimeService.signalEventReceived("Notif Signal", waitingExecution.getId());
            Task usertask2 = taskService.createTaskQuery()
                    .processInstanceId(currentProcessInstanceId).taskName("mangerdeuxrefuseor accept decision")
                    .singleResult();
            ;
            System.out.println(usertask2);
            System.out.println(usertask2);
            System.out.println(usertask2);
            System.out.println(usertask2.getId());
            taskService.complete(usertask2.getId());
        }
        catch(Exception e){
            System.out.println("Erreur lors de remplissage de Mangager2 : " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println(variablesvalidator);
        return variablesvalidator ;
    }
@GetMapping("process/{processInstanceId}/{activityId}/{signalName}")
    public boolean isProcessWaitingForSignal( @PathVariable ("processInstanceId") String processInstanceId, @PathVariable("activityId") String activityId,  @PathVariable("signalName") String signalName) {
        Execution waitingExecution = runtimeService.createExecutionQuery()
                .processInstanceId(processInstanceId)
                .activityId(activityId)
                .signalEventSubscriptionName(signalName)
                .singleResult();

        return waitingExecution != null;
    }

        @PostMapping("remplirchampvalidator/{commentaire}/{decision}/{iddemand}")
    public Map<String, Object>  remplirFormulaireActivitiusertask(@PathVariable("commentaire") String  commentaire,@PathVariable("decision") boolean decision, @PathVariable("iddemand") Long  iddeamnd) {

        Map<String, Object> variablesvalidator = new HashMap<>();
        try {
            System.out.println(currentProcessInstanceId);
            String  validator_commentt = commentaire;
            variablesvalidator.put("decision", decision);
Demande_conge demande_conge=demande_congebRepository.findById(iddeamnd).orElse(null);
Personnel collaborateur=personnelRepository.findById(demande_conge.getCollaborateur().getCin()).orElse(null);
           Personnel managerresponsable=personnelRepository.findById(collaborateur.getManager().getCin()).orElse(null);
            variablesvalidator.put("validatorname",managerresponsable.getUsername());
            Long cin =managerresponsable.getCin();
            variablesvalidator.put("initiateur",cin);
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
            System.out.println(usertask2);
            System.out.println(usertask2);
            System.out.println(usertask2);
            System.out.println(usertask2.getId());
            taskService.complete(usertask2.getId());
        }
        catch(Exception e){
            System.out.println("Erreur lors de remplissage de usertask : " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println(variablesvalidator);
        return variablesvalidator ;
    }
    @Autowired
    checkdonneesdeform checkdonneesdeform;
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("remplirchamp/{iduserconnete}")
    public Demande_conge remplirFormulaireActiviti(@RequestBody Demand_congerequest demande_conge , @PathVariable("iduserconnete") Long iduser)   {
        Map<String, Object> variables = new HashMap<>();
        Demande_conge demandeconge = new Demande_conge();
        try {

            Date start = demande_conge.getDate_debut();
            Boolean pausevariable = Boolean.FALSE;
            variables.put("variablepause", pausevariable);
            Date end = demande_conge.getDate_fin();
            Type_conge type_conge = demande_conge.getTypeconge();
            Type_conge_exceptionnel type_conge_exceptionnel = demande_conge.getType_conge_exceptionnel();
            variables.put("start", start);
            variables.put("end", end);
            variables.put("motif", type_conge);
            variables.put("type_congeexceptionnel", type_conge_exceptionnel);

            Personnel personnel = personnelRepository.findById(iduser).orElse(null);
            System.out.println(variables);
            System.out.println("variables");
            List<Type_conge_exceptionnel> elementsrequierjustif = Arrays.asList(Type_conge_exceptionnel.Conge_maternite, Type_conge_exceptionnel.Conge_de_Demangement, Type_conge_exceptionnel.Conge_demariage, Type_conge_exceptionnel.Conge_paternite, Type_conge_exceptionnel.Conge_Enfant_malade, Type_conge_exceptionnel.Conge_en_cas_de_décès,
                    Type_conge_exceptionnel.Conge_pour_service_militaire_ou_civil);

            if (elementsrequierjustif.contains(demande_conge.getType_conge_exceptionnel())) {
                demandeconge.setJustificatifs_requis(true);
            } else {
                demandeconge.setJustificatifs_requis(false);
            }
            LocalDate today = LocalDate.now();
            Date date = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
            demandeconge = new Demande_conge(start, end, type_conge, type_conge_exceptionnel, personnel, demandeconge.getJustificatifs_requis(),date);
            demandeconge.setStatut_conge(Statut_conge.Enattente_de_validation);

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
            taskService.setVariables(usertasks.getId(),variables);
            taskService.complete(usertasks.getId());

            System.out.println(checkdonneesdeform.resultat);
            System.out.println("resultat");
            if (checkdonneesdeform.resultat.equals("vous avez le droit de conges et les jours sont valides")) {
                demande_congebRepository.save(demandeconge);



            }

        }
        catch (Exception e) {
            System.out.println("Erreur lors de l'analyse de la  : " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println(variables);
        return demandeconge ;
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
 public int planningequipe (@PathVariable("date_fin_utilisateur") @DateTimeFormat(pattern="yyyy-MM-dd") Date date_fin_utilisateur,@PathVariable("date_debut_utilisateur") @DateTimeFormat(pattern="yyyy-MM-dd") Date date_debut_utilisateur) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    System.out.println(userDetails.getUsername());
    Personnel personnel = personnelRepository.findById(userDetails.getCin()).orElse(null);
        return demande_congebRepository.planningequipe(date_fin_utilisateur,date_debut_utilisateur,personnel.getCin());
}
@PostMapping("uploadimage")
public ResponseEntity<Map> uploadimage(@RequestParam("image") MultipartFile multipartFile) throws IOException {
        Map Data =cloudinaryService.
                upload(multipartFile);
        return  new ResponseEntity<>(Data , HttpStatus.OK);
}
@PostMapping("addandassignimage/{iddemande}/{chatroomid}")
    public Image_justificatif AddandAssig(@RequestParam("image") MultipartFile image, @PathVariable("iddemande") Long iddemande, @PathVariable("chatroomid") Long idchatroom) throws IOException {
    return     imageserv.AddandAssig(image,iddemande,idchatroom);
}
@GetMapping("getdemandescongesuser/{iduser}")
    public List<Demande_conge> getdemandecongesdeuser(@PathVariable("iduser") Long iduser){
        Personnel personnel=personnelRepository.findById(iduser).orElse(null);
return demande_congebRepository.getdemandecongesdeuser(iduser);
}
 @GetMapping("getalldem")
    public List<Demande_conge> getalldemand(){
       return  demande_congebRepository.findAll();}
 
@GetMapping("gettaskcompletion")
    public Date taskcompletion() {
   return  deadlinedutraitement.getTaskCompletionDate();
}

@PutMapping("assign/{iddemand}/{gestionnaireid}")
    public Demande_conge assignDemande(@PathVariable("iddemand")Long demandeId, @PathVariable("gestionnaireid") Long gestionnaireId) {
        Demande_conge demande = demande_congebRepository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));
        Personnel gestionnaire =personnelRepository.findById(gestionnaireId).orElse(null);
        demande.setCollaborateur(gestionnaire);
        return demande_congebRepository.save(demande);
    }
    public float  soldeconges(@PathVariable("iddemand")Long iduser,@PathVariable Long iddemandconges){
        Personnel personnel=personnelRepository.findById(iduser).orElse(null);
        Demande_conge demande_conge=demande_congebRepository.findById(iddemandconges).orElse(null);
        LocalDate startDate = ((Date) runtimeService.getVariable(currentProcessInstanceId, "start")).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = ((Date) runtimeService.getVariable(currentProcessInstanceId, "start")).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();


      long  number=  ChronoUnit.DAYS.between(startDate, endDate);
float num =0;
        if (demande_conge.getStatut_conge().equals(Statut_conge.valide2)){
           num=  personnel.getSolde_conges()-number ;
        }
        return num ;
    }

    @GetMapping("getdemandevalidesprem")
    public List<Demande_conge> getalldemandvalideprem() {
        List<Demande_conge> listdemandconges = getalldemand();
        return listdemandconges.stream()
                .filter(demande -> demande.getStatut_conge().equals(Statut_conge.valide1))
                .collect(Collectors.toList());
    }
    @GetMapping("getgestionnaire/{erole}")
    public Personnel getgestionnaire(@PathVariable ERole erole){
      return   personnelRepository.findByRoles(erole);
    }
    @Autowired
    ChatService chatService ;
    @GetMapping("getemployes/{idsender}/{idreceiver}/{iddemande}")
    public Chatroom getchatrrom(@PathVariable("idsender") long idsender ,@PathVariable("idreceiver") long idreceiver,@PathVariable("iddemande") long iddemande){
        return chatService.findchat(idsender,idreceiver,iddemande);
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
    public Demande_conge getdembyid( @PathVariable("iddem") long iddem){
        return demande_congebRepository.findById(iddem).orElse(null);
    }
    @Autowired
    ChatRoomrepo chatRoomrepo;
    @GetMapping("getmessagebyhatroom/{idchatroom}")
    public List<ChatMessage> getmessagebychatrrom(@PathVariable("idchatroom") long idchatroom){
    Chatroom chatrrom=chatRoomrepo.findById(idchatroom).orElse(null);
       return  chatMessagerepo.findChatMessageByChat(chatrrom);
    }
    @GetMapping("getchtaroombyidch/{chatroomid}")
    public Chatroom getchatroombyyidch(@PathVariable("chatroomid") long idchatroom){
      return   chatRoomrepo.getChatroomByChatroomId(idchatroom);
    }
}





