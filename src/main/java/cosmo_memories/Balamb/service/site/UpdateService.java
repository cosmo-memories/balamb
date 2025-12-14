package cosmo_memories.Balamb.service.site;

import cosmo_memories.Balamb.model.site.Update;
import cosmo_memories.Balamb.repository.site.UpdateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UpdateService {

    @Autowired
    UpdateRepository updateRepository;

    public Update submitUpdate(Update update) {
        update.setCreated(LocalDateTime.now());
        return updateRepository.save(update);
    }

    public void deleteUpdate(Long id) {
        updateRepository.delete(updateRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Update does not exist!")));
    }

    public List<Update> findAllUpdates() {
        return updateRepository.findAll(Sort.by(Sort.Direction.DESC, "created"));
    }

}
