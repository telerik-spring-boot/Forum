package com.telerik.forum.services;

import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.models.Tag;
import com.telerik.forum.models.Post;
import com.telerik.forum.models.User;
import com.telerik.forum.repositories.AdminDetailsRepository;
import com.telerik.forum.repositories.TagRepository;
import com.telerik.forum.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static com.telerik.forum.services.PostServiceImpl.BLOCKED_ACCOUNT_MESSAGE;

@Service
public class TagServiceImpl implements TagService {

    private static final String UNAUTHORIZED_MESSAGE = "You are not allowed to edit this post's tags.";
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

        addTagsToPostHelper(tagList, post);

        postRepository.update(post);

    }




    @Override
    public void updateTagFromPost(int postId, String oldTags,String newTags, User user) {
        Post post = postRepository.getPostWithTagsById(postId);
        checkPermissions(post, user);

        List<String> oldTagList = extractTags(oldTags);
        List<String> newTagList = extractTags(newTags);

        if(oldTagList.size() != newTagList.size()) {
            throw new UnauthorizedOperationException(UNAUTHORIZED_MESSAGE);
        }
    }

    @Override
    public void deleteTagFromPost(int postId, String tags, User user) {
        Post post = postRepository.getPostWithTagsById(postId);
        checkPermissions(post, user);

        List<String> tagList = extractTags(tags);

        deleteTagsFromPostHelper(tagList, post);

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

    private void addTagsToPostHelper(List<String> tagList, Post post) {
        for (String tagToAdd : tagList) {

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
    }

    private void deleteTagsFromPostHelper(List<String> tagList, Post post) {
        for (String tagToRemove : tagList) {

            Tag tag = tagRepository.findByName(tagToRemove);

            if (tag != null) {
                post.getTags().remove(tag);
            }
        }
    }
}
