package com.jsh.erp.service;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Service
public class GcsFileService {

    public static final String GCS_UPLOAD_BUCKET_URL = "https://storage.googleapis.com/";

    @Value("${gcs.credential-file-location}")
    private String credentialFileLocation;

    @Value("${gcs.bucket.name}")
    private String gcsBucketName;

    private Storage gcsStorage;

    public GcsFileService() {
        gcsStorage = StorageOptions.getDefaultInstance().getService();
    }

    public String uploadFile(MultipartFile file, String gcsPath) throws IOException {

        ServiceAccountCredentials credentials = ServiceAccountCredentials.fromStream(
                new FileInputStream(credentialFileLocation));

        gcsStorage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

        String uniqueFileName = gcsPath + "/" + UUID.randomUUID() + "-" + Objects.requireNonNull(file.getOriginalFilename());
        // 指定文件的 MIME 類型
        String contentType = file.getContentType(); // 或者手动指定文件类型

        byte[] fileBytes;
        try {
            fileBytes = file.getBytes();
        } catch (IOException e) {
            // 处理异常
            e.printStackTrace();
            return "File uploaded failed!";
        }

        BlobId blobId = BlobId.of(gcsBucketName, uniqueFileName);
        Blob blob = gcsStorage.create(BlobInfo.newBuilder(blobId).setContentType(contentType).build(),
                fileBytes);

        //"gs://" +
        return GCS_UPLOAD_BUCKET_URL + gcsBucketName + "/" + uniqueFileName;
    }

    public void downloadFile(String objectName, String destinationFilePath) throws FileNotFoundException {
        BlobId blobId = BlobId.of(gcsBucketName, objectName);
        Blob blob = gcsStorage.get(blobId);

        if (blob != null) {
            blob.downloadTo(Paths.get(destinationFilePath));
        } else {
            throw new FileNotFoundException("File not found in GCS.");
        }
    }
}
