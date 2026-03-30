package com.example.web.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.InputStream;

@Service
public class MinIOService {

    private final MinioClient minioClient;
    private final JdbcTemplate jdbcTemplate;
    private final String bucketName = "is-lab";

    @Autowired
    public MinIOService(MinioClient minioClient, JdbcTemplate jdbcTemplate) {
        this.minioClient = minioClient;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void uploadFileWithTransaction(String filePath, String objectName) throws Exception {
        // **Simulating database connection failure**
//        if (true) {
//            throw new RuntimeException("Database connection failed");
//        }

        // 1. 记录上传日志 (PENDING)
        jdbcTemplate.update("INSERT INTO file_upload_log (filename, status) VALUES (?, 'PENDING')", objectName);

        try (InputStream inputStream = new FileInputStream(filePath)) {
//            **Simulating MinIO connection failure**
            if (true) {
                System.out.println("=== MINIO FAILURE CODE EXECUTED ===");
                throw new RuntimeException("Simulating MinIO connection failure");
            }
            // 2. 上传到 MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType("application/octet-stream")
                            .build()
            );

            // 3. 更新上传状态 (COMPLETED)
            jdbcTemplate.update("UPDATE file_upload_log SET status = 'COMPLETED' WHERE filename = ?", objectName);

            // **4. Throw an exception after the business logic is completed**
//            if (true) {
//                throw new RuntimeException("Server business logic error");
//            }

            System.out.println("File uploaded successfully：" + objectName);
        } catch (Exception e) {
            // 4. 失败则回滚
            jdbcTemplate.update("UPDATE file_upload_log SET status = 'FAILED' WHERE filename = ?", objectName);
            throw new RuntimeException("File upload failed：" + e.getMessage());
        }
    }
    //添加
    @Transactional
    public void putObject(MultipartFile file, String objectName) throws Exception {
        System.out.println("=== ENTER putObject ===");

//        RuntimeException e = new RuntimeException("MinIO down");
//        e.printStackTrace(); // 确保控制台能看到异常
//        throw e;

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );
    }

    //是否存在
    public boolean exists(String objectName) {
        try {
            minioClient.statObject(
                    io.minio.StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //删除
    public void removeObject(String objectName) {
        try {
            minioClient.removeObject(
                    io.minio.RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            // 删除失败通常只记录日志，不再抛异常
            System.err.println("Failed to remove object: " + objectName);
        }
    }
}
