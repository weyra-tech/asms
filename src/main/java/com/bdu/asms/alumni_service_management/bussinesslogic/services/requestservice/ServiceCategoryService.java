package com.bdu.asms.alumni_service_management.bussinesslogic.services.requestservice;

import com.bdu.asms.alumni_service_management.dto.category.ServiceCategoryCreateDTO;
import com.bdu.asms.alumni_service_management.dto.category.ServiceCategoryResponseDTO;
import com.bdu.asms.alumni_service_management.dto.category.ServiceCategoryUpdateDTO;
import com.bdu.asms.alumni_service_management.bussinesslogic.entities.service_category.ServiceCategory;
import com.bdu.asms.alumni_service_management.repository.ServiceCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceCategoryService {

    private final ServiceCategoryRepository categoryRepository;

    public ServiceCategoryResponseDTO createCategory(ServiceCategoryCreateDTO dto) {
        if (categoryRepository.findByName(dto.getName()).isPresent()) {
            throw new RuntimeException("Category with this name already exists");
        }

        ServiceCategory category = new ServiceCategory();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setBaseFee(dto.getBaseFee());
        category.setActive(true);

        ServiceCategory saved = categoryRepository.save(category);
        return mapToResponseDTO(saved);
    }

    public List<ServiceCategoryResponseDTO> getAllActiveCategories() {
        return categoryRepository.findByIsActiveTrue().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ServiceCategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public ServiceCategoryResponseDTO updateCategory(Long id, ServiceCategoryUpdateDTO dto) {
        ServiceCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (dto.getName() != null)
            category.setName(dto.getName());
        if (dto.getDescription() != null)
            category.setDescription(dto.getDescription());
        if (dto.getBaseFee() != null)
            category.setBaseFee(dto.getBaseFee());
        if (dto.getIsActive() != null)
            category.setActive(dto.getIsActive());

        ServiceCategory saved = categoryRepository.save(category);
        return mapToResponseDTO(saved);
    }

    private ServiceCategoryResponseDTO mapToResponseDTO(ServiceCategory category) {
        ServiceCategoryResponseDTO dto = new ServiceCategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setBaseFee(category.getBaseFee());
        dto.setActive(category.isActive());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        return dto;
    }
}
