package com.example.applicationcongess.services;


import com.example.applicationcongess.models.Demande_conge;
import com.example.applicationcongess.models.Image_justificatif;
import com.example.applicationcongess.repositories.ChatRoomrepo;
import com.example.applicationcongess.repositories.Demande_congebRepository;
import com.example.applicationcongess.repositories.Imagerepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class Imageserv {
    @Autowired
    CloudinaryService cloudinaryService;
   @Autowired
   Imagerepository imagerepository;
   @Autowired
    Demande_congebRepository demande_congebRepository;


    public List<Image_justificatif> list() {
        List<Image_justificatif> i = new ArrayList<>();
        imagerepository.findAll().forEach(i::add);
        return i;
    }


    public Optional<Image_justificatif> getOne(int id) {
        return imagerepository.findById((long) id);

    }
@Autowired
    ChatRoomrepo chatRoomrepo;

    public Image_justificatif AddandAssig(MultipartFile image,long iddemande,long chatroomid) throws IOException {

        Demande_conge dem=demande_congebRepository.findById(iddemande).orElse(null);
        Map result = cloudinaryService.upload(image);
        BufferedImage bi = ImageIO.read(image.getInputStream());
        Image_justificatif media = new Image_justificatif((String)
                result.get("original_filename")
                , (String) result.get("url"),
                (String) result.get("public_id"));

        media.setDemandecngjustif(dem);
        media.setChatroom(chatRoomrepo.findById(chatroomid).orElse(null));
        imagerepository.save(media);

        return media  ;
    }
    public Image_justificatif AddandAssig(MultipartFile image,long iddemande) throws IOException {

        Demande_conge dem=demande_congebRepository.findById(iddemande).orElse(null);
        Map result = cloudinaryService.upload(image);
        BufferedImage bi = ImageIO.read(image.getInputStream());
        Image_justificatif media = new Image_justificatif((String)
                result.get("original_filename")
                , (String) result.get("url"),
                (String) result.get("public_id"));

        media.setDemandecngjustif(dem);

        imagerepository.save(media);

        return media  ;
    }



    public void delete(int id) {
        imagerepository.deleteById((long) id);
    }


    public boolean exists(int id) {
        return imagerepository.existsById((long) id);
    }
}
