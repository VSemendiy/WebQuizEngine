package engine;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface QuizCardRepository extends PagingAndSortingRepository<QuizCard, Integer> {
    List<QuizCard> findAll();
    Page<IdTitleTextOptions> findAllBy(Pageable pageable);
    QuizCard findById(int id);
    void deleteById(int id);
}

interface IdTitleTextOptions{
    int getId();
    String getTitle();
    String getText();
    String[] getOptions();
}