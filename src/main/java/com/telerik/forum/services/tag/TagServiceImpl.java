package com.telerik.forum.services.tag;

import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.models.post.Post;
import com.telerik.forum.models.post.Tag;
import com.telerik.forum.models.user.User;
import com.telerik.forum.repositories.post.PostRepository;
import com.telerik.forum.repositories.tag.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.telerik.forum.services.post.PostServiceImpl.BLOCKED_ACCOUNT_MESSAGE;

@Service
public class TagServiceImpl implements TagService {

    private static final String UNAUTHORIZED_MESSAGE = "You are not allowed to edit this post's tags.";

    private final TagRepository tagRepository;
    private final PostRepository postRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository, PostRepository postRepository) {
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
    }

    private static boolean checkIfUserIsAdmin(User user) {
        return user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"));
    }

    private static List<String> extractTags(String tags) {
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .map(String::toLowerCase)
                .toList();
    }

    private static void removeTagFromPostHelper(List<String> tagNamesToRemove, Post post) {
        Set<String> tagNamesToRemoveSet = new HashSet<>(tagNamesToRemove);

        Set<Tag> tagsToRemove = post.getTags().stream()
                .filter(tag -> tagNamesToRemoveSet.contains(tag.getName()))
                .collect(Collectors.toSet());

        post.getTags().removeAll(tagsToRemove);
    }

    @Override
    public void addTagToPost(int postId, String tags, User user) {
        Post post = postRepository.getPostWithTagsById(postId);

        if (post == null) {
            throw new EntityNotFoundException("Post", "id", postId);
        }

        checkPermissions(post, user);

        List<String> tagList = extractTags(tags);

        for (String tagToAdd : tagList) {
            addTagToPostHelper(tagToAdd, post);
        }

        postRepository.update(post);

    }

    @Override
    public void updateTagFromPost(int postId, String oldTags, String newTags, User user) {
        Post post = postRepository.getPostWithTagsById(postId);

        if (post == null) {
            throw new EntityNotFoundException("Post", "id", postId);
        }

        checkPermissions(post, user);

        List<String> oldTagList = extractTags(oldTags);
        List<String> newTagList = extractTags(newTags);

        removeTagFromPostHelper(oldTagList, post);

        for (String tagName : newTagList) {
            addTagToPostHelper(tagName, post);
        }

        postRepository.update(post);

    }

    @Override
    public void deleteTagFromPost(int postId, String tags, User user) {
        Post post = postRepository.getPostWithTagsById(postId);

        if (post == null) {
            throw new EntityNotFoundException("Post", "id", postId);
        }

        checkPermissions(post, user);

        List<String> tagList = extractTags(tags);

        removeTagFromPostHelper(tagList, post);

        postRepository.update(post);
    }

    private void checkPermissions(Post post, User user) {

        boolean isAdmin = checkIfUserIsAdmin(user);

        if (!(post.getUser().equals(user) || isAdmin)) {
            throw new UnauthorizedOperationException(UNAUTHORIZED_MESSAGE);
        }

        if (user.isBlocked()) {
            throw new UnauthorizedOperationException(BLOCKED_ACCOUNT_MESSAGE);
        }

    }

    private void addTagToPostHelper(String tagToAdd, Post post) {

        Tag tag = tagRepository.findByName(tagToAdd);

        if (tag == null) {
            Tag newTag = new Tag();

            newTag.setName(tagToAdd);

            tagRepository.addTag(newTag);

            post.getTags().add(newTag);

        } else {
            post.getTags().add(tag);
        }
    }

    public void deleteOrphanedTags() {
        List<Tag> orphanedTags = tagRepository.getOrphanedTags();

        List<Tag> orphanedTagsCopy = new ArrayList<>(orphanedTags);

        orphanedTagsCopy.forEach(tagRepository::deleteTag);

    }

    @Scheduled(fixedRate = 86400000) // 24 hours
    public void scheduledDeleteOrphanedTags() {
        this.deleteOrphanedTags();
    }

}
