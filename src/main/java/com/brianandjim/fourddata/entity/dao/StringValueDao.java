package com.brianandjim.fourddata.entity.dao;

import com.brianandjim.fourddata.entity.models.StringValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StringValueDao extends JpaRepository<StringValue, Long> {
    StringValue findFirstByNodeValueSpace_NodeSpaceId(Long nodeValeSpaceId);
}
