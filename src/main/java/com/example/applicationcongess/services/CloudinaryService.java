package com.example.applicationcongess.services;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.metadata.IIOMetadata;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


@Service
public class CloudinaryService {
    final Cloudinary cloudinary;

    private Map<String, String> valuesMap = new HashMap<>();

    public CloudinaryService() {
        valuesMap.put("cloud_name", "dcbg7hmvc");
        valuesMap.put("api_key", "152545221988441");
        valuesMap.put("api_secret", "5Q-oCmrUKORJIYJe87lbFt43rbo");
        cloudinary = new Cloudinary(valuesMap);
    }

    public Map upload(MultipartFile multipartFile) throws IOException {

        File file = convert(multipartFile);



        Map result = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
        file.delete();
        return result;
    }

    public Map delete(String id) throws IOException {
        Map result = cloudinary.uploader().destroy(id, ObjectUtils.emptyMap());
        return result;
    }

    private File convert(MultipartFile multipartFile) throws IOException {
        File file = new File(multipartFile.getOriginalFilename());
        FileOutputStream fo = new FileOutputStream(file);
        fo.write(multipartFile.getBytes());
        fo.close();
        return file;
    }
    public Map getImageMetadata(String publicId) {
        try {
            return cloudinary.api().resource(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch image metadata", e);
        }
    }
    public String replaceCloudinaryImage(String oldImagePublicId, MultipartFile newImage) {
        try {
            // Supprimer l'ancienne image
            cloudinary.uploader().destroy(oldImagePublicId, ObjectUtils.emptyMap());

            // Télécharger la nouvelle image
            Map uploadResult = cloudinary.uploader().upload(newImage.getBytes(), ObjectUtils.asMap("public_id", oldImagePublicId));
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du remplacement de l'image Cloudinary", e);
        }


    }


}
