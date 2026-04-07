package hexlet.code.specification;

import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.model.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

/**
 * Specification builder for dynamic task filtering.
 * Supports filtering by title (like), assignee, status and labels.
 */
@Component
public class TaskSpecification {

    public Specification<Task> build(TaskParamsDTO params) {
        return withTitleCont(params.titleCont())
                .and(withAssigneeId(params.assigneeId()))
                .and(withStatus(params.status()))
                .and(withLabelId(params.labelId()));
    }

    private Specification<Task> withTitleCont(String titleCont) {
        return ((root, query, cb) ->
                titleCont == null
                ? null
                : cb.like(cb.lower(root.get("name")), "%" + titleCont.toLowerCase() + "%")
        );
    }

    private Specification<Task> withAssigneeId(Long assigneeId) {
        return (root, query, cb) -> assigneeId == null
                ? null
                : cb.equal(root.get("assignee").get("id"), assigneeId);
    }

    private Specification<Task> withStatus(String status) {
        return (root, query, cb) -> status == null
                ? null
                : cb.equal(root.get("taskStatus").get("slug"), status);
    }

    private Specification<Task> withLabelId(Long labelId) {
        return (root, query, cb) -> labelId == null
                ? null
                : cb.equal(root.join("labels").get("id"), labelId);
    }
}
