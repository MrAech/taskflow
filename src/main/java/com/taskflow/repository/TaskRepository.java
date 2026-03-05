package com.taskflow.repository;

import com.taskflow.model.Task;
import com.taskflow.model.User;
import com.taskflow.model.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAssignedUser(User user);

    List<Task> findByStatis(TaskStatus status);

    List<Task> findByStatusIn(List<TaskStatus> statuses);

    Page<Task> findAll(Pageable pageable);

    @Query("SELECT t FROM Task t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Task> searchByKeyword(@Param("q") String keyword);

    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId")
    List<Task> findByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT t FROM Task t WHERE t.dueDate < :now AND t.status != 'COMPLETED' AND t.status != 'CANCELLED'")
    List<Task> findOverdueTasks(@Param("now") LocalDateTime now);

    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.comments WHERE t.id IN :ids")
    List<Task> findByIdsWithComments(@Param("ids") List<Long> ids);
}
