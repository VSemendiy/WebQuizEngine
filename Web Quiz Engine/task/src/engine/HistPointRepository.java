package engine;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface HistPointRepository extends PagingAndSortingRepository<HistPoint, Integer> {
    long count();

    Page<IdAndCompletedAt> findAllByUserIdOrderByCompletedAtDesc(String userId, Pageable pageable);
}

interface IdAndCompletedAt{
    int getId();
    LocalDateTime getCompletedAt();
}
