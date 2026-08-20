package com.aipintuan.voiceagent.repository;

import com.aipintuan.voiceagent.entity.SessionStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionStateRepository extends JpaRepository<SessionStateEntity, String> {
}