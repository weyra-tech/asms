package com.bdu.asms.alumni_service_management.bussinesslogic.utils.commonutils;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class IdGeneratorService {
    public String generatePublicId(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 15).toUpperCase();
    }
}
