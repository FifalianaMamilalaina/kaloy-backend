package org.example.mozika.services.interfaces;

import io.minio.GetObjectResponse;
import io.minio.StatObjectResponse;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public interface StorageService {
    String getPresignedUrl(String objectName);

    String getPresignedUrl(String objectName, int expiry, TimeUnit unit);

    GetObjectResponse getObject(String objectName);

    GetObjectResponse getObject(String objectName, long offset, long length);

    StatObjectResponse statObject(String objectName);

    void uploadObject(String objectName, InputStream inputStream, long size, String contentType);
}
