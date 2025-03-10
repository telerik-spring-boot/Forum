package com.telerik.forum.repositories.post;

import com.telerik.forum.models.dtos.postDTOs.PostDisplayDTO;
import com.telerik.forum.models.filters.FilterPostOptions;
import com.telerik.forum.models.post.Like;
import com.telerik.forum.models.post.Post;
import com.telerik.forum.models.post.Tag;
import jakarta.persistence.criteria.*;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    public List<Post> getAllPosts() {
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
    public Page<Post> getAllPostsWithFilters(FilterPostOptions options, Pageable pageable) {
        return getPostsWithFiltersHelper(-1, options, pageable);
    }

    @Override
    public List<Post> getAllPostsWithFilters(FilterPostOptions options) {
        return getPostsWithFiltersHelper(-1, options);
    }

    @Override
    public List<PostDisplayDTO> getPostsCreationDates() {
        try (Session session = sessionFactory.openSession()) {

            Query<PostDisplayDTO> query = session.createQuery(
                    "SELECT new com.telerik.forum.models.dtos.postDTOs.PostDisplayDTO(p.createdAt) FROM Post p",
                    PostDisplayDTO.class
            );

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
    public List<Post> getMostRecentPosts(int limit) {
        try (Session session = sessionFactory.openSession()) {
            Query<Post> query = session.createQuery
                    ("SELECT DISTINCT p FROM Post p" +
                                    " LEFT JOIN FETCH p.comments " +
                                    " LEFT JOIN FETCH p.likes " +
                                    " LEFT JOIN FETCH p.tags " +
                                    "ORDER BY p.createdAt DESC",
                            Post.class);

            query.setMaxResults(limit);
            return query.list();
        }
    }

    @Override
    public Page<Post> getPostsWithCommentsByUserId(int userId, FilterPostOptions options, Pageable pageable) {
        return getPostsWithFiltersHelper(userId, options, pageable);
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

    private Page<Post> getPostsWithFiltersHelper(int userId, FilterPostOptions options, Pageable pageable) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<Post> criteriaQuery = criteriaBuilder.createQuery(Post.class);

            Root<Post> root = criteriaQuery.from(Post.class);

            Join<Post, Like> likesJoin = root.join("likes", JoinType.LEFT);

            List<Predicate> predicates = new ArrayList<>();

            if (userId != -1) {
                predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));
            }

            options.getCreatorUsername().ifPresent(username -> {
                predicates.add(criteriaBuilder.like(root.get("user").get("username"), "%" + username + "%"));
            });

            options.getTitle().ifPresent(title -> {
                predicates.add(criteriaBuilder.like(root.get("title"), "%" + title + "%"));
            });

            options.getContent().ifPresent(content -> {
                predicates.add(criteriaBuilder.like(root.get("content"), "%" + content + "%"));
            });


            options.getTags().ifPresent(tagNames -> {
                Join<Post, Tag> tagsJoin = root.join("tags", JoinType.LEFT);
                List<Predicate> tagPredicates = new ArrayList<>();

                for (String tagName : tagNames) {
                    tagPredicates.add(criteriaBuilder.like(tagsJoin.get("name"), "%" + tagName + "%"));
                }
                predicates.add(criteriaBuilder.or(tagPredicates.toArray(new Predicate[0])));
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

            Query<Post> query = session.createQuery(criteriaQuery)
                    .setFirstResult((int) pageable.getOffset())
                    .setMaxResults(pageable.getPageSize());


            List<Post> posts = query.list();


            posts.forEach(post -> {
                Hibernate.initialize(post.getTags());
                Hibernate.initialize(post.getLikes());
                Hibernate.initialize(post.getComments());
            });
            return new PageImpl<>(query.getResultList(), pageable, query.getResultCount());
//            return posts;
        }
    }

    private List<Post> getPostsWithFiltersHelper(int userId, FilterPostOptions options) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<Post> criteriaQuery = criteriaBuilder.createQuery(Post.class);

            Root<Post> root = criteriaQuery.from(Post.class);

            Join<Post, Like> likesJoin = root.join("likes", JoinType.LEFT);

            List<Predicate> predicates = new ArrayList<>();

            if (userId != -1) {
                predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));
            }

            options.getCreatorUsername().ifPresent(username -> {
                predicates.add(criteriaBuilder.like(root.get("user").get("username"), "%" + username + "%"));
            });

            options.getTitle().ifPresent(title -> {
                predicates.add(criteriaBuilder.like(root.get("title"), "%" + title + "%"));
            });

            options.getContent().ifPresent(content -> {
                predicates.add(criteriaBuilder.like(root.get("content"), "%" + content + "%"));
            });


            options.getTags().ifPresent(tagNames -> {
                Join<Post, Tag> tagsJoin = root.join("tags", JoinType.LEFT);
                List<Predicate> tagPredicates = new ArrayList<>();

                for (String tagName : tagNames) {
                    tagPredicates.add(criteriaBuilder.like(tagsJoin.get("name"), "%" + tagName + "%"));
                }
                predicates.add(criteriaBuilder.or(tagPredicates.toArray(new Predicate[0])));
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


            posts.forEach(post -> {
                Hibernate.initialize(post.getTags());
                Hibernate.initialize(post.getLikes());
                Hibernate.initialize(post.getComments());
            });

            return posts;
        }
    }

}
