package com.telerik.forum.repositories;

import com.telerik.forum.models.Tag;

import java.util.List;

public interface TagRepository {
    Tag findByName(String name);

    List<Tag> getAllTags();

    void addTag(Tag tag);

    void updateTag(Tag tag);

    void deleteTag(Tag tag);
}
