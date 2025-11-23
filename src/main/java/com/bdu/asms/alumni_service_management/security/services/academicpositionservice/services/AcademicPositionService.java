package com.bdu.asms.alumni_service_management.security.services.academicpositionservice.services;

import com.bdu.asms.alumni_service_management.security.entities.AcademicPosition;


import java.util.List;

public interface AcademicPositionService {

    AcademicPosition createPosition(AcademicPosition position);
    AcademicPosition updatePosition(String publicId, AcademicPosition updatedPosition);
    void deletePosition(String publicId);
    AcademicPosition getPositionByPublicId(String publicId);
    List<AcademicPosition> getAllPositions();
    List<AcademicPosition> getActivePositions();
}
