package com.example.web.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "import_transaction")
public class ImportTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public ImportTransaction() {
    }

    public ImportTransaction(Long id, String fileName, String objectName, String status, LocalDateTime createdAt) {
        this.id = id;
        this.fileName = fileName;
        this.objectName = objectName;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    private String fileName;

    private String objectName;

    private String status; // PREPARED / COMMITTED / ROLLED_BACK

    private LocalDateTime createdAt = LocalDateTime.now();

    // getter / setter
}
