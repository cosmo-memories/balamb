package cosmo_memories.Balamb.repository.books;

import cosmo_memories.Balamb.model.enums.Category;
import cosmo_memories.Balamb.model.enums.Genre;
import cosmo_memories.Balamb.model.items.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, PagingAndSortingRepository<Book, Long> {

    Page<Book> findByGenreOrderByAddedDesc(Genre genre, Pageable pageable);

    Page<Book> findByCategoryOrderByAddedDesc(Category category, Pageable pageable);

    Page<Book> findByGenreAndCategoryOrderByAddedDesc(Genre genre, Category category, Pageable pageable);

}
