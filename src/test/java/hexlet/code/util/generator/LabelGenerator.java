package hexlet.code.util.generator;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.util.ModelUtils;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Getter
@Component
@RequiredArgsConstructor
public class LabelGenerator {

    private Model<Label> model;
    private Model<LabelCreateDTO> createDTO;
    private Model<LabelUpdateDTO> updateDTO;

    private final Faker faker;

    private static final AtomicLong COUNTER = new AtomicLong(1);

    @PostConstruct
    public void init() {
        model = buildModel();
        createDTO = buildCreateDTO();
        updateDTO = ModelUtils.buildUpdateModel(LabelUpdateDTO.class);

    }

    private Model<Label> buildModel() {
        return Instancio.of(Label.class)
                .ignore(Select.field(Label::getId))
                .ignore(Select.field(Label::getCreatedAt))
                .supply(
                        Select.field(Label::getName),
                        () -> "%s_%d".formatted(faker.commerce().department(), COUNTER.getAndIncrement())
                )
                .lenient()
                .toModel();
    }

    private Model<LabelCreateDTO> buildCreateDTO() {
        return Instancio.of(LabelCreateDTO.class)
                .supply(
                        Select.field(Label::getName),
                        () -> "%s_%d".formatted(faker.commerce().department(), COUNTER.getAndIncrement())
                )
                .lenient()
                .toModel();
    }
}
