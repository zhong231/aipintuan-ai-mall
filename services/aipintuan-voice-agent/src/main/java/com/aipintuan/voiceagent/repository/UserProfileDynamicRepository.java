package com.aipintuan.voiceagent.repository;

import com.aipintuan.voiceagent.entity.UserProfileDynamicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileDynamicRepository extends JpaRepository<UserProfileDynamicEntity, Long> {
}