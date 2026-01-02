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
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    BookService bookService;

    @Autowired
    UpdateService updateService;

    @Autowired
    UserService userService;

    public AdminController() {}

    @GetMapping("/admin")
    public String getAdmin(Model model) {
        model.addAttribute("activePage", "admin");
        return "pages/admin/admin";
    }

    @GetMapping("/login")
    public String getLogin(Model model) {
        model.addAttribute("activePage", "login");
        return "pages/login";
    }

    @GetMapping("/admin/add")
    public String getAddBook(Model model) {
        model.addAttribute("activePage", "add_book");
        model.addAttribute("bookDto", new BookDTO());
        model.addAttribute("categories", Category.values());
        model.addAttribute("genres", Genre.values());
        return "pages/admin/add";
    }

    @PostMapping("/admin/add/submit")
    public String submitBook(Model model, @Valid @ModelAttribute("bookDto") BookDTO bookDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.info("Failed to add book.");
            model.addAttribute("activePage", "add_book");
            model.addAttribute("bookDto", bookDto);
            model.addAttribute("categories", Category.values());
            model.addAttribute("genres", Genre.values());
            return "pages/admin/add";
        }
        if (bookService.validateBook(bookDto)) {
            bookService.saveNewBook(bookDto);
        }
        logger.info("Book added.");
        return "redirect:/admin/add";
    }

    @PostMapping("/admin/update")
    public String submitUpdate(Model model, @AuthenticationPrincipal LibraryUserDetails principal,
                               @Valid @ModelAttribute("update") Update update, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("activePage", "updates");
            model.addAttribute("update", update);
            model.addAttribute("updateType", UpdateType.values());
            model.addAttribute("error", true);
            model.addAttribute("errorText", "Something went wrong posting your update.");
            List<Update> updates = updateService.findAllUpdates();
            model.addAttribute("updateList", updates);
            return "/pages/updates";
        } else {
            LibraryUser user = userService.findUserById(principal.getId()).orElseThrow(() -> new IllegalArgumentException("User does not exist!"));
            update.setAuthor(user);
            updateService.submitUpdate(update);
        }
        return "redirect:/updates";
    }

    @DeleteMapping("/admin/update/{id}")
    public ResponseEntity<?> deleteUpdate(@PathVariable Long id) {
        try {
            updateService.deleteUpdate(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Delete failed. The selected note may not exist.");
        }
    }

    @DeleteMapping("/admin/book/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Delete failed. The selected book may not exist.");
        }
    }

    @PostMapping("/admin/add/image/{id}")
    public String uploadImage(Model model, @PathVariable Long id, @RequestParam("uploadedFile") MultipartFile file) {
        Book book = bookService.findBookById(id).orElseThrow(() -> new IllegalArgumentException("Book does not exist"));
        if (file.isEmpty()) {
            model.addAttribute("uploadError", "No file selected.");
            model.addAttribute("book", book);
            return "pages/book";
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            model.addAttribute("uploadError", "File must be smaller than 10MB.");
            model.addAttribute("book", book);
            return "pages/book";
        }
        String extension = file.getContentType().split("/")[1];
        List<String> allowed = Arrays.asList("png", "jpg", "jpeg");
        if (!allowed.contains(extension)) {
            model.addAttribute("uploadError", "File type must be PNG or JPG.");
            model.addAttribute("book", book);
            return "pages/book";
        }
        try {
            Path directory = Paths.get("uploads", "images");
            Files.createDirectories(directory);
            Path filepath = directory.resolve(id + "." + extension);
            Files.write(filepath, file.getBytes());
            book.setImage(id + "." + extension);
            bookService.saveBook(book);
        } catch (IOException e) {
            model.addAttribute("uploadError", "Something went wrong uploading the file.");
            model.addAttribute("book", book);
            return "pages/book";
        }
        return "redirect:/browse/" + id;
    }

    @PostMapping("/admin/edit/{id}")
    public String editBook(Model model, @PathVariable long id, @Valid @ModelAttribute("bookDto") BookDTO bookDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.info("Failed to edit book. Form fields invalid.");
            model.addAttribute("activePage", "browse");
            model.addAttribute("book", bookService.findBookById(id).orElseThrow(() -> new NoSuchElementException("Book not found")));
            model.addAttribute("bookDto", bookDto);
            model.addAttribute("categories", Category.values());
            model.addAttribute("genres", Genre.values());
            return "pages/book";
        }
        if (bookService.validateBook(bookDto)) {
            bookService.updateBook(id, bookDto);
        }
        logger.info("Book edited.");
        return "redirect:/browse/" + id;
    }

}
