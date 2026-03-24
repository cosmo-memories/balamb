package cosmo_memories.Balamb.controller;

import cosmo_memories.Balamb.model.items.Book;
import cosmo_memories.Balamb.service.books.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Controller for core pages.
 */
@Controller
public class MainController {

    @Autowired
    BookService bookService;

    /**
     * GET mapping for home page.
     * @param model     Model
     * @return          Home page
     */
    @GetMapping("/")
    public String getHome(Model model) {
        int numBooks = 6;
        model.addAttribute("activePage", "home");
        model.addAttribute("latestBooks", bookService.findNewestBooks(numBooks));
        List<Book> randomBooks = new ArrayList<>();
        Book newBook;
        if (bookService.countBooks() >= 6) {
            for (int i=0; randomBooks.size() < numBooks; i++) {
                newBook = bookService.findRandomBook();
                if (!randomBooks.contains(newBook)) {
                    randomBooks.add(newBook);
                }
            }
        } else {
            for (int i=0; i < numBooks; i++) {
                newBook = bookService.findRandomBook();
                if (!randomBooks.contains(newBook)) {
                    randomBooks.add(newBook);
                }
            }
        }
        model.addAttribute("randomBooks", randomBooks.stream().filter(Objects::nonNull).collect(Collectors.toList()));
        return "pages/home";
    }

    /**
     * GET mapping for login attempts exceeded page.
     * @param model         Model
     * @return              Blocked page
     */
    @GetMapping("/blocked")
    public String getBlocked(Model model) {
        return "pages/blocked";
    }

    /**
     * GET mapping for about page.
     * @param model     Model
     * @return          About page
     */
    @GetMapping("/about")
    public String getAbout(Model model) {
        model.addAttribute("activePage", "about");
        return "pages/about";
    }

}
