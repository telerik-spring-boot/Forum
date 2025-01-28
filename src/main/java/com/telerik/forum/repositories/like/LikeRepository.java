package com.telerik.forum.repositories.like;

import com.telerik.forum.models.post.Like;

public interface LikeRepository {

    //Like getLikeByPostAndUserId(int postId, int userId);

    void create(Like like);

    void update(Like like);

    void delete(int likeId);
}
