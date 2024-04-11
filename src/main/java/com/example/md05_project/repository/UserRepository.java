package com.example.md05_project.repository;

import com.example.md05_project.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UserRepository extends JpaRepository<User,Long> {
    User findByUsername(String username);
    Boolean existsByUsername(String username);
    @Modifying
    @Query(" update User u Set u.status=case when u.status=true then false else true end where u.id=?1")
    void changeStatus(Long id);

    @Modifying
    @Query(" update User u Set u.status=false where u.id=?1")
    void blockUser(Long id);
    Page<User>findAllByUsernameContainingIgnoreCase(Pageable pageable,String name);
}
