package com.telerik.forum.repositories;

import com.telerik.forum.models.Like;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
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
    public Like getLikeByPostAndUserId(int postId, int userId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Like> query = session.createQuery(
                    "FROM Like l WHERE l.post.id = :postId and l.user.id = :userId",
                    Like.class
            );
            query.setParameter("postId", postId);
            query.setParameter("userId", userId);
            return query.uniqueResult();
        }
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
