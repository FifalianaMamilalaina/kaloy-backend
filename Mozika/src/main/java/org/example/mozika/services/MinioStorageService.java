package org.example.mozika.services;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.StatObjectResponse;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.services.interfaces.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MinioStorageService implements StorageService {

    private static final Logger log = LoggerFactory.getLogger(MinioStorageService.class);

    @Qualifier("publicMinioClient")
    private final MinioClient minioClient;

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
                            .build());
            log.debug("URL média générée pour l'objet MinIO: {}", objectName);
            return url;
        } catch (Exception e) {
            log.error("Impossible de générer l'URL média MinIO pour l'objet: {}", objectName, e);
            throw new InternalServerErrorException(
                    "Erreur lors de la génération de l'URL pour : " + objectName, e);
        }
    }

    @Override
    public GetObjectResponse getObject(String objectName) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            throw new InternalServerErrorException("Erreur lors de la lecture du média : " + objectName, e);
        }
    }

    @Override
    public GetObjectResponse getObject(String objectName, long offset, long length) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .offset(offset)
                    .length(length)
                    .build());
        } catch (Exception e) {
            throw new InternalServerErrorException("Erreur lors de la lecture partielle du média : " + objectName, e);
        }
    }

    @Override
    public StatObjectResponse statObject(String objectName) {
        try {
            return minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            throw new InternalServerErrorException("Média introuvable : " + objectName, e);
        }
    }
}
