package com.telerik.forum.repositories;

import com.telerik.forum.exceptions.EntityNotFoundException;
import com.telerik.forum.models.Post;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class PostRepositoryImpl implements PostRepository {

    private final SessionFactory sessionFactory;

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
