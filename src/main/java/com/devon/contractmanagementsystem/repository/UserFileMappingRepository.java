package com.devon.contractmanagementsystem.repository;

import com.devon.contractmanagementsystem.model.UserFileMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
public interface UserFileMappingRepository extends JpaRepository<UserFileMapping, Integer> {

    List<UserFileMapping> findByUserId(int userId);

    UserFileMapping findByContentId(String id);

    UserFileMapping findByUserIdAndContentId(@Param("userId") int userId, @Param("contentId") String contentId);
}