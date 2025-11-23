package com.bdu.asms.alumni_service_management.bussinesslogic.utils.commonutils.documentupload;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
@Getter
@Setter
public class FileStorageProperties {
    private String uploadDir = "uploads";
}
