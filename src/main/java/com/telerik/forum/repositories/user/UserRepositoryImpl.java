package com.telerik.forum.repositories.user;

import com.telerik.forum.models.dtos.userDTOs.UserDisplayMvcDTO;
import com.telerik.forum.models.filters.FilterUserOptions;
import com.telerik.forum.models.post.Post;
import com.telerik.forum.models.user.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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

import static com.telerik.forum.repositories.utilities.SortingHelper.sortingHelper;


@Repository
public class UserRepositoryImpl implements UserRepository {

    private final SessionFactory sessionFactory;


    @Autowired
    public UserRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public List<User> getAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User", User.class);

            return query.list();
        }
    }

    @Override
    public List<UserDisplayMvcDTO> getAllMvc() {
        try (Session session = sessionFactory.openSession()) {
            Query<UserDisplayMvcDTO> query = session.createQuery("SELECT new com.telerik.forum.models.dtos.userDTOs.UserDisplayMvcDTO(u.id, CONCAT(u.firstName, ' ', u.lastName), u.username, (SELECT COUNT(p) FROM Post p WHERE p.user.id = u.id), (SELECT COUNT(c) FROM Comment c WHERE c.user.id = u.id),CASE WHEN 'ADMIN' IN (SELECT r.name FROM u.roles r) THEN true ELSE false END, u.blocked , u.lastLogin) FROM User u", UserDisplayMvcDTO.class);
            return query.list();
        }
    }

    @Override
    public Page<User> getAll(FilterUserOptions options, Pageable pageable) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);

            Root<User> root = criteriaQuery.from(User.class);

            List<Predicate> predicates = new ArrayList<>();

            options.getFirstName().ifPresent(firstName -> {
                predicates.add(criteriaBuilder.like(root.get("firstName"), "%" + firstName + "%"));
            });

            options.getLastName().ifPresent(lastName -> {
                predicates.add(criteriaBuilder.like(root.get("lastName"), "%" + lastName + "%"));
            });

            options.getUsername().ifPresent(username -> {
                predicates.add(criteriaBuilder.like(root.get("username"), "%" + username + "%"));
            });

            options.getEmailAddress().ifPresent(email -> {
                predicates.add(criteriaBuilder.like(root.get("emailAddress"), "%" + email + "%"));
            });

            criteriaQuery.where(predicates.toArray(new Predicate[0]));

            sortingHelper(criteriaBuilder, root, criteriaQuery, options);


            Query<User> query = session.createQuery(criteriaQuery)
                    .setFirstResult((int) pageable.getOffset())
                    .setMaxResults(pageable.getPageSize());


            return new PageImpl<>(query.getResultList(), pageable, query.getResultCount());
        }
    }

    @Override
    public User getByIdWithPosts(int id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.createQuery("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.posts WHERE u.id = :id", User.class)
                    .setParameter("id", id)
                    .uniqueResult();


            if (user != null) {
                for (Post post : user.getPosts()) {
                    session.createQuery("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.comments WHERE p.id = :postId", Post.class)
                            .setParameter("postId", post.getId())
                            .uniqueResult();
                }
            }

            return user;
        }
    }


    @Override
    public User getByIdWithComments(int id) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.comments WHERE u.id = :id", User.class);

            query.setParameter("id", id);

            return query.uniqueResult();
        }
    }

    @Override
    public User getByIdWithRoles(int id) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id", User.class);

            query.setParameter("id", id);

            return query.uniqueResult();

        }
    }


    @Override
    public User getById(int id) {
        try (Session session = sessionFactory.openSession()) {

            return session.get(User.class, id);

        }
    }

    @Override
    public User getByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where emailAddress = :email", User.class);

            query.setParameter("email", email);

            return query.uniqueResult();
        }
    }

    @Override
    public User getByUsernameWithRoles(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username", User.class);

            query.setParameter("username", username);

            return query.uniqueResult();
        }
    }


    @Override
    public User getByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User WHERE username = :username", User.class);

            query.setParameter("username", username);

            return query.uniqueResult();
        }
    }

    @Override
    public User getByFirstName(String firstName) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where firstName = :firstName", User.class);

            query.setParameter("firstName", firstName);

            return query.uniqueResult();
        }
    }

    @Override
    public void create(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            session.persist(user);

            session.getTransaction().commit();
        }
    }

    @Override
    public void update(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            session.merge(user);

            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(int id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            User user = session.get(User.class, id);

            session.remove(user);

            session.getTransaction().commit();
        }
    }

}
