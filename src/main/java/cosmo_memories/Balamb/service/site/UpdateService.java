package cosmo_memories.Balamb.service.site;

import cosmo_memories.Balamb.model.enums.UpdateType;
import cosmo_memories.Balamb.model.site.Update;
import cosmo_memories.Balamb.repository.site.UpdateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UpdateService {

    @Autowired
    UpdateRepository updateRepository;

    public Update submitUpdate(Update update) {
        update.setCreated(LocalDateTime.now());
        update.setEdited(LocalDateTime.now());
        return updateRepository.save(update);
    }

    public void deleteUpdate(Long id) {
        updateRepository.delete(updateRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Update does not exist!")));
    }

    public Page<Update> findAllUpdates(int pageNo, int pageSize) {
        return updateRepository.findAll(PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "created")));
    }

    public Page<Update> findUpdatesByType(UpdateType type, int pageNo, int pageSize) {
        return updateRepository.findByUpdateType(type, PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "created")));
    }

    public void resolveUpdate(Long id) {
        Update update = updateRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Update does not exist!"));
        update.setResolved(true);
        update.setEdited(LocalDateTime.now());
        updateRepository.save(update);
    }

}
