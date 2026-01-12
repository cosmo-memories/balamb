package cosmo_memories.Balamb.controller;

import cosmo_memories.Balamb.model.enums.Category;
import cosmo_memories.Balamb.model.enums.Genre;
import cosmo_memories.Balamb.model.enums.UpdateType;
import cosmo_memories.Balamb.model.items.Book;
import cosmo_memories.Balamb.model.site.Update;
import cosmo_memories.Balamb.service.books.BookService;
import cosmo_memories.Balamb.service.site.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
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
        int numBooks = 6;
        model.addAttribute("activePage", "home");
        model.addAttribute("latestBooks", bookService.findNewestBooks(numBooks));
        List<Book> randomBooks = new ArrayList<>();
        for (int i=0; i < numBooks; i++) {
            randomBooks.add(bookService.findRandomBook());
        }
        model.addAttribute("randomBooks", randomBooks);
        return "pages/home";
    }

    @GetMapping("/browse")
    public String getBrowse(Model model,
                            @RequestParam(name = "genre", required = false) Genre genre,
                            @RequestParam(name = "category", required = false) Category category,
                            @RequestParam(name = "pageNo", required = false, defaultValue = "0") int pageNo) {
        model.addAttribute("activePage", "browse");
        model.addAttribute("genres", Genre.values());
        model.addAttribute("categories", Category.values());
        if (genre != null) {
            if (category != null) {
                model.addAttribute("bookList", bookService.findBookByGenreAndCategory(genre, category, pageNo));
                model.addAttribute("genre", genre);
                model.addAttribute("category", category);
            } else {
                model.addAttribute("bookList", bookService.findBookByGenre(genre, pageNo));
                model.addAttribute("genre", genre);
            }
        } else if (category != null) {
            model.addAttribute("bookList", bookService.findBookByCategory(category, pageNo));
            model.addAttribute("category", category);
        } else {
            model.addAttribute("bookList", bookService.findAllBooksOnPage(pageNo));
        }
        return "pages/browse";
    }

    @GetMapping("/about")
    public String getAbout(Model model) {
        model.addAttribute("activePage", "about");
        return "pages/about";
    }

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
        return "pages/book";
    }

}
