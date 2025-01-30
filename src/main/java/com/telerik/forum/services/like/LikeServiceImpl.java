package com.telerik.forum.services.like;

import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.models.post.Like;
import com.telerik.forum.models.post.Post;
import com.telerik.forum.models.user.User;
import com.telerik.forum.repositories.like.LikeRepository;
import com.telerik.forum.repositories.post.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.telerik.forum.services.post.PostServiceImpl.BLOCKED_ACCOUNT_MESSAGE;

@Service
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    @Autowired
    public LikeServiceImpl(LikeRepository likeRepository, PostRepository postRepository) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
    }

    @Override
    public void likePost(int postId, User user) {

        reactToPost(postId, user, 1);
    }

    @Override
    public void dislikePost(int postId, User user) {

        reactToPost(postId, user, -1);

    }

    private void reactToPost(int postId, User user, int reaction) {
        checkForBlockedUser(user);

        Post post = postRepository.getPostWithLikesById(postId);

        if (post == null) {
            throw new EntityNotFoundException("Post", "id", postId);
        }

        Like like = post.getLikes().stream()
                .filter(l -> l.getUser().getId() == user.getId())
                .findFirst()
                .orElse(null);


        if (like == null) {
            Like newLike = new Like();

            newLike.setPost(post);
            newLike.setUser(user);
            newLike.setReaction(reaction);

            likeRepository.create(newLike);
        } else if (like.getReaction() == -1 * reaction) {

            like.setReaction(reaction);

            likeRepository.update(like);
        } else {

            likeRepository.delete(like.getId());

        }
    }

    private void checkForBlockedUser(User user) {
        if (user.isBlocked()) {
            throw new UnauthorizedOperationException(BLOCKED_ACCOUNT_MESSAGE);
        }
    }
}
