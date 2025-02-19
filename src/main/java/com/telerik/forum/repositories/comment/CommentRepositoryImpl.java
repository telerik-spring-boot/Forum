package com.telerik.forum.repositories.comment;


import com.telerik.forum.models.post.Comment;
import com.telerik.forum.models.filters.FilterCommentOptions;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.telerik.forum.repositories.utilities.SortingHelper.sortingHelper;


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

    @Override
    public List<Comment> getByUserId(int id, FilterCommentOptions options) {


        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<Comment> criteriaQuery = criteriaBuilder.createQuery(Comment.class);

            Root<Comment> root = criteriaQuery.from(Comment.class);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("user").get("id"), id));

            options.getContent().ifPresent(content -> {
                predicates.add(criteriaBuilder.like(root.get("commentContent"), "%" + content + "%"));
            });

            criteriaQuery.where(predicates.toArray(new Predicate[0]));

            sortingHelper(criteriaBuilder, root, criteriaQuery, options);

            Query<Comment> query = session.createQuery(criteriaQuery);

            return query.list();
        }

    }
//
//    @Override
//    public List<Comment> getByPostId(int postId) {
//        try (Session session = sessionFactory.openSession()) {
//            Query<Comment> query = session.createQuery("from Comment where post.id = :postId", Comment.class);
//            query.setParameter("postId", postId);
//            return query.list();
//        }
//    }

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
