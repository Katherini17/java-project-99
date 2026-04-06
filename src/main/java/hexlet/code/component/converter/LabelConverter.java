package hexlet.code.component.converter;

import hexlet.code.exception.UnprocessableEntityException;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LabelConverter {

    private final LabelRepository labelRepository;

    public Set<Label> toEntity(Set<Long> labelIds) {
        if (labelIds == null) {
            return new HashSet<>();
        }

        return Optional.of(labelIds)
                .map(labelRepository::findAllById)
                .map(HashSet::new)
                .filter(labels -> labels.size() == labelIds.size())
                .orElseThrow(() -> new UnprocessableEntityException("One or more labels not found"));
    }

    public Set<Long> toIds(Set<Label> labels) {
        return Optional.ofNullable(labels)
                .map(list -> list.stream()
                        .map(Label::getId)
                        .collect(Collectors.toSet()))
                .orElse(null);
    }
}
