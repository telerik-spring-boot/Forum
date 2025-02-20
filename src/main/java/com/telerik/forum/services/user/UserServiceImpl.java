package com.telerik.forum.services.user;

import com.telerik.forum.exceptions.DuplicateEntityException;
import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.helpers.PostMapper;
import com.telerik.forum.models.dtos.PostCommentWrapper;
import com.telerik.forum.models.dtos.postDTOs.PostDisplayDTO;
import com.telerik.forum.models.dtos.userDTOs.UserOverviewPageDisplayDTO;
import com.telerik.forum.models.dtos.userDTOs.UserPostsPageDisplayDTO;
import com.telerik.forum.models.filters.Sortable;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final String PERMISSION_ERROR_MESSAGE = "You do not have permission to perform this action";
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostMapper postMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PostRepository postRepository, CommentRepository commentRepository, PostMapper postMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.postMapper = postMapper;
    }


    @Override
    public User getById(int id, User userRequest) {
        authorizationBlocked(userRequest);

        User user = userRepository.getById(id);

        if (user == null) {
            throw new EntityNotFoundException("User", "id", id);
        }

        return user;
    }

    @Override
    public UserPostsPageDisplayDTO getByIdWithPosts(int id, FilterPostOptions options, User userRequest, Pageable pageable) {
        authorizationBlocked(userRequest);

        User user = getUserValidationAndSortingValidation(id, options, true);

        Page<Post> postsPaged = filterByLikes(postRepository.getPostsWithCommentsByUserId(id, options, pageable), options);

        Page<PostDisplayDTO> posts = postsPaged.map(postMapper::postToPostDisplayDTO);

        for (int i = 0; i < postsPaged.getContent().size(); i++) {
            posts.getContent().get(i)
                    .setReaction(postsPaged.getContent().get(i).getLikes().stream()
                            .filter(like -> like.getUser().getId() == userRequest.getId())
                            .map(Like::getReaction).findFirst().orElse(0));
        }

        return new UserPostsPageDisplayDTO(user.getUsername(), user.getId(),
                posts);


    }


    @Override
    public User getByIdWithComments(int id, FilterCommentOptions options, User userRequest) {
        authorizationBlocked(userRequest);

        User user = getUserValidationAndSortingValidation(id, options, false);

        List<Comment> comments = commentRepository.getByUserId(id, options);

        user.setComments(comments);

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
    public UserOverviewPageDisplayDTO getByIdWithCommentsAndPosts(int id, FilterPostOptions postOptions, FilterCommentOptions commentOptions, User userRequest, Pageable pageable) {
        List<PostCommentWrapper> combinedList = new ArrayList<>();
        UserOverviewPageDisplayDTO user = new UserOverviewPageDisplayDTO();

        Page<Post> posts = postRepository.getPostsWithCommentsByUserId(id, postOptions, pageable);
        List<Comment> comments = commentRepository.getByUserId(id, commentOptions);

        posts.forEach(post -> combinedList.add(new PostCommentWrapper(postMapper.postToPostDisplayDTO(post))));
        comments.forEach(comment -> combinedList.add(new PostCommentWrapper(comment)));


        if (postOptions.getSortBy().isPresent()) {
            switch (postOptions.getSortBy().toString()) {
                case "content":
                    combinedList.sort((a, b) -> {
                        if (a.getPost() != null) {
                            if (b.getPost() != null) {
                                return b.getPost().getContent().compareTo(a.getPost().getContent());
                            } else
                                return b.getComment().getContent().compareTo(a.getPost().getContent());
                        } else {
                            if (b.getPost() != null) {
                                return b.getPost().getContent().compareTo(a.getComment().getContent());
                            } else
                                return b.getComment().getContent().compareTo(a.getComment().getContent());

                        }
                    });
                    break;
                case "createdAt":
                    combinedList.sort(Comparator.comparing(PostCommentWrapper::getCreatedAt));
                    break;
            }
        } else combinedList.sort(Comparator.comparing(PostCommentWrapper::getCreatedAt));

        user.setEntities(combinedList);
        user.setUsername(getById(id, userRequest).getUsername());
        user.setId(id);

        if (user.getUsername() == null) {
            throw new EntityNotFoundException("User", "id", id);
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
    public void update(User userInput, User userRequest) {
        authorization(userInput.getId(), userRequest);

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
    public void delete(int id, User userRequest) {
        authorization(id, userRequest);

        User userToDelete = userRepository.getById(id);

        if (userToDelete == null) {
            throw new EntityNotFoundException("User", "id", id);
        }

        userRepository.delete(id);
    }

    private void authorizationBlocked(User userRequest) {

        if (userRequest.isBlocked()) {
            throw new UnauthorizedOperationException(PERMISSION_ERROR_MESSAGE);
        }
    }

    private void authorization(int id, User userRequest) {

        boolean isAdmin = userRequest.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("ADMIN"));

        if (userRequest.isBlocked() ||
                (!isAdmin && userRequest.getId() != id)) {
            throw new UnauthorizedOperationException(PERMISSION_ERROR_MESSAGE);
        }
    }

    private Page<Post> filterByLikes(Page<Post> posts, FilterPostOptions options) {
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
//            posts.removeAll(postsToDelete);
            List<Post> filteredPosts = posts.getContent()
                    .stream()
                    .filter(post -> !postsToDelete.contains(post)) // Remove unwanted posts
                    .toList();

            return new PageImpl<>(filteredPosts, posts.getPageable(), filteredPosts.size());

        }
        return posts;
    }

    private <T extends Sortable> User getUserValidationAndSortingValidation(int id, T options, boolean isPost) {
        User user = userRepository.getById(id);

        if (isPost) {
            options.getSortBy().ifPresent(SortingHelper::validateSortByFieldPost);
        } else options.getSortBy().ifPresent(SortingHelper::validateSortByFieldComment);

        options.getSortOrder().ifPresent(SortingHelper::validateSortOrderField);

        if (user == null) {
            throw new EntityNotFoundException("User", "id", id);
        }
        return user;
    }
}
