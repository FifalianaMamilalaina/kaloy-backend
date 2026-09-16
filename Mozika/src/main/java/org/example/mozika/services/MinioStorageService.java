package org.example.mozika.services;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.services.interfaces.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MinioStorageService implements StorageService {

    private final MinioClient minioClient;

    @Value("${minio.endpoint}")
    private String minioEndpoint;

    @Value("${minio.public-url}")
    private String minioPublicUrl;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.presigned-url-expiry-days}")
    private int expiryDays;

    @Override
    public String getPresignedUrl(String objectName) {
        if (objectName == null || objectName.isBlank()) {
            return null;
        }
        try {
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .method(Method.GET)
                            .expiry(expiryDays, TimeUnit.DAYS)
                            .build()
            );
            // SDK génère l'URL avec localhost:9000 — on remplace par l'URL publique LocalTunnel
            return url.replace(minioEndpoint, minioPublicUrl);
        } catch (Exception e) {
            throw new InternalServerErrorException(
                    "Erreur lors de la génération de l'URL pour : " + objectName, e
            );
        }
    }
}
