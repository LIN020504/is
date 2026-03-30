package com.example.web.repository;

import com.example.web.entity.ImportTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportTransactionRepository
        extends JpaRepository<ImportTransaction, Long> {
}

