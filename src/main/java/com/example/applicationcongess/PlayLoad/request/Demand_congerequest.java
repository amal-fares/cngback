package com.example.applicationcongess.PlayLoad.request;

import com.example.applicationcongess.enums.Type_conge;
import com.example.applicationcongess.enums.Type_conge_exceptionnel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Demand_congerequest {
    Date  date_debut ;
    Date date_fin ;
    Type_conge typeconge;
    @Nullable
    Type_conge_exceptionnel type_conge_exceptionnel;

}
