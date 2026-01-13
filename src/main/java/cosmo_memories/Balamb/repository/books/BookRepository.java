package cosmo_memories.Balamb.repository.books;

import cosmo_memories.Balamb.model.enums.Category;
import cosmo_memories.Balamb.model.enums.Genre;
import cosmo_memories.Balamb.model.items.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Repository for Book entities.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long>, PagingAndSortingRepository<Book, Long> {

    Page<Book> findByGenreOrSubgenreOrderByAddedDesc(Genre genre, Genre subgenre, Pageable pageable);

    Page<Book> findByCategoryOrderByAddedDesc(Category category, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE (b.genre = :genre OR b.subgenre = :subgenre) AND b.category = :category ORDER BY b.added DESC")
    Page<Book> findByGenreOrSubgenreAndCategoryOrderByAddedDesc(Genre genre, Genre subgenre, Category category, Pageable pageable);

    @Query("SELECT b FROM Book b ORDER BY b.added DESC")
    List<Book> findNewestBooks(Pageable pageable);

}
