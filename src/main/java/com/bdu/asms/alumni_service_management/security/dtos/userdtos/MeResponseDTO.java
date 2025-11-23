package com.bdu.asms.alumni_service_management.security.dtos.userdtos;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeResponseDTO {
    private UserProfileDTO user;
    private StaffProfileDTO staff;
    private AppointmentDTO currentAppointment;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserProfileDTO {
        private String publicId;
        private String userName;
        private List<String> roles;       // Role names only (not every authority/permission)
        private List<String> authorities; // Optional: permissions/authorities if you want
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StaffProfileDTO {
        private String publicId;
        private String fName;
        private String mName;
        private String lName;
        private String fullName;
        private OrgUnitBriefDTO organizationalUnit; // nullable
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppointmentDTO {
        private String publicId;
        private String status;
        private LocalDate startDate;
        private LocalDate endDate;
        private PositionBriefDTO position;
        private OrgUnitBriefDTO organizationalUnit;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PositionBriefDTO {
        private String publicId;
        private String title;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrgUnitBriefDTO {
        private String publicId;
        private String name;
        private String unitTypeName;
    }
}
