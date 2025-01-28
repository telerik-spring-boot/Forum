package com.telerik.forum.services.like;

import com.telerik.forum.models.user.User;

public interface LikeService {

    void likePost(int postId, User user);

    void dislikePost(int postId, User user);

}
