package com.telerik.forum.services.tag;

import com.telerik.forum.models.user.User;

public interface TagService {

    void addTagToPost(int postId, String tags, User user);

    void updateTagFromPost(int postId, String oldTags, String newTags, User user);

    void deleteTagFromPost(int postId, String tags, User user);
}
