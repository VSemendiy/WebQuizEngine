package engine;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, String> {
        List<User> findAll();
        Optional<User> findById(String email);
        User findByPassword(String password);
        long count();
}
