package org.example.mozika.services.interfaces;

import io.minio.GetObjectResponse;
import io.minio.StatObjectResponse;

import java.io.InputStream;

public interface StorageService {
    String getPresignedUrl(String objectName);

    GetObjectResponse getObject(String objectName);

    GetObjectResponse getObject(String objectName, long offset, long length);

    StatObjectResponse statObject(String objectName);

    void uploadObject(String objectName, InputStream inputStream, long size, String contentType);
}
