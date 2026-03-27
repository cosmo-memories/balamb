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
import java.util.Objects;
import java.util.Optional;

/**
 * Service class for Update-related functions.
 */
@Service
public class UpdateService {

    @Autowired
    UpdateRepository updateRepository;

    /**
     * Save new Update.
     * @param update        Update
     * @return              Update
     */
    public Update submitUpdate(Update update) {
        if (!validateUpdate(update)) {
            throw new IllegalArgumentException("Update is invalid.");
        }
        update.setCreated(LocalDateTime.now());
        update.setEdited(LocalDateTime.now());
        return updateRepository.save(update);
    }

    /**
     * Delete Update.
     * @param id            Update ID
     */
    public void deleteUpdate(Long id) {
        updateRepository.delete(updateRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Update does not exist!")));
    }

    /**
     * Find all Updates on given page.
     * @param pageNo        Page number
     * @param pageSize      Page size
     * @return              Page of Updates
     */
    public Page<Update> findAllUpdates(int pageNo, int pageSize) {
        return updateRepository.findAll(PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "created")));
    }

    /**
     * Find all Updates on given page, filtered by UpdateType.
     * @param type          UpdateType
     * @param pageNo        Page number
     * @param pageSize      Page size
     * @return              Page of Updates
     */
    public Page<Update> findUpdatesByType(UpdateType type, int pageNo, int pageSize) {
        return updateRepository.findByUpdateType(type, PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "created")));
    }

    /**
     * Mark Update as resolved.
     * @param id            Update ID
     */
    public void resolveUpdate(Long id) {
        Update update = updateRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Update does not exist!"));
        update.setResolved(!update.getResolved());
        update.setEdited(LocalDateTime.now());
        updateRepository.save(update);
    }

    /**
     * Save update. [for testing purposes]
     * @param update        Update
     * @return              Update
     */
    public Update save(Update update) {
        return updateRepository.save(update);
    }

    /**
     * Find Update by ID.
     * @param id            Update ID
     * @return              Update
     */
    public Optional<Update> findById(Long id) {
        return updateRepository.findById(id);
    }

    /**
     * Edit an Update.
     * @param id            Old Update ID
     * @param newUpdate     New Update data
     */
    public void editUpdate(Long id, Update newUpdate) {
        if (!validateUpdate(newUpdate)) {
            throw new IllegalArgumentException("New Update is invalid.");
        }
        Optional<Update> savedUpdate = updateRepository.findById(id);
        if (savedUpdate.isEmpty()) {
            throw new IllegalArgumentException("Update does not exist.");
        }
        Update oldUpdate = savedUpdate.get();
        if (!Objects.equals(oldUpdate.getAuthor().getId(), newUpdate.getAuthor().getId())) {
            throw new IllegalArgumentException("User is not the author of this Update.");
        }
        oldUpdate.setEdited(LocalDateTime.now());
        oldUpdate.setUpdateType(newUpdate.getUpdateType());
        oldUpdate.setDescription(newUpdate.getDescription());
        updateRepository.save(oldUpdate);
    }

    /**
     * Validate Update length.
     * @param update        Update
     * @return              Boolean result
     */
    public boolean validateUpdate(Update update) {
        return update.getDescription().length() <= 1000;
    }
}
