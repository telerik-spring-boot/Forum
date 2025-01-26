package com.telerik.forum.repositories;

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
            Query<Post> query = session.createQuery
                    ("SELECT DISTINCT p FROM Post p" +
                                    " LEFT JOIN FETCH p.comments " +
                                    " LEFT JOIN FETCH p.likes " +
                                    " LEFT JOIN FETCH p.tags ",
                            Post.class);

            return query.list();
        }
    }

    @Override
    public List<Post> getMostCommentedPosts(int limit) {
        try (Session session = sessionFactory.openSession()) {
            Query<Post> query = session.createQuery
                    ("SELECT DISTINCT p FROM Post p " +
                                    "LEFT JOIN FETCH p.comments " +
                                    "LEFT JOIN FETCH p.likes " +
                                    "LEFT JOIN FETCH p.tags " +
                                    "ORDER BY SIZE(p.comments) DESC",
                            Post.class);

            query.setMaxResults(limit);
            return query.list();
        }
    }

    @Override
    public List<Post> getMostLikedPosts(int limit) {
//        try (Session session = sessionFactory.openSession()) {
//            Query<Integer> postIdQuery = session.createQuery
//                    ("SELECT p.id " +
//                            "FROM Post p " +
//                            "LEFT JOIN p.likes l " +
//                            "GROUP BY p.id " +
//                            "ORDER BY SUM(COALESCE(l.reaction, 0)) DESC",
//                            Integer.class);
//
//            postIdQuery.setMaxResults(limit);
//            List<Integer> postIds = postIdQuery.list();
//
//            Query<Post> postQuery = session.createQuery(
//                    "SELECT DISTINCT p FROM Post p " +
//                            "LEFT JOIN FETCH p.comments " +
//                            "LEFT JOIN FETCH p.likes " +
//                            "LEFT JOIN FETCH p.tags " +
//                            "WHERE p.id IN :postIds",
//                    Post.class
//            );
//            postQuery.setParameter("postIds", postIds);
//
//            return postQuery.list();
//        }
        try (Session session = sessionFactory.openSession()) {
            Query<Post> query = session.createQuery
                    ("SELECT DISTINCT p FROM Post p " +
                                    "LEFT JOIN FETCH p.comments " +
                                    "LEFT JOIN FETCH p.likes l " +
                                    "LEFT JOIN FETCH p.tags " +
                                    "GROUP BY p.id " +
                                    "ORDER BY SUM(l.reaction) DESC",
                            Post.class);

            query.setMaxResults(limit);
            return query.list();
        }
    }

    @Override
    public Post getPostById(int postId) {

        try (Session session = sessionFactory.openSession()) {

            return session.get(Post.class, postId);

        }
    }

    @Override
    public Post getPostWithCommentsById(int postId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Post> query = session.createQuery
                    ("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.comments WHERE p.id=:postId ",
                            Post.class);
            query.setParameter("postId", postId);
            return query.uniqueResult();
        }
    }

    @Override
    public Post getPostWithLikesById(int postId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Post> query = session.createQuery
                    ("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.id=:postId ",
                            Post.class);
            query.setParameter("postId", postId);
            return query.uniqueResult();
        }
    }

    @Override
    public Post getPostWithTagsById(int postId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Post> query = session.createQuery
                    ("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.tags WHERE p.id=:postId ",
                            Post.class);
            query.setParameter("postId", postId);
            return query.uniqueResult();
        }
    }

    @Override
    public Post getPostWithCommentsAndLikesAndTagsById(int postId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Post> query = session.createQuery
                    ("SELECT DISTINCT p FROM Post p" +
                                    " LEFT JOIN FETCH p.comments " +
                                    " LEFT JOIN FETCH p.likes " +
                                    " LEFT JOIN FETCH p.tags " +
                                    "WHERE p.id=:postId",

                            Post.class);

            query.setParameter("postId", postId);
            return query.uniqueResult();
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
            session.remove(post);

            session.getTransaction().commit();

        }

    }
}
