package com.example.web.service;

import com.example.web.entity.ImportTransaction;
import com.example.web.repository.ImportTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class ImportTransactionService {

    @Autowired
    private ImportTransactionRepository txRepo;

    @Autowired
    private MinIOService minIOService;

    @Autowired
    private AppUserService appUserService;

    @Transactional
    public ImportTransaction prepare(MultipartFile file) {
        ImportTransaction tx = new ImportTransaction();
        tx.setFileName(file.getOriginalFilename());
        tx.setObjectName(UUID.randomUUID() + "_" + file.getOriginalFilename());
        tx.setStatus("PREPARED");
        return txRepo.save(tx);
    }

    public void commit(Long txId, MultipartFile file) throws Exception {
        ImportTransaction tx = txRepo.findById(txId)
                .orElseThrow(() -> new RuntimeException("Tx not found"));

        try {
            minIOService.putObject(file, tx.getObjectName());
            appUserService.importExcel(file);

            tx.setStatus("COMMITTED");
            txRepo.save(tx);

        } catch (Exception e) {
            rollback(txId);
            throw e;
        }
    }

    public void rollback(Long txId) {
        ImportTransaction tx = txRepo.findById(txId)
                .orElseThrow(() -> new RuntimeException("Tx not found"));

        if (minIOService.exists(tx.getObjectName())) {
            minIOService.removeObject(tx.getObjectName());
        }

        tx.setStatus("ROLLED_BACK");
        txRepo.save(tx);
    }
}
