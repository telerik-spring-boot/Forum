package com.telerik.forum.repositories.like;

import com.telerik.forum.models.post.Like;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class LikeRepositoryImpl implements LikeRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public LikeRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }



    @Override
    public void create(Like like) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(like);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Like like) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(like);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(int likeId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Like like = session.get(Like.class, likeId);
            session.remove(like);

            session.getTransaction().commit();
        }
    }
}
