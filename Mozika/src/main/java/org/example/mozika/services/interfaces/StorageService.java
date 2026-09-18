package org.example.mozika.services.interfaces;

import io.minio.GetObjectResponse;
import io.minio.StatObjectResponse;

public interface StorageService {
    String getPresignedUrl(String objectName);

    GetObjectResponse getObject(String objectName);

    GetObjectResponse getObject(String objectName, long offset, long length);

    StatObjectResponse statObject(String objectName);
}
