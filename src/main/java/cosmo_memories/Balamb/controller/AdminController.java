package cosmo_memories.Balamb.controller;

import cosmo_memories.Balamb.model.accounts.LibraryUser;
import cosmo_memories.Balamb.model.accounts.LibraryUserDetails;
import cosmo_memories.Balamb.model.enums.Category;
import cosmo_memories.Balamb.model.enums.Genre;
import cosmo_memories.Balamb.model.enums.UpdateType;
import cosmo_memories.Balamb.model.items.Book;
import cosmo_memories.Balamb.model.items.BookDTO;
import cosmo_memories.Balamb.model.site.Update;
import cosmo_memories.Balamb.service.accounts.UserService;
import cosmo_memories.Balamb.service.books.BookService;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Controller for functions and pages only accessible to logged-in users.
 */
@Controller
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    BookService bookService;

    /**
     * GET mapping for admin panel.
     * @param model     Model
     * @return          Admin page
     */
    @GetMapping("/admin")
    public String getAdmin(Model model) {
        model.addAttribute("activePage", "admin");
        return "pages/admin/admin";
    }

    /**
     * GET mapping for login form.
     * @param model     Model
     * @return          Login page
     */
    @GetMapping("/login")
    public String getLogin(Model model) {
        model.addAttribute("activePage", "login");
        return "pages/login";
    }

    /**
     * GET mapping for new book form.
     * @param model     Model
     * @return          New book form
     */
    @GetMapping("/admin/add/book")
    public String getAddBook(Model model) {
        model.addAttribute("activePage", "add_book");
        model.addAttribute("bookDto", new BookDTO());
        model.addAttribute("categories", Category.values());
        model.addAttribute("genres", Genre.values());
        return "pages/admin/add";
    }

    /**
     * POST mapping for new book form.
     * @param model             Model
     * @param bookDto           BookDTO
     * @param bindingResult     BindingResult
     * @return                  New book form
     */
    @PostMapping("/admin/add/book")
    public String submitBook(Model model, @Valid @ModelAttribute("bookDto") BookDTO bookDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.info("Failed to add book.");
            model.addAttribute("activePage", "add_book");
            model.addAttribute("bookDto", bookDto);
            model.addAttribute("categories", Category.values());
            model.addAttribute("genres", Genre.values());
            return "pages/admin/add";
        }
        if (bookService.validateBookDto(bookDto)) {
            Book newBook = bookService.saveBookFromDto(bookDto);
            logger.info("Book added.");
            return "redirect:/browse/" + newBook.getId();
        }
        return "redirect:/admin/add/book";
    }

}
