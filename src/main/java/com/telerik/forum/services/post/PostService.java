package com.telerik.forum.services.post;

import com.telerik.forum.models.post.Post;
import com.telerik.forum.models.user.User;
import com.telerik.forum.models.filters.FilterPostOptions;

import java.util.List;

public interface PostService {

    List<Post> getAllPosts();

    List<Post> getAllPostsWithFilters(FilterPostOptions filterPostOptions);

    List<Post> getMostCommentedPosts(int limit);

    List<Post> getMostLikedPosts(int limit);

    //List<Post> getPostsByAuthor(User user);

    Post getById(int id);

    Post getByIdWithComments(int id);

    Post getByIdWithLikes(int id);

    Post getByIdWithCommentsAndLikesAndTags(int id);

    void createPost(Post post, User user);

    void updatePost(Post post, User user);

    void deletePost(int postId, User user);

}
