package com.bdu.asms.alumni_service_management.security.services.positionservice.impl;

import com.bdu.asms.alumni_service_management.bussinesslogic.exceptions.BadRequestException;
import com.bdu.asms.alumni_service_management.bussinesslogic.exceptions.DuplicateResourceException;
import com.bdu.asms.alumni_service_management.bussinesslogic.exceptions.ResourceNotFoundException;
import com.bdu.asms.alumni_service_management.bussinesslogic.utils.commonutils.IdGeneratorService;
import com.bdu.asms.alumni_service_management.security.dtos.positiondtos.PositionCreateDTO;
import com.bdu.asms.alumni_service_management.security.dtos.positiondtos.PositionResponseDTO;
import com.bdu.asms.alumni_service_management.security.dtos.positiondtos.PositionUpdateDTO;
import com.bdu.asms.alumni_service_management.security.entities.Position;
import com.bdu.asms.alumni_service_management.security.mappers.positionmappers.PositionMapper;
import com.bdu.asms.alumni_service_management.security.repository.positionrepository.PositionRepository;
import com.bdu.asms.alumni_service_management.security.services.positionservice.services.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;
    private final PositionMapper positionMapper;
    private final IdGeneratorService idGeneratorService;

    @Override
    public PositionResponseDTO createPosition(final PositionCreateDTO dto) {
        // Validate required fields
        if (dto.getLevel() == null) {
            throw new BadRequestException("Position level is required");
        }

        // Normalize and ensure uniqueness for title
        final String normalizedTitle = normalizeTitle(dto.getTitle());
        positionRepository.findByNameIgnoreCase(normalizedTitle).ifPresent(p -> {
            throw new DuplicateResourceException("Position with title '" + normalizedTitle + "' already exists.");
        });

        // Map and persist
        Position position = positionMapper.toEntity(dto);
        position.setPublicId(idGeneratorService.generatePublicId("POS-"));
        position.setName(normalizedTitle);

        Position saved = positionRepository.save(position);
        return positionMapper.toResponseDTO(saved);
    }

    @Override
    public PositionResponseDTO updatePosition(final String publicId, final PositionUpdateDTO dto) {
        Position existing = positionRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Position not found with publicId: " + publicId));

        // Title (if provided): normalize and check uniqueness if changed
        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            String normalizedTitle = normalizeTitle(dto.getTitle());
            if (!normalizedTitle.equalsIgnoreCase(existing.getName())) {
                Optional<Position> conflict = positionRepository.findByNameIgnoreCase(normalizedTitle);
                if (conflict.isPresent() && !conflict.get().getPublicId().equals(existing.getPublicId())) {
                    throw new DuplicateResourceException("Position title already exists: " + normalizedTitle);
                }
                existing.setName(normalizedTitle);
            }
        }

        // Description
        if (dto.getDescription() != null) {
            existing.setDescription(dto.getDescription());
        }

        // Level (nullable means ignore)
        if (dto.getLevel() != null) {
            existing.setLevel(dto.getLevel());
        }



        Position updated = positionRepository.save(existing);
        return positionMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public PositionResponseDTO getPositionByPublicId(final String publicId) {
        Position position = positionRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Position not found with publicId: " + publicId));
        return positionMapper.toResponseDTO(position);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PositionResponseDTO> getAllPositions() {
        return positionRepository.findAll()
                .stream()
                .map(positionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PositionResponseDTO> getPositions(Pageable pageable) {
        return positionRepository.findAll(pageable).map(positionMapper::toResponseDTO);
    }

    @Override
    public void deletePosition(final String publicId) {
        Position position = positionRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Position not found with publicId: " + publicId));
        positionRepository.delete(position);
    }

    // ---------------- Helpers ----------------

    private String normalizeTitle(String title) {
        if (title == null) throw new BadRequestException("Position title is required");
        String t = title.trim();
        if (t.isBlank()) throw new BadRequestException("Position title must not be blank");
        return t;
    }
}