package cosmo_memories.Balamb.controller;

import cosmo_memories.Balamb.model.enums.Category;
import cosmo_memories.Balamb.model.enums.Genre;
import cosmo_memories.Balamb.model.items.Book;
import cosmo_memories.Balamb.model.items.BookDTO;
import cosmo_memories.Balamb.service.books.BookService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Controller for individual Book pages.
 */
@Controller
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    BookService bookService;

    /**
     * GET mapping for individual book page.
     * @param model     Model
     * @param id        Book ID
     * @return          Individual book page
     */
    @GetMapping("/browse/{id}")
    public String getBookPage(Model model, @PathVariable Long id) {
        Optional<Book> book = bookService.findBookById(id);
        if (book.isEmpty()) {
            return "redirect:/browse";
        }
        model.addAttribute("activePage", "browse");
        model.addAttribute("book", book.get());
        model.addAttribute("bookDto", bookService.mapBookToDto(book.get()));
        model.addAttribute("categories", Category.values());
        model.addAttribute("genres", Genre.values());
        model.addAttribute("series", bookService.findAllBooksInSeries(book.get().getSeries()));
        model.addAttribute("random", bookService.listRandomBooksInGenre(book.get().getGenre()));
        return "pages/book";
    }

    /**
     * POST mapping for editing books.
     * @param model             Model
     * @param id                Book ID
     * @param bookDto           Book DTO
     * @param bindingResult     BindingResult
     * @return                  Book page
     */
    @PostMapping("/admin/edit/{id}")
    public String editBook(Model model, @PathVariable long id, @Valid @ModelAttribute("bookDto") BookDTO bookDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Book book = bookService.findBookById(id).orElseThrow(() -> new NoSuchElementException("Book not found"));
            logger.info("Failed to edit book. Form fields invalid.");
            model.addAttribute("activePage", "browse");
            model.addAttribute("book", book);
            model.addAttribute("bookDto", bookDto);
            model.addAttribute("categories", Category.values());
            model.addAttribute("genres", Genre.values());
            model.addAttribute("formErrors", true);
            model.addAttribute("series", bookService.findAllBooksInSeries(book.getSeries()));
            model.addAttribute("random", bookService.listRandomBooksInGenre(book.getGenre()));
            return "pages/book";
        }
        if (bookService.validateBookDto(bookDto)) {
            bookService.updateBook(id, bookDto);
        } else {
            logger.info("Failed book validation.");
        }
        logger.info("Book edited.");
        return "redirect:/browse/" + id;
    }

    /**
     * DELETE mapping for books.
     * @param id        Book ID
     * @return          ResponseEntity
     */
    @DeleteMapping("/admin/book/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            logger.info("Book deleted.");
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.info("Delete book failed.");
            return ResponseEntity.badRequest().body("Delete failed. The selected book may not exist.");
        }
    }

    /**
     * POST mapping for adding images.
     * @param model     Model
     * @param id        Book ID
     * @param file      Uploaded file
     * @return          Book page
     */
    @PostMapping("/admin/add/image/{id}")
    public String uploadImage(Model model, @PathVariable Long id, @RequestParam("uploadedFile") MultipartFile file) {
        Book book = bookService.findBookById(id).orElseThrow(() -> new IllegalArgumentException("Book does not exist"));
        String error = bookService.validateImage(file);
        if (error != null && !error.isEmpty()) {
            logger.info("File upload failed verification.");
            model.addAttribute("uploadError", error);
            model.addAttribute("book", book);
            model.addAttribute("bookDto", bookService.mapBookToDto(book));
            model.addAttribute("activePage", "browse");
            model.addAttribute("series", bookService.findAllBooksInSeries(book.getSeries()));
            model.addAttribute("random", bookService.listRandomBooksInGenre(book.getGenre()));
            return "pages/book";
        }

        try {
            bookService.uploadImage(file, book);
        } catch (IOException e) {
            logger.info("Something went wrong uploading a file.");
            model.addAttribute("uploadError", "Something went wrong uploading the file.");
            model.addAttribute("book", book);
            model.addAttribute("activePage", "browse");
            model.addAttribute("series", bookService.findAllBooksInSeries(book.getSeries()));
            model.addAttribute("random", bookService.listRandomBooksInGenre(book.getGenre()));
            return "pages/book";
        }
        return "redirect:/browse/" + id;
    }

    /**
     * POST mapping for toggling a book's complete/incomplete marker.
     * @param model         Model
     * @param id            Book ID
     * @return              Book page
     */
    @PostMapping("/admin/update/{id}/complete")
    public String completeBook(Model model, @PathVariable Long id) {
        try {
            Book book = bookService.findBookById(id).orElseThrow(() -> new NoSuchElementException("Book not found"));
            bookService.toggleComplete(book);
            return "redirect:/browse/" + id;
        } catch (NoSuchElementException e) {
            logger.info("Toggle book completion failed.");
            // Book ID doesn't exist
            // TODO: Handle properly?
            return "redirect:/browse";
        }
    }

}
