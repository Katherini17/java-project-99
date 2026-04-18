package hexlet.code.repository;

import hexlet.code.model.Task;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends
        JpaRepository<Task, Long>,
        JpaSpecificationExecutor<Task> {

    @Override
    @EntityGraph(attributePaths = {"taskStatus", "labels", "assignee"})
    Optional<Task> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"taskStatus", "labels", "assignee"})
    List<Task> findAll();

    boolean existsByAssigneeId(Long userId);
    boolean existsByTaskStatusId(Long statusId);
    boolean existsByLabelsId(Long labelId);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Task t WHERE t.id = :id")
    Optional<Task> findWithLockById(@Param("id") Long id);
}
