package com.example.applicationcongess.repositories;

import com.example.applicationcongess.models.Demande_conge;
import com.example.applicationcongess.models.Personnel;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

@EnableJpaRepositories
public interface Demande_congebRepository extends CrudRepository<Demande_conge, Long>{
    Demande_conge findByCollaborateur_Cin(Long cin );
    @Query("SELECT dc FROM Demande_conge dc JOIN FETCH dc.collaborateur WHERE dc.collaborateur.cin = :iduser")
    public List<Demande_conge> getdemandecongesdeuser(@Param("iduser") Long iduser);

     @Query("select d from Demande_conge d where d.date_debut=:datedeb and d.date_fin=:datefin and d.collaborateur.cin=:idcollab")
     Demande_conge  demande  (@Param("datedeb") Date date_debut ,@Param("datefin") Date date_fin,@Param("idcollab")  Long  collabcin);

    @Query(" SELECT COUNT(DISTINCT d.collaborateur) as nombre_utilisateurs FROM Demande_conge d WHERE d.date_fin >=:date_fin_utilisateur AND d.date_debut <=:date_debut_utilisateur AND d.collaborateur.cin <>:collaborateurexclu  "  )
     int planningequipe (@Param("date_fin_utilisateur") Date date_fin_utilisateur,@Param("date_debut_utilisateur") Date date_debut_utilisateur,@Param("collaborateurexclu")  Long  collaborateurexclu);

    List<Demande_conge> findAll();

}
