package com.telerik.forum.repositories;

import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.models.Post;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostRepositoryImpl implements PostRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public PostRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Post> getAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<Post> query = session.createQuery("from Post", Post.class);
            return query.list();
        }
    }

    @Override
    public Post getById(int id) {
        try(Session session = sessionFactory.openSession()) {
            Post post = session.get(Post.class, id);

            if (post==null) {
                throw new EntityNotFoundException("Post", "id", id);
            }
            return post;
        }
    }

    @Override
    public List<Post> getbyUserId(int userId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Post> query = session.createQuery("from Post where user.id = :userId  ", Post.class);
            query.setParameter("userId", userId);
            return query.list();
        }
    }

    @Override
    public void create(Post post) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(post);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Post post) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(post);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(int id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Post post = session.get(Post.class, id);
            if (post==null) {
                throw new EntityNotFoundException("Post", "id", id);
            }
            session.remove(post);
            session.getTransaction().commit();

        }

    }
}
