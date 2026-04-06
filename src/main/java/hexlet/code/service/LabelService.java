package hexlet.code.service;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LabelService {

    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    private static final String LABEL_NOT_FOUND_MESSAGE = "Label with id %d not found";

    public Page<LabelDTO> getAll(Pageable pageable) {
        return labelRepository.findAll(pageable)
                .map(labelMapper::map);
    }

    public LabelDTO findById(Long id) {
        return labelMapper.map(findLabelById(id));
    }

    @Transactional
    public LabelDTO create(LabelCreateDTO labelData) {
        var label = labelMapper.map(labelData);

        log.info("Label created: {}", label.getName());
        return labelMapper.map(labelRepository.save(label));
    }

    @Transactional
    public LabelDTO update(LabelUpdateDTO labelData, Long id) {
        var label = findLabelById(id);
        labelMapper.update(labelData, label);

        log.info("Label with id {} updated", id);
        return labelMapper.map(labelRepository.save(label));
    }

    @Transactional
    public void delete(Long id) {
        labelRepository.delete(findLabelById(id));
        log.info("Label with id {} deleted", id);
    }

    private Label findLabelById(Long id) {
        return labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(LABEL_NOT_FOUND_MESSAGE.formatted(id)));
    }
}

