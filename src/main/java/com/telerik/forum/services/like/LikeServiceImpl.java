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

        checkForBlockedUser(user);

        Post post = postRepository.getPostWithLikesById(postId);

        if(post == null) {
            throw new EntityNotFoundException("Post", "id", postId);
        } // added


        // think of a way to get postsWithLikesForParticularUser rather than doing it in memory
        // user might have just 1post where the entire database is of 1million posts

        Like like = post.getLikes().stream()
                .filter(l -> l.getUser().getId() == user.getId())
                .findFirst()
                .orElse(null);


        if (like == null) {
            Like newLike = new Like();

            newLike.setPost(post);
            newLike.setUser(user);
            newLike.setReaction(1);

            likeRepository.create(newLike);
        } else if (like.getReaction() == -1) {

            like.setReaction(1);

            likeRepository.update(like);
        } else {
            likeRepository.delete(like.getId());
        }
    }

    @Override
    public void dislikePost(int postId, User user) {

        checkForBlockedUser(user);

        Post post = postRepository.getPostWithLikesById(postId);

        if(post == null){
            throw new EntityNotFoundException("Post", "id", postId);
        } // added

        Like dislike = post.getLikes().stream()
                .filter(l -> l.getUser().getId() == user.getId())
                .findFirst()
                .orElse(null);


        if (dislike == null) {
            Like newDislike = new Like();

            newDislike.setPost(post);
            newDislike.setUser(user);
            newDislike.setReaction(-1);

            likeRepository.create(newDislike);
        } else if (dislike.getReaction() == 1) {
            // methods are exactly the same the only difference are the variables in the reaction fields
            // think of a way how to extract all into 1 method and just call them based on reactions
            dislike.setReaction(-1);

            likeRepository.update(dislike);
        } else {

            likeRepository.delete(dislike.getId());

        }

    }

    private void checkForBlockedUser(User user) {
        if (user.isBlocked()) {
            throw new UnauthorizedOperationException(BLOCKED_ACCOUNT_MESSAGE);
        }
    }
}
