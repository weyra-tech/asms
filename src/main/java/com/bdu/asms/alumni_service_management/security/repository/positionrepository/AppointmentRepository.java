package com.bdu.asms.alumni_service_management.security.repository.positionrepository;



import com.bdu.asms.alumni_service_management.security.entities.Appointment;
import com.bdu.asms.alumni_service_management.security.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByStaffPublicId(String staffPublicId);
    List<Appointment> findByOrganizationalUnitPublicId(String organizationalUnitPublicId);
    Optional<Appointment> findByPublicId(String publicId);

  /*  @Query("SELECT a FROM Appointment a WHERE a.staff.publicId = :staffPublicId AND a.status = 'ACTIVE'")
    Optional<Appointment> findActiveAppointmentByStaffPublicId(@Param("staffPublicId") String staffPublicId);
*/
    Optional<Appointment> findByStaff_PublicIdAndAppointmentStatus(String publicId, AppointmentStatus status);

}