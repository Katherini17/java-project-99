package hexlet.code.service;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LabelService {
    Page<LabelDTO> getAll(Pageable pageable);
    LabelDTO getById(Long id);
    LabelDTO create(LabelCreateDTO labelData);
    LabelDTO update(LabelUpdateDTO labelData, Long id);
    void delete(Long id);
}
