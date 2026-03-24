package cosmo_memories.Balamb.controller;

import cosmo_memories.Balamb.model.accounts.LibraryUser;
import cosmo_memories.Balamb.model.accounts.LibraryUserDetails;
import cosmo_memories.Balamb.model.enums.UpdateType;
import cosmo_memories.Balamb.model.site.Update;
import cosmo_memories.Balamb.service.accounts.UserService;
import cosmo_memories.Balamb.service.site.UpdateService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for Updates page and Update objects.
 */
@Controller
public class UpdateController {

    private static final Logger logger = LoggerFactory.getLogger(UpdateController.class);

    @Autowired
    UpdateService updateService;

    @Autowired
    UserService userService;

    /**
     * GET mapping for updates page.
     * @param model     Model
     * @param type      Search type
     * @param pageNo    Page number
     * @return          Updates page
     */
    @GetMapping("/updates")
    public String getUpdates(Model model,
                             @RequestParam(name = "type", required = false) UpdateType type,
                             @RequestParam(name = "pageNo", required = false, defaultValue = "0") int pageNo) {
        model.addAttribute("activePage", "updates");
        model.addAttribute("updateType", UpdateType.values());
        model.addAttribute("update", new Update());
        int pageSize = 10;
        Page<Update> updates;
        if (type != null) {
            updates = updateService.findUpdatesByType(type, pageNo, pageSize);
            model.addAttribute("type", type);
        } else {
            updates = updateService.findAllUpdates(pageNo, pageSize);
        }
        model.addAttribute("updateList", updates);
        return "pages/updates";
    }

    /**
     * POST mapping for new update form.
     * @param model             Model
     * @param principal         Logged in user
     * @param update            Update object
     * @param bindingResult     BindingResult
     * @return                  Updates page
     */
    @PostMapping("/admin/update")
    public String submitUpdate(Model model, @AuthenticationPrincipal LibraryUserDetails principal,
                               @Valid @ModelAttribute("update") Update update, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("activePage", "updates");
            model.addAttribute("update", update);
            model.addAttribute("updateType", UpdateType.values());
            model.addAttribute("error", true);
            model.addAttribute("errorText", "Something went wrong posting your update.");
            Page<Update> updates = updateService.findAllUpdates(0, 10);
            model.addAttribute("updateList", updates);
            return "/pages/updates";
        } else {
            LibraryUser user = userService.findUserById(principal.getId()).orElseThrow(() -> new IllegalArgumentException("User does not exist!"));
            update.setAuthor(user);
            updateService.submitUpdate(update);
        }
        return "redirect:/updates";
    }

    /**
     * POST mapping for resolving To Do/Known Issue updates.
     * @param id        Update ID
     * @return          ResponseEntity
     */
    @PostMapping("/admin/update/{id}")
    public ResponseEntity<?> resolveUpdate(@PathVariable Long id) {
        try {
            updateService.resolveUpdate(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Edit failed. The selected note may not exist.");
        }
    }

    /**
     * DELETE mapping for updates.
     * @param id        Update ID
     * @return          ResponseEntity
     */
    @DeleteMapping("/admin/update/{id}")
    public ResponseEntity<?> deleteUpdate(@PathVariable Long id) {
        try {
            updateService.deleteUpdate(id);
            logger.info("Update deleted.");
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.info("Delete update failed.");
            return ResponseEntity.badRequest().body("Delete failed. The selected note may not exist.");
        }
    }

}
