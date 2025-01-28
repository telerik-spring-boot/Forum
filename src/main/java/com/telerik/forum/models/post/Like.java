package com.telerik.forum.models.post;

import com.telerik.forum.models.user.User;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "likes")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="like_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "reaction", nullable = false)
    private int reaction;

    public Like() {
    }

    public Like(int id, Post post, User user, int reaction) {
        this.id = id;
        this.post = post;
        this.user = user;
        this.reaction = reaction;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getReaction() {
        return reaction;
    }

    public void setReaction(int reaction) {
        this.reaction = reaction;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Like like = (Like) o;
        return reaction == like.reaction && Objects.equals(post, like.post) && Objects.equals(user, like.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(post, user, reaction);
    }
}
