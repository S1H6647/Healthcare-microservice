package com.healthcare.appointment.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final Cloudinary cloudinary;

    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String contentType = file.getContentType();
        boolean isPdf = contentType != null && contentType.equals("application/pdf");
        
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "healthcare/doctors/qualifications",
                            "resource_type", isPdf ? "raw" : "image",
                            "public_id", UUID.randomUUID().toString()
                    )
            );
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload qualification document", e);
        }
    }
}
