package com.telerik.forum.services;

import com.telerik.forum.models.User;

public interface TagService {

    void addTagToPost(int postId, String tags, User user);

    void updateTagFromPost(int postId, String oldTags, String newTags, User user);

    void deleteTagFromPost(int postId, String tags, User user);
}
