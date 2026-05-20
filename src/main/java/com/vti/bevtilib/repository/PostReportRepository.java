package com.vti.bevtilib.repository;

import com.vti.bevtilib.model.PostReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostReportRepository extends JpaRepository<PostReport, Long> {
    boolean existsByPostIdAndUserUserId(Long postId, String userId);
}
