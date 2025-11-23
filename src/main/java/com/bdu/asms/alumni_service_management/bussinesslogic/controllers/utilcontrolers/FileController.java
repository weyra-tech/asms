package com.bdu.asms.alumni_service_management.bussinesslogic.controllers.utilcontrolers;


import com.bdu.asms.alumni_service_management.bussinesslogic.utils.commonutils.documentupload.FileStorageServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;



@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileStorageServiceUtil fileStorageService;

    // ✅ Upload a single file
    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('VIEW_FILES') ")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "general") String folder) {

        String path = fileStorageService.storeFile(file, folder);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "File uploaded successfully");
        response.put("path", path);
        response.put("fileName", file.getOriginalFilename());

        return ResponseEntity.ok(response);
    }

    // ✅ Upload multiple files
    @PostMapping("/upload/multiple")
    @PreAuthorize("hasAuthority('VIEW_FILES') ")
    public ResponseEntity<Map<String, Object>> uploadMultipleFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "folder", defaultValue = "general") String folder) {

        List<String> paths = new ArrayList<>();
        for (MultipartFile file : files) {
            paths.add(fileStorageService.storeFile(file, folder));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Files uploaded successfully");
        response.put("paths", paths);
        response.put("count", files.length);

        return ResponseEntity.ok(response);
    }

    // ✅ Download endpoint
    @GetMapping("/download")
    @PreAuthorize("hasAuthority('VIEW_FILES') ")
    public ResponseEntity<Resource> downloadFile(@RequestParam("path") String filePath) {
        Resource resource = fileStorageService.loadFile(filePath);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}


