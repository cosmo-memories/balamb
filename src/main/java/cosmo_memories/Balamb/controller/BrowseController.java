package cosmo_memories.Balamb.controller;

import cosmo_memories.Balamb.model.enums.Category;
import cosmo_memories.Balamb.model.enums.Genre;
import cosmo_memories.Balamb.model.enums.SortOrder;
import cosmo_memories.Balamb.service.books.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for Browse page.
 */
@Controller
public class BrowseController {

    @Autowired
    BookService bookService;

    /**
     * GET mapping for browse records page.
     * @param model         Model
     * @param genre         Search genre
     * @param category      Search category
     * @param pageNo        Page number
     * @return              Browse page
     */
    @GetMapping("/browse")
    public String getBrowse(Model model,
                            @RequestParam(name = "genre", required = false) Genre genre,
                            @RequestParam(name = "category", required = false) Category category,
                            @RequestParam(name = "pageNo", required = false, defaultValue = "0") int pageNo,
                            @RequestParam(name = "sortOrder", required = false, defaultValue = "ADDED") SortOrder sortOrder) {
        model.addAttribute("activePage", "browse");
        model.addAttribute("genres", Genre.values());
        model.addAttribute("categories", Category.values());
        model.addAttribute("sorts", SortOrder.values());
        sortOrder = bookService.validateSortOrder(sortOrder);
        model.addAttribute("sortOrder", sortOrder);
        if (genre != null) {
            if (category != null) {
                model.addAttribute("bookList", bookService.findBookByGenreAndCategory(genre, category, pageNo, sortOrder));
                model.addAttribute("genre", genre);
                model.addAttribute("category", category);
            } else {
                model.addAttribute("bookList", bookService.findBookByGenre(genre, pageNo, sortOrder));
                model.addAttribute("genre", genre);
            }
        } else if (category != null) {
            model.addAttribute("bookList", bookService.findBookByCategory(category, pageNo, sortOrder));
            model.addAttribute("category", category);
        } else {
            model.addAttribute("bookList", bookService.findAllBooksOnPage(pageNo, sortOrder));
        }
        return "pages/browse";
    }

}