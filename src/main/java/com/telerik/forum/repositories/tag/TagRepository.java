package com.telerik.forum.repositories.tag;

import com.telerik.forum.models.post.Tag;

import java.util.List;

public interface TagRepository {
    Tag findByName(String name);

    List<Tag> getAllTags();

    void addTag(Tag tag);

    void updateTag(Tag tag);

    void deleteTag(Tag tag);
}
