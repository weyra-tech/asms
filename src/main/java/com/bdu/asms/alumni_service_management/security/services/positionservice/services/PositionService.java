package com.bdu.asms.alumni_service_management.security.services.positionservice.services;


import com.bdu.asms.alumni_service_management.security.dtos.positiondtos.PositionCreateDTO;
import com.bdu.asms.alumni_service_management.security.dtos.positiondtos.PositionResponseDTO;
import com.bdu.asms.alumni_service_management.security.dtos.positiondtos.PositionUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PositionService {
    PositionResponseDTO createPosition(PositionCreateDTO dto);
    PositionResponseDTO updatePosition(String publicId, PositionUpdateDTO dto);
    List<PositionResponseDTO> getAllPositions();

    PositionResponseDTO getPositionByPublicId(String publicId);
    Page<PositionResponseDTO> getPositions(Pageable pageable);
    void deletePosition(String publicId);
}

