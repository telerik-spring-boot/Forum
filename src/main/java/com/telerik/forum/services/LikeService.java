package com.telerik.forum.services;

import com.telerik.forum.models.Post;
import com.telerik.forum.models.User;

public interface LikeService {

    void likePost(int postId, User user);

    void dislikePost(int postId, User user);

}
