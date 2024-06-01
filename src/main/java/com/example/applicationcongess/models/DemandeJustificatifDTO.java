package com.example.applicationcongess.models;

import com.example.applicationcongess.enums.Type_conge;
import com.example.applicationcongess.enums.Type_conge_exceptionnel;
import lombok.Data;

import java.util.List;
@Data
public class DemandeJustificatifDTO {
    private Long demandeId;
    private Type_conge typeconge;
    private Type_conge_exceptionnel typecongeexceptionnel ;
    private List<String> Imageurl;
    private List<Long> ids;
    private List<String> publicids;

    public DemandeJustificatifDTO(Long demandeId, List<String> justificatif,List<Long>idjustifs,List<String>publicidsjustif,Type_conge typeconge,Type_conge_exceptionnel typecongeexceptionnel) {
        this.demandeId = demandeId;
        this.Imageurl = justificatif;
        this.ids=idjustifs;
        this.publicids=publicidsjustif;
       this.typeconge=typeconge;
       this.typecongeexceptionnel=typecongeexceptionnel;
    }

}
