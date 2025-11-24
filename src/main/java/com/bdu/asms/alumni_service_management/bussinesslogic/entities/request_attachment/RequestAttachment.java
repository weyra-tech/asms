package com.bdu.asms.alumni_service_management.bussinesslogic.entities.request_attachment;

import com.bdu.asms.alumni_service_management.bussinesslogic.entities.BaseEntity;
import com.bdu.asms.alumni_service_management.bussinesslogic.entities.service_request.ServiceRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity

@Table(name = "request_attachments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestAttachment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private ServiceRequest serviceRequest;



    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "file_type", nullable = false)
    private String fileType; // e.g., "image/jpeg", "application/pdf"

    @Enumerated(EnumType.STRING)
    @Column(name = "attachment_type", nullable = false)
    private AttachmentType attachmentType;

    public enum AttachmentType {
        COST_SHARING_LETTER,
        SPONSORSHIP_LETTER,
        OTHER_DOCUMENT,
        PAYMENT_RECEIPT
    }
}
