package com.telerik.forum.repositories;

import com.telerik.forum.models.Like;
import com.telerik.forum.models.Post;
import com.telerik.forum.models.Tag;
import com.telerik.forum.models.filters.FilterPostOptions;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
                    ("SELECT DISTINCT p FROM Post p" +
                                    " LEFT JOIN FETCH p.comments " +
                                    " LEFT JOIN FETCH p.likes " +
                                    " LEFT JOIN FETCH p.tags " +
                                    "ORDER BY SIZE(p.comments) DESC",
                            Post.class);

            query.setMaxResults(limit);
            return query.list();
        }
    }

    @Override
    public List<Post> getMostLikedPosts(int limit) {
        try (Session session = sessionFactory.openSession()) {
            Query<Integer> postIdQuery = session.createQuery
                    ("SELECT p.id " +
                            "FROM Post p " +
                            "LEFT JOIN p.likes l " +
                            "GROUP BY p.id " +
                            "ORDER BY SUM(l.reaction) DESC",
                            Integer.class);

            postIdQuery.setMaxResults(limit);
            List<Integer> postIds = postIdQuery.list();

            Query<Post> postQuery = session.createQuery(
                    "SELECT DISTINCT p FROM Post p " +
                            "LEFT JOIN FETCH p.comments " +
                            "LEFT JOIN FETCH p.likes " +
                            "LEFT JOIN FETCH p.tags " +
                            "WHERE p.id IN :postIds",
                    Post.class
            );
            postQuery.setParameter("postIds", postIds);

            return postQuery.list();
        }
    }

    @Override
    public List<Post> getPostsWithCommentsByUserId(int userId, FilterPostOptions options){
        try(Session session = sessionFactory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<Post> criteriaQuery = criteriaBuilder.createQuery(Post.class);

            Root<Post> root = criteriaQuery.from(Post.class);

            Join<Post, Tag> tagsJoin = root.join("tags", JoinType.LEFT);

            Join<Post, Like> likesJoin = root.join("likes", JoinType.LEFT);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));

            options.getCreatorUsername().ifPresent(creatorUsername -> {
                predicates.add(criteriaBuilder.like(root.get("creatorUsername"), "%" + creatorUsername + "%"));
            });

            options.getTitle().ifPresent(title -> {
                predicates.add(criteriaBuilder.like(root.get("title"), "%" + title + "%"));
            });

            options.getContent().ifPresent(content -> {
                predicates.add(criteriaBuilder.like(root.get("content"), "%" + content + "%"));
            });

            options.getMinLikes().ifPresent(minLikes -> {
                Expression<Long> sumOfReactions = criteriaBuilder.sum(likesJoin.get("reaction"));
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(sumOfReactions, minLikes));
            });

            options.getMaxLikes().ifPresent(maxLikes -> {
                Expression<Long> sumOfReactions = criteriaBuilder.sum(likesJoin.get("reaction"));
                predicates.add(criteriaBuilder.lessThanOrEqualTo(sumOfReactions, maxLikes));
            });

            options.getTags().ifPresent(tagNames -> {
                List<Predicate> likePredicates = new ArrayList<>();
                for (String tagName : tagNames) {
                    likePredicates.add(criteriaBuilder.like(tagsJoin.get("name"), "%" + tagName + "%"));
                }
                predicates.add(criteriaBuilder.or(likePredicates.toArray(new Predicate[0])));
            });

            criteriaQuery.where(predicates.toArray(new Predicate[0]));

            criteriaQuery.groupBy(root.get("id"));

            options.getSortBy().ifPresent(sortBy -> {
                String sortOrder = options.getSortOrder().orElse("asc");

                Expression<?> expression = root.get(sortBy);

                if (sortBy.equalsIgnoreCase("likes")) {
                    expression = criteriaBuilder.sum(likesJoin.get("reaction"));
                }

                if (sortOrder.equalsIgnoreCase("desc")) {
                    criteriaQuery.orderBy(criteriaBuilder.desc(expression));
                } else {
                    criteriaQuery.orderBy(criteriaBuilder.asc(expression));
                }

            });

            Query<Post> query = session.createQuery(criteriaQuery);

            List<Post> posts = query.list();

            for(Post post : posts){
                session.createQuery("SELECT DISTINCT p FROM Post p " +
                                "LEFT JOIN FETCH p.comments " +
                                "LEFT JOIN FETCH p.tags " +
                                "LEFT JOIN FETCH p.likes " +
                                "WHERE p.id = :postId", Post.class)
                        .setParameter("postId", post.getId())
                        .uniqueResult();
            }

            return posts;
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
