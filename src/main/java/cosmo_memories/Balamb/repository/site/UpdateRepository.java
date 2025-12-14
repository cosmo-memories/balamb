package cosmo_memories.Balamb.repository.site;

import cosmo_memories.Balamb.model.site.Update;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UpdateRepository extends JpaRepository<Update, Long>, PagingAndSortingRepository<Update, Long> {



}
