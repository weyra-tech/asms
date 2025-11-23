package com.bdu.asms.alumni_service_management.bussinesslogic.utils.commonutils.documentupload;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.servlet.multipart")
public class FileUploadProperties {
    private String maxFileSize;
    private String maxRequestSize;

    // Getters and setters
    public String getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(String maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public String getMaxRequestSize() {
        return maxRequestSize;
    }

    public void setMaxRequestSize(String maxRequestSize) {
        this.maxRequestSize = maxRequestSize;
    }

    // Helper method to convert to bytes
    public long getMaxFileSizeInBytes() {
        return parseSizeToBytes(maxFileSize);
    }

    public long getMaxRequestSizeInBytes() {
        return parseSizeToBytes(maxRequestSize);
    }

    private long parseSizeToBytes(String size) {
        if (size == null) return 50 * 1024 * 1024; // Default 50MB

        size = size.toUpperCase();
        if (size.endsWith("MB")) {
            return Long.parseLong(size.replace("MB", "").trim()) * 1024 * 1024;
        } else if (size.endsWith("KB")) {
            return Long.parseLong(size.replace("KB", "").trim()) * 1024;
        } else if (size.endsWith("GB")) {
            return Long.parseLong(size.replace("GB", "").trim()) * 1024 * 1024 * 1024;
        } else {
            return Long.parseLong(size);
        }
    }
}
