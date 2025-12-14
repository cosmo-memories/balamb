package cosmo_memories.Balamb.repository.books;

import cosmo_memories.Balamb.model.enums.Category;
import cosmo_memories.Balamb.model.enums.Genre;
import cosmo_memories.Balamb.model.items.Book;
import jakarta.persistence.OrderBy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, PagingAndSortingRepository<Book, Long> {

    List<Book> findByGenreOrderByAddedDesc(Genre genre);

    List<Book> findByCategoryOrderByAddedDesc(Category category);

    List<Book> findByGenreAndCategoryOrderByAddedDesc(Genre genre, Category category);

}
