package com.lambdaschool.tempEC.repository;

import com.lambdaschool.tempEC.models.Conversation;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

public interface ConversationRepository extends CrudRepository<Conversation, Long> {
    @Transactional
    @Modifying
    @Query(value = "DELETE from conversation WHERE conversation.created_date BETWEEN :startDate AND :endDate", nativeQuery = true)
    void deleteConvotask(@Param("startDate") Date startDate, @Param("endDate")Date endDate);
}
