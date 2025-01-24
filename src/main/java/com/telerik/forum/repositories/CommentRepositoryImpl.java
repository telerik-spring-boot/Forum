package com.telerik.forum.repositories;


import com.telerik.forum.models.Comment;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class CommentRepositoryImpl implements CommentRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public CommentRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    //    @Override
//    public List<Comment> getAll() {
//        try (Session session = sessionFactory.openSession()) {
//            Query<Comment> query = session.createQuery("from Comment", Comment.class);
//            return query.list();
//        }
//    }
//
    @Override
    public Comment getById(int id) {
        try (Session session = sessionFactory.openSession()) {

            return session.get(Comment.class, id);

        }
    }
//
    @Override
    public List<Comment> getByPostId(int postId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Comment> query = session.createQuery("from Comment where post.id = :postId", Comment.class);
            query.setParameter("postId", postId);
            return query.list();
        }
    }

    @Override
    public void create(Comment comment) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(comment);
            session.getTransaction().commit();
        }

    }

    @Override
    public void update(Comment comment) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(comment);
            session.getTransaction().commit();
        }

    }

    @Override
    public void delete(int commentId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Comment comment = session.get(Comment.class, commentId);
            session.remove(comment);

            session.getTransaction().commit();


        }

    }
}
