package com.telerik.forum.services;


import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.exceptions.UnauthorizedOperationException;
import com.telerik.forum.models.post.Post;
import com.telerik.forum.models.post.Tag;
import com.telerik.forum.models.user.Role;
import com.telerik.forum.models.user.User;
import com.telerik.forum.repositories.admin.AdminDetailsRepository;
import com.telerik.forum.repositories.post.PostRepository;
import com.telerik.forum.repositories.tag.TagRepository;
import com.telerik.forum.services.tag.TagServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.telerik.forum.DummyObjectProvider.*;

@ExtendWith(MockitoExtension.class)
public class TagServiceTests {

    @Mock
    private TagRepository mockTagRepository;

    @Mock
    private PostRepository mockPostRepository;

    @Mock
    private AdminDetailsRepository mockAdminDetailsRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    @Test
    public void addTagToPost_Should_Throw_When_PostDoesNotExist() {
        // Arrange
        Mockito.when(mockPostRepository.getPostWithTagsById(Mockito.anyInt()))
                .thenReturn(null);

        // Act, Assert
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> tagService.addTagToPost(1, "", createMockUser()));
    }

    @Test
    public void addTagToPost_Should_Throw_When_UserIsBlocked(){
        // Arrange
        User user = createMockUser();
        Post post = createMockPost();

        post.setUser(user);
        user.setBlocked(true);

        Mockito.when(mockPostRepository.getPostWithTagsById(Mockito.anyInt()))
                .thenReturn(post);


        // Act, Assert
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> tagService.addTagToPost(1, "", user));
    }

    @Test
    public void addTagToPost_Should_CreateTagAndAddTagToPost_When_TagDoesNotExist(){
        // Arrange
        User user = createMockUser();
        Post post = createMockPost();

        post.setUser(user);
        String tag = "epic";

        Mockito.when(mockPostRepository.getPostWithTagsById(Mockito.anyInt()))
                .thenReturn(post);

        Mockito.when(mockTagRepository.findByName(Mockito.anyString()))
                .thenReturn(null);

        // Act
        tagService.addTagToPost(post.getId(), tag, user);

        // Assert
        Mockito.verify(mockPostRepository, Mockito.times(1))
                .getPostWithTagsById(Mockito.anyInt());

        Mockito.verify(mockTagRepository, Mockito.times(1))
                .findByName(Mockito.anyString());

        Mockito.verify(mockTagRepository, Mockito.times(1))
                .addTag(Mockito.any(Tag.class));

        Mockito.verify(mockPostRepository, Mockito.times(1))
                .update(Mockito.any(Post.class));

        Assertions.assertEquals(1, post.getTags().size());
    }

    @Test
    public void addTagToPost_Should_AddTagToPost_When_TagExistsButNotForPost(){
        // Arrange
        User user = createMockUser();
        Post post = createMockPost();

        post.setUser(user);
        String tag = "MockTag";

        Mockito.when(mockPostRepository.getPostWithTagsById(Mockito.anyInt()))
                .thenReturn(post);

        Mockito.when(mockTagRepository.findByName("mocktag"))
                .thenReturn(createMockTag());

        // Act
        tagService.addTagToPost(post.getId(), tag, user);

        // Assert
        Mockito.verify(mockPostRepository, Mockito.times(1))
                .getPostWithTagsById(Mockito.anyInt());

        Mockito.verify(mockTagRepository, Mockito.times(1))
                .findByName("mocktag");

        Mockito.verify(mockPostRepository, Mockito.times(1))
                .update(Mockito.any());

        Assertions.assertEquals(1, post.getTags().size());
    }

    @Test
    public void updateTagFromPost_Should_Throw_When_PostDoesNotExist(){
        // Arrange
        Mockito.when(mockPostRepository.getPostWithTagsById(Mockito.anyInt()))
                .thenReturn(null);

        // Act, Assert
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> tagService.updateTagFromPost(1, "", "", createMockUser()));
    }

    @Test
    public void updateTagFromPost_Should_Throw_When_PostNotCreatedByUserAndUserNotAdmin(){
        // Arrange
        User user = createMockUser();
        Post post = createMockPost();

        user.setId(2);
        post.setUser(createMockUser());

        Mockito.when(mockPostRepository.getPostWithTagsById(Mockito.anyInt()))
                .thenReturn(post);

        // Act, Assert
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> tagService.updateTagFromPost(1, "", "", user));

        Mockito.verify(mockPostRepository, Mockito.times(1))
                .getPostWithTagsById(Mockito.anyInt());

    }


    @Test
    public void updateTagFromPost_Should_UpdateTags_When_EverythingIsValid(){
        // Arrange
        User user = createMockUser();
        Post post = createMockPost();
        Set<Tag> tags = new HashSet<>();

        post.setUser(user);
        tags.add(createMockTag());
        post.setTags(tags);

        Mockito.when(mockPostRepository.getPostWithTagsById(Mockito.anyInt()))
                .thenReturn(post);

        // Act
        tagService.updateTagFromPost(post.getId(), "mocktag,what", "pass,not", user);

        // Assert

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, post.getTags().size()),
                () -> Assertions.assertTrue(post.getTags().stream()
                        .anyMatch(tag -> tag.getName().equals("pass"))));

        Mockito.verify(mockPostRepository, Mockito.times(1))
                .getPostWithTagsById(Mockito.anyInt());

        Mockito.verify(mockPostRepository, Mockito.times(1))
                .update(Mockito.any());

    }

    @Test
    public void deleteTagFromPost_Should_Throw_When_PostDoesNotExist(){
        // Arrange
        Mockito.when(mockPostRepository.getPostWithTagsById(Mockito.anyInt()))
                .thenReturn(null);

        // Act, Assert
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> tagService.deleteTagFromPost(1, "", createMockUser()));
    }

    @Test
    public void deleteTagFromPost_Should_Throw_When_UserBlockedOrNoPermissions(){
        // Arrange
        User user = createMockUser();
        Post post = createMockPost();

        user.setBlocked(true);
        post.setUser(user);

        Mockito.when(mockPostRepository.getPostWithTagsById(Mockito.anyInt()))
                .thenReturn(post);

        // Act, Assert
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> tagService.deleteTagFromPost(post.getId(), "", user));

        Mockito.verify(mockPostRepository, Mockito.times(1))
                .getPostWithTagsById(Mockito.anyInt());


    }

    @Test
    public void deleteTagFromPost_Should_DeleteTags_When_EverythingIsValid(){
        // Arrange
        User user = createMockUser();
        Post post = createMockPost();
        Set<Tag> tags = new HashSet<>();
        Role admin = new Role();

        admin.setName("ADMIN");
        admin.setId(1);

        user.setId(2);
        user.addRole(admin);

        tags.add(new Tag(1, "pass"));
        tags.add(new Tag(2, "fast"));
        tags.add(new Tag(3, "epic"));

        post.setUser(createMockUser());
        post.setTags(tags);

        Mockito.when(mockPostRepository.getPostWithTagsById(Mockito.anyInt()))
                .thenReturn(post);

        // Act
        tagService.deleteTagFromPost(post.getId(), "pass,epic,notfound", user);

        // Assert
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, post.getTags().size()),
                () -> Assertions.assertTrue(post.getTags().stream().anyMatch(tag -> tag.getName().equalsIgnoreCase("fast"))));

        Mockito.verify(mockPostRepository, Mockito.times(1))
                .getPostWithTagsById(Mockito.anyInt());

        Mockito.verify(mockPostRepository, Mockito.times(1))
                .update(Mockito.any());

    }

    @Test
    public void deleteOrphanedTags_Should_DeleteTags_When_Called(){
        // Arrange
        List<Tag> tags = new ArrayList<>();
        Tag tag = new Tag(1, "pass");
        tags.add(tag);

        Mockito.when(mockTagRepository.getOrphanedTags())
                .thenReturn(tags);

        Mockito.doAnswer((invocation) -> tags.remove(tag)).when(mockTagRepository).deleteTag(tag);

        // Act, Assert
        Assertions.assertDoesNotThrow(() -> tagService.deleteOrphanedTags());

        Mockito.verify(mockTagRepository, Mockito.times(1))
                .getOrphanedTags();

        Mockito.verify(mockTagRepository, Mockito.times(1))
                .deleteTag(tag);

    }

    @Test
    public void scheduledDeleteOrphanedTags_Should_DeleteTags_When_TimeComes(){
        // Arrange
        List<Tag> tags = new ArrayList<>();
        Tag tag = new Tag(1, "pass");
        tags.add(tag);

        Mockito.when(mockTagRepository.getOrphanedTags())
                .thenReturn(tags);

        Mockito.doAnswer((invocation) -> tags.remove(tag)).when(mockTagRepository).deleteTag(tag);


        // Act, Assert
        Assertions.assertDoesNotThrow(() -> tagService.scheduledDeleteOrphanedTags());

        Mockito.verify(mockTagRepository, Mockito.times(1))
                .getOrphanedTags();

        Mockito.verify(mockTagRepository, Mockito.times(1))
                .deleteTag(tag);


    }

}
