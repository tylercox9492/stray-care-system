package com.straycare.stray_care_system.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.straycare.stray_care_system.model.HealthRecord;
import com.straycare.stray_care_system.repository.HealthRecordRepository;

@Service
public class HealthRecordService {
      @Autowired
    private HealthRecordRepository healthRecordRepository;

    public HealthRecord saveHealthRecord(HealthRecord healthRecord) {
        return healthRecordRepository.save(healthRecord);
    }

    public List<HealthRecord> getHealthRecordsByDogId(Long dogId) {
        return healthRecordRepository.findByDogId(dogId);
    }
}
