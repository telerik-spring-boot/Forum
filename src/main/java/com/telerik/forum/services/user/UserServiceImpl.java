package com.telerik.forum.services.user;

import com.telerik.forum.exceptions.DuplicateEntityException;
import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.models.post.Comment;
import com.telerik.forum.models.post.Like;
import com.telerik.forum.models.post.Post;
import com.telerik.forum.models.user.User;
import com.telerik.forum.models.filters.FilterCommentOptions;
import com.telerik.forum.models.filters.FilterPostOptions;
import com.telerik.forum.repositories.comment.CommentRepository;
import com.telerik.forum.repositories.post.PostRepository;
import com.telerik.forum.repositories.role.RoleRepository;
import com.telerik.forum.repositories.user.UserRepository;
import com.telerik.forum.repositories.utilities.SortingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final String PERMISSION_ERROR_MESSAGE = "You do not have permission to perform this action";
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PostRepository postRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }


    @Override
    public User getById(int id) {
        User user = userRepository.getById(id);

        if (user == null) {
            throw new EntityNotFoundException("User", "id", id);
        }

        return user;
    }

    @Override
    public User getByIdWithPosts(int id, FilterPostOptions options) {
        User user = userRepository.getById(id);

        options.getSortBy().ifPresent(SortingHelper::validateSortByFieldPost);

        options.getSortOrder().ifPresent(SortingHelper::validateSortOrderField);

        if (user == null) {
            throw new EntityNotFoundException("User", "id", id);
        }

        List<Post> posts = postRepository.getPostsWithCommentsByUserId(id, options);

        filterByLikes(posts, options);

        user.setPosts(posts);

        return user;
    }


    @Override
    public User getByIdWithComments(int id, FilterCommentOptions options) {
        User user = userRepository.getById(id);

        options.getSortBy().ifPresent(SortingHelper::validateSortByFieldComment);

        options.getSortOrder().ifPresent(SortingHelper::validateSortOrderField);

        if (user == null) {
            throw new EntityNotFoundException("User", "id", id);
        }

        List<Comment> comments = commentRepository.getByUserId(id, options);

        user.setComments(comments);

        return user;
    }

    @Override
    public User getByEmail(String email) {
        User user = userRepository.getByEmail(email);

        if (user == null) {
            throw new EntityNotFoundException("User", "email", email);
        }

        return user;
    }

    @Override
    public User getByUsername(String username) {
        User user = userRepository.getByUsername(username);

        if (user == null) {
            throw new EntityNotFoundException("User", "username", username);
        }

        return user;
    }

    @Override
    public User getByUsernameWithRoles(String username) {
        User user = userRepository.getByUsernameWithRoles(username);

        if (user == null) {
            throw new EntityNotFoundException("User", "username", username);
        }

        return user;
    }

    @Override
    public User getByFirstName(String firstName) {
        User user = userRepository.getByFirstName(firstName);

        if (user == null) {
            throw new EntityNotFoundException("User", "firstName", firstName);
        }

        return user;
    }

    @Override
    public void create(User userInput) {
        boolean emailAlreadyExists = userRepository.getByEmail(userInput.getEmailAddress()) != null;
        boolean usernameAlreadyExists = userRepository.getByUsername(userInput.getUsername()) != null;

        if (emailAlreadyExists) {
            throw new DuplicateEntityException("User", "email", userInput.getEmailAddress());
        }

        if (usernameAlreadyExists) {
            throw new DuplicateEntityException("User", "username", userInput.getUsername());
        }

        userInput.addRole(roleRepository.findByName("USER"));

        userRepository.create(userInput);
    }

    @Override
    public void update(User userInput, int requestUserId) {
        authorization(userInput, requestUserId);

        User userToUpdate = userRepository.getByEmail(userInput.getEmailAddress());

        if (userToUpdate != null && userToUpdate.getId() != userInput.getId()) {
            throw new DuplicateEntityException("User", "email", userInput.getEmailAddress());
        }

        if (userToUpdate != null && !userToUpdate.getUsername().equals(userInput.getUsername())) {
            throw new UnauthorizedOperationException(PERMISSION_ERROR_MESSAGE);
        }

        userRepository.update(userInput);
    }

    @Override
    public void delete(int id, int requestUserId) {
        authorization(userRepository.getById(id), requestUserId);

        userRepository.delete(id);
    }

    private void authorization(User userInput, int requestUserId) {
        User user = userRepository.getByIdWithRoles(requestUserId);
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("ADMIN"));

        if (user.isBlocked() ||
                (!isAdmin && requestUserId != userInput.getId())) {
            throw new UnauthorizedOperationException(PERMISSION_ERROR_MESSAGE);
        }
    }

    private void filterByLikes(List<Post> posts, FilterPostOptions options) {
        List<Post> postsToDelete = new ArrayList<>();

        for (Post post : posts) {
            int totalLikes = post.getLikes().stream()
                    .map(Like::getReaction)
                    .mapToInt(Integer::intValue)
                    .sum();

            options.getMinLikes().ifPresent(minLikes -> {
                if (totalLikes < minLikes) {
                    postsToDelete.add(post);
                }
            });

            options.getMaxLikes().ifPresent(maxLikes -> {
                if (totalLikes > maxLikes) {
                    postsToDelete.add(post);
                }
            });
        }
        if (!postsToDelete.isEmpty()) {
            posts.removeAll(postsToDelete);
        }
    }
}
