package com.aipintuan.domain.tag.adapter.repository;

import com.aipintuan.domain.tag.model.entity.CrowdTagsJobEntity;

/**
 * @description 人群标签仓储接口
 * @create 2024-12-28 11:26
 */
public interface ITagRepository {

    CrowdTagsJobEntity queryCrowdTagsJobEntity(String tagId, String batchId);

    void addCrowdTagsUserId(String tagId, String userId);

    void updateCrowdTagsStatistics(String tagId, int count);

}
