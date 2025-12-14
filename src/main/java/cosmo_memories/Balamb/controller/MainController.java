package cosmo_memories.Balamb.controller;

import cosmo_memories.Balamb.model.enums.Category;
import cosmo_memories.Balamb.model.enums.Genre;
import cosmo_memories.Balamb.model.enums.UpdateType;
import cosmo_memories.Balamb.model.items.Book;
import cosmo_memories.Balamb.model.site.Update;
import cosmo_memories.Balamb.service.books.BookService;
import cosmo_memories.Balamb.service.site.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class MainController {

    @Autowired
    UpdateService updateService;

    @Autowired
    BookService bookService;

    @GetMapping("/")
    public String getHome(Model model) {
        model.addAttribute("activePage", "home");
        return "pages/home";
    }

    @GetMapping("/browse")
    public String getBrowse(Model model,
                            @RequestParam(name = "genre", required = false) Genre genre,
                            @RequestParam(name = "category", required = false) Category category) {
        model.addAttribute("activePage", "browse");
        model.addAttribute("bookList", bookService.findAllBooks());
        model.addAttribute("genres", Genre.values());
        model.addAttribute("categories", Category.values());
        if (genre != null) {
            if (category != null) {
                model.addAttribute("bookList", bookService.findBookByGenreAndCategory(genre, category));
                model.addAttribute("genre", genre);
                model.addAttribute("category", category);
            } else {
                model.addAttribute("bookList", bookService.findBookByGenre(genre));
                model.addAttribute("genre", genre);
            }
        } else if (category != null) {
            model.addAttribute("bookList", bookService.findBookByCategory(category));
            model.addAttribute("category", category);
        }
        return "pages/browse";
    }

    @GetMapping("/about")
    public String getAbout(Model model) {
        model.addAttribute("activePage", "about");
        return "pages/about";
    }

    @GetMapping("/updates")
    public String getUpdates(Model model) {
        model.addAttribute("activePage", "updates");
        model.addAttribute("updateType", UpdateType.values());
        model.addAttribute("update", new Update());
        List<Update> updates = updateService.findAllUpdates();
        model.addAttribute("updateList", updates);
        return "pages/updates";
    }

    @GetMapping("/browse/{id}")
    public String getBookPage(Model model, @PathVariable Long id) {
        Optional<Book> book = bookService.findBookById(id);
        if (book.isEmpty()) {
            return "redirect:/browse";
        }
        model.addAttribute("activePage", "browse");
        model.addAttribute("book", book.get());
        return "pages/book";
    }

}
