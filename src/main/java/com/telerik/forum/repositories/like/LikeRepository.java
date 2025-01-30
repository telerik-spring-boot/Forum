package com.telerik.forum.repositories.like;

import com.telerik.forum.models.post.Like;

public interface LikeRepository {

    void create(Like like);

    void update(Like like);

    void delete(int likeId);
}
