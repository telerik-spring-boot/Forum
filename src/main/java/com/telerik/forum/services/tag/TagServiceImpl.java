package com.telerik.forum.services.tag;

import com.telerik.forum.exceptions.InvalidUserInputException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.models.post.Tag;
import com.telerik.forum.models.post.Post;
import com.telerik.forum.models.user.User;
import com.telerik.forum.repositories.admin.AdminDetailsRepository;
import com.telerik.forum.repositories.tag.TagRepository;
import com.telerik.forum.repositories.post.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static com.telerik.forum.services.post.PostServiceImpl.BLOCKED_ACCOUNT_MESSAGE;

@Service
public class TagServiceImpl implements TagService {

    private static final String UNAUTHORIZED_MESSAGE = "You are not allowed to edit this post's tags.";
    private static final String NON_MATCHING_OLD_AND_NEW_TAGS = "You have not entered the same number of old and new tags.";
    private final TagRepository tagRepository;
    private final PostRepository postRepository;
    private final AdminDetailsRepository adminDetailsRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository, PostRepository postRepository,
                          AdminDetailsRepository adminDetailsRepository) {
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
        this.adminDetailsRepository = adminDetailsRepository;
    }

    @Override
    public void addTagToPost(int postId, String tags, User user) {
        Post post = postRepository.getPostWithTagsById(postId);
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
        checkPermissions(post, user);

        List<String> oldTagList = extractTags(oldTags);
        List<String> newTagList = extractTags(newTags);

        if (oldTagList.size() != newTagList.size()) {
            throw new InvalidUserInputException(NON_MATCHING_OLD_AND_NEW_TAGS);
        }

        for (int i = 0; i < oldTagList.size(); i++) {

            Tag oldTag = tagRepository.findByName(oldTagList.get(i));

            if (post.getTags().remove(oldTag)) {

                addTagToPostHelper(newTagList.get(i), post);

            }

        }

        postRepository.update(post);

    }

    @Override
    public void deleteTagFromPost(int postId, String tags, User user) {
        Post post = postRepository.getPostWithTagsById(postId);
        checkPermissions(post, user);

        List<String> tagList = extractTags(tags);

        for (String tagToRemove : tagList) {

            removeTagFromPostHelper(tagToRemove, post);
        }

        postRepository.update(post);
    }


    private void checkPermissions(Post post, User user) {

        boolean isAdmin = adminDetailsRepository.getByUserId(user.getId()) != null;
        if (!(post.getUser().equals(user) || isAdmin)) {
            throw new UnauthorizedOperationException(UNAUTHORIZED_MESSAGE);
        }
        if (user.isBlocked()) {
            throw new UnauthorizedOperationException(BLOCKED_ACCOUNT_MESSAGE);
        }

    }

    private static List<String> extractTags(String tags) {
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .toList();
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

    private void removeTagFromPostHelper(String tagToRemove, Post post) {
        Tag tag = tagRepository.findByName(tagToRemove);

        if (tag != null) {
            post.getTags().remove(tag);
        }
    }


}
