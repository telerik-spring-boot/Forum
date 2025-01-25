package com.telerik.forum.services;

import com.telerik.forum.models.Post;
import com.telerik.forum.models.User;

public interface LikeService {

    void likePost(Post post, User user);

    void dislikePost(Post post, User user);

}
