package application.repository;

import application.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User getByUsername(String username);

    User getByUsernameAndPassword(String username, String password);

//    @Query("SELECT u FROM User u JOIN FETCH u.orders WHERE u.id = :userId")
//    User findUserWithOrdersById(Integer userId);
}
