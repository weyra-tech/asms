package com.bdu.asms.alumni_service_management.bussinesslogic.utils.commonutils.documentupload;

import com.bdu.asms.alumni_service_management.bussinesslogic.exceptions.BadRequestException;
import com.bdu.asms.alumni_service_management.bussinesslogic.exceptions.StorageException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


import java.util.*;


@Service
public class FileStorageServiceUtil {

    private final Path fileStorageLocation;
    private static final Logger logger = LoggerFactory.getLogger(FileStorageServiceUtil.class);

    // ✅ Allowed file types (includes Excel, Word, PDF, Images)
    private static final List<String> ALLOWED_TYPES = Arrays.asList(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // DOCX
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // XLSX
            "application/vnd.ms-excel", // XLS
            "image/jpeg",
            "image/jpg",
            "image/png"
    );

    private final long maxFileSize;

    @Autowired
    public FileStorageServiceUtil(FileStorageProperties fileStorageProperties,
                                  FileUploadProperties fileUploadProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        // Get max file size from application properties
        this.maxFileSize = fileUploadProperties.getMaxFileSizeInBytes();

        logger.info("File storage configured with max file size: {} bytes ({} MB)",
                maxFileSize, maxFileSize / (1024 * 1024));

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create upload directory", ex);
        }
    }

    // ✅ Store single file
    public String storeFile(MultipartFile file, String subDirectory) {
        validateFile(file);

        try {
            String safeSubDirectory = sanitizePathComponent(subDirectory);
            Path targetDirectory = this.fileStorageLocation.resolve(safeSubDirectory).normalize();
            Files.createDirectories(targetDirectory);

            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
            String safeFileName = sanitizeFileName(originalFileName);

            String fileExtension = safeFileName.contains(".")
                    ? safeFileName.substring(safeFileName.lastIndexOf('.'))
                    : "";
            String fileName = UUID.randomUUID() + fileExtension;

            Path targetLocation = targetDirectory.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Validate Excel after saving (optional)
            if (isExcel(file)) {
                validateExcel(targetLocation);
            }

            return targetLocation.toString();
        } catch (IOException ex) {
            throw new RuntimeException("Failed to store file " + file.getOriginalFilename(), ex);
        }
    }

    // ✅ Store multiple files
    public List<String> storeMultipleFiles(List<MultipartFile> files, String subDirectory) {
        if (files == null || files.isEmpty()) {
            throw new BadRequestException("No files provided for upload");
        }

        // Validate total request size
        validateTotalRequestSize(files);

        validateFiles(files);

        List<String> savedPaths = new ArrayList<>();
        for (MultipartFile file : files) {
            String path = storeFile(file, subDirectory);
            savedPaths.add(path);
        }

        return savedPaths;
    }

    // Overload to support array input (e.g. from controller)
    public List<String> storeMultipleFiles(MultipartFile[] files, String subDirectory) {
        return storeMultipleFiles(Arrays.asList(files), subDirectory);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("File cannot be empty");
        }

        if (file.getSize() > maxFileSize) {
            throw new BadRequestException(
                    String.format("File size exceeds limit. Maximum allowed: %d MB, Your file: %.2f MB",
                            maxFileSize / (1024 * 1024),
                            file.getSize() / (1024.0 * 1024.0))
            );
        }

        String contentType = file.getContentType();
        if (!ALLOWED_TYPES.contains(contentType)) {
            throw new BadRequestException("Only PDF, DOCX, XLSX, XLS, JPEG, and PNG files are allowed");
        }
    }

    // ✅ Validate total request size for multiple files
    private void validateTotalRequestSize(List<MultipartFile> files) {
        long totalSize = files.stream().mapToLong(MultipartFile::getSize).sum();

        if (totalSize > maxFileSize) {
            throw new BadRequestException(
                    String.format("Total request size exceeds limit. Maximum allowed: %d MB, Your request: %.2f MB",
                            maxFileSize / (1024 * 1024),
                            totalSize / (1024.0 * 1024.0))
            );
        }
    }

    // ✅ Excel validation
    private void validateExcel(Path excelFile) {
        try (Workbook workbook = WorkbookFactory.create(Files.newInputStream(excelFile))) {
            if (workbook.getNumberOfSheets() == 0) {
                throw new BadRequestException("Excel file contains no sheets");
            }
        } catch (Exception e) {
            throw new BadRequestException("Invalid Excel file format");
        }
    }

    private boolean isExcel(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (contentType.contains("spreadsheetml") || contentType.contains("excel"));
    }

    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
        } catch (IOException ex) {
            logger.error("Failed to delete file: " + filePath, ex);
        }
    }

    public Resource loadFile(String filePath) {
        Path path = Paths.get(filePath);
        try {
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageException("Could not read file: " + filePath);
            }
        } catch (MalformedURLException e) {
            throw new StorageException("Invalid file path: " + filePath);
        }
    }

    // ✅ Bulk validation helper
    public void validateFiles(List<MultipartFile> files) {
        for (MultipartFile file : files) {
            validateFile(file);
        }
    }

    // ✅ Sanitization helpers
    public static String sanitizeFileName(String fileName) {
        if (fileName == null) return "file";
        fileName = fileName.replace("..", "");
        return fileName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
    }

    public static String sanitizePathComponent(String component) {
        if (component == null) return "";
        return component.replaceAll("[^a-zA-Z0-9/\\-_]", "_");
    }

    public String ensureDownloadUrl(String pathOrUrl) {
        if (pathOrUrl == null) return null;
        if (pathOrUrl.startsWith("http")) return pathOrUrl;

        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/files/download")
                .queryParam("path", pathOrUrl)
                .toUriString();
    }

    // Helper method to get configured max file size
    public long getMaxFileSize() {
        return maxFileSize;
    }

    // Helper method to get max file size in MB for display
    public String getMaxFileSizeInMB() {
        return (maxFileSize / (1024 * 1024)) + " MB";
    }
}