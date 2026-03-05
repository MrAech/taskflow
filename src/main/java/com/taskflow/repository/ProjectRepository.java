package com.taskflow.repository;

import com.taskflow.model.Project;
import com.taskflow.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByOwner(User owner, Sort sort);

    List<Project> findByMembersContaining(User user);
}
