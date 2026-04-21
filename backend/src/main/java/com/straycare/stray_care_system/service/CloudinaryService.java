package com.straycare.stray_care_system.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryService {

    private static final Logger logger = LoggerFactory.getLogger(CloudinaryService.class);

    private final Cloudinary cloudinary;
    private final String placeholderUrl;

    @Autowired
    public CloudinaryService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret,
            @Value("${cloudinary.placeholder-url:https://placehold.co/600x400?text=Image+Unavailable}") String placeholderUrl) {

        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        this.cloudinary = new Cloudinary(config);
        this.placeholderUrl = placeholderUrl;
    }

    CloudinaryService(Cloudinary cloudinary, String placeholderUrl) {
        this.cloudinary = cloudinary;
        this.placeholderUrl = placeholderUrl;
    }

    public UploadResult uploadFile(MultipartFile file) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("resource_type", "image")
            );

            Object secureUrl = result.get("secure_url");
            if (secureUrl == null || secureUrl.toString().isBlank()) {
                throw new IOException("Cloudinary upload did not return a secure_url");
            }

            return UploadResult.success(secureUrl.toString());
        } catch (Exception exception) {
            logger.error(
                "Cloudinary upload failed for file '{}' ({} bytes). Falling back to placeholder image.",
                file.getOriginalFilename(),
                file.getSize(),
                exception
            );

            return UploadResult.failure(
                placeholderUrl,
                new UploadError(
                    "CLOUDINARY_UPLOAD_FAILED",
                    "Image upload failed. A placeholder image URL has been returned instead."
                )
            );
        }
    }

    public record UploadResult(boolean success, String url, boolean fallbackUsed, UploadError error) {
        public static UploadResult success(String url) {
            return new UploadResult(true, url, false, null);
        }

        public static UploadResult failure(String url, UploadError error) {
            return new UploadResult(false, url, true, error);
        }
    }

    public record UploadError(String code, String message) {
    }
}
