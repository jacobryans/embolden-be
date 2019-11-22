package com.lambdaschool.tempEC.services;

import com.lambdaschool.tempEC.models.Conversation;

import java.util.List;

public interface ConversationService {
    Conversation save(Conversation conversation);
    Conversation findById(long id);
    void delete(long id);
    List<Conversation> findAll();
}
