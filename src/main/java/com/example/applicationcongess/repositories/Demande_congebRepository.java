package com.example.applicationcongess.repositories;

import com.example.applicationcongess.enums.Statut_conge;
import com.example.applicationcongess.enums.Type_conge;
import com.example.applicationcongess.enums.Type_conge_exceptionnel;
import com.example.applicationcongess.models.Demande_conge;
import com.example.applicationcongess.models.Personnel;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@EnableJpaRepositories
public interface Demande_congebRepository extends CrudRepository<Demande_conge, Long>{
    Demande_conge findByCollaborateur_Cin(Long cin );
    @Query("SELECT dc FROM Demande_conge dc JOIN FETCH dc.collaborateur WHERE dc.collaborateur.cin = :iduser")
    public List<Demande_conge> getdemandecongesdeuser(@Param("iduser") Long iduser);
    List<Demande_conge> findByDeadlineBetween(Date dateDebut, Date  dateFin);
    int countByTypeconge(Type_conge type_conge );
    int countByTypecongeexceptionnel(Type_conge_exceptionnel type_conge );
     @Query("select d from Demande_conge d where d.date_debut=:datedeb and d.date_fin=:datefin and d.collaborateur.cin=:idcollab")
     Demande_conge  demande  (@Param("datedeb") Date date_debut ,@Param("datefin") Date date_fin,@Param("idcollab")  Long  collabcin);

    @Query(" SELECT COUNT(DISTINCT d.collaborateur) as nombre_utilisateurs FROM Demande_conge d WHERE d.date_fin >=:date_fin_utilisateur AND d.date_debut <=:date_debut_utilisateur AND d.collaborateur.cin <>:collaborateurexclu  "  )
     int planningequipe (@Param("date_fin_utilisateur") Date date_fin_utilisateur,@Param("date_debut_utilisateur") Date date_debut_utilisateur,@Param("collaborateurexclu")  Long  collaborateurexclu);

    List<Demande_conge> findAll();
  List<Demande_conge>getDemande_congeByTypeconge(Type_conge typesconges);
List<Demande_conge> findDemande_congeByCollaborateurAndStatutcongeAndTypeconge(Personnel personnel , Statut_conge statut_conge,Type_conge type_conge);
List<Demande_conge> findDemande_congeByCollaborateurAndTypeconge(Personnel personnel , Type_conge type_conge);

List<Demande_conge> findDemande_congeByCollaborateur(Personnel personnel);
    @Query("SELECT COUNT(d) FROM Demande_conge d WHERE d.datedecision >= :debutSemaine AND d.datedecision <= :finSemaine"  )
    int demandesemainecourantes (@Param("debutSemaine") Date debutSemaine, @Param("finSemaine") Date finSemaine);

    @Query("SELECT COUNT(d) FROM Demande_conge d WHERE d.datedecision >= :debutSemaine AND d.datedecision <= :finSemaine AND d.statutconge=:status" )
 int counstdemvalidesemour (@Param("debutSemaine") Date debutsemaine,@Param("finSemaine") Date debutfin, @Param("status") Statut_conge Statusconge);
List<Demande_conge> findDemande_congeByDatedecisionBetween(Date datedeb,Date datefin);
@Query("SELECT d FROM Demande_conge d  WHERE d.date_debut >= :datedebut AND d.date_fin <= :datefin ")
List<Demande_conge> foundbydatdeby( @Param("datedebut")Date datedeb,@Param("datefin")Date datefin);



    @Query("SELECT u FROM Demande_conge u " +
            "WHERE (:character IS NULL " +
            "      OR LOWER(u.typecongeexceptionnel) LIKE LOWER(CONCAT('%', :character, '%')) " +
            "      OR LOWER(u.collaborateur.prenom) LIKE LOWER(CONCAT('%', :character, '%')) " +
            "      OR LOWER(u.date_debut) LIKE LOWER(CONCAT('%', :character, '%')) " +
            "      OR LOWER(u.date_fin) LIKE LOWER(CONCAT('%', :character, '%')))")
    List<Demande_conge> rechercheDynamique(@Param("character") String character);

}
