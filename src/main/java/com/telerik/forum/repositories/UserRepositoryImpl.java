package com.telerik.forum.repositories;

import com.telerik.forum.models.Post;
import com.telerik.forum.models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class UserRepositoryImpl implements UserRepository {

    private final SessionFactory sessionFactory;


    @Autowired
    public UserRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public List<User> getAll() {
        try(Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User", User.class);

            return query.list();
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
        try(Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.comments WHERE u.id = :id", User.class);

            query.setParameter("id", id);

            return query.uniqueResult();
        }
    }

    @Override
    public User getById(int id) {
        try(Session session = sessionFactory.openSession()) {

            return session.get(User.class, id);

        }
    }

    @Override
    public User getByEmail(String email) {
        try(Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where emailAddress = :email", User.class);

            query.setParameter("email", email);

            return query.uniqueResult();
        }
    }

    @Override
    public User getByUsername(String username) {
        try(Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where username = :username", User.class);

            query.setParameter("username", username);

            return query.uniqueResult();
        }
    }

    @Override
    public User getByFirstName(String firstName) {
        try(Session session = sessionFactory.openSession()) {
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
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            User user = session.get(User.class, id);

            session.remove(user);

            session.getTransaction().commit();
        }
    }
}
