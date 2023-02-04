package com.dynatrace.books.controller;

import com.dynatrace.books.exception.BadRequestException;
import com.dynatrace.books.exception.ResourceNotFoundException;
import com.dynatrace.books.model.Book;
import com.dynatrace.books.repository.BookRepository;
import com.dynatrace.books.repository.ConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/books")
public class BookController extends HardworkingController {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    ConfigRepository configRepository;
    Logger logger = LoggerFactory.getLogger(BookController.class);

    // get all books
    @GetMapping("")
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // get a book by id
    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {
        simulateHardWork();
        simulateCrash();
        Optional<Book> book = bookRepository.findById(id);
        if (book.isEmpty()) {
            ResourceNotFoundException ex = new ResourceNotFoundException("Book not found");
            logger.error(ex.getMessage());
            throw ex;
        }
        return book.get();
    }

    // find a book by isbn
    @GetMapping("/find")
    public Book getBookByIsbn(@RequestParam String isbn) {
        simulateHardWork();
        simulateCrash();
        logger.info("Looking for book " + isbn);
        Book bookDb = bookRepository.findByIsbn(isbn);
        if (bookDb == null) {
            ResourceNotFoundException ex = new ResourceNotFoundException("Book does not exist, ISBN: " + isbn);
            logger.error(ex.getMessage());
            throw ex;
        }
        return bookDb;
    }

    // ingest a book
    @PostMapping("")
    public Book ingestBook(@RequestBody Book book) {
        simulateHardWork();
        simulateCrash();
        logger.debug("Creating book " + book.getIsbn());
        return bookRepository.save(book);
    }

    // update a book
    @PutMapping("/{id}")
    public Book updateBookById(@PathVariable Long id, @RequestBody Book book) {
        Optional<Book> bookDB = bookRepository.findById(id);
        if (bookDB.isEmpty()) {
            ResourceNotFoundException ex = new ResourceNotFoundException("Book not found");
            logger.error(ex.getMessage());
            throw ex;
        } else if (book.getId() != id || bookDB.get().getId() != id) {
            BadRequestException ex = new BadRequestException("bad book id");
            logger.error(ex.getMessage());
            throw ex;
        }
        return bookRepository.save(book);
    }

    // delete a book
    @DeleteMapping("/{id}")
    public void deleteBookById(@PathVariable Long id) {
        bookRepository.deleteById(id);
    }

    // delete all books
    @DeleteMapping("/delete-all")
    public void deleteAllBooks() {
        bookRepository.deleteAll();
    }

    // vend a book by isbn
    @PostMapping("/vend")
    public Book vendBookByIsbn(@RequestParam String isbn) {
        simulateHardWork();
        simulateCrash();
        Book book = bookRepository.findByIsbn(isbn);
        if (book == null) {
            ResourceNotFoundException ex = new ResourceNotFoundException("Book not found, ISBN: " + isbn);
            logger.error(ex.getMessage());
            throw ex;
        }

        book.setPublished(!book.isPublished());
        return bookRepository.save(book);
    }

    // vend all books
    @PostMapping("/vend-all")
    public void vendAllBooks() {
        // empty loop to simulate hard work
        simulateHardWork();
        simulateCrash();

//        bookRepository.bulkBookVending(true);
        this.bulkVending(true);
    }

    // unvend all books
    @DeleteMapping("/vend-all")
    public void unvendAllBooks() {
//        bookRepository.bulkBookVending(false);
        this.bulkVending(false);
    }

    private void bulkVending(boolean vend) {
        for (Book book: bookRepository.findByPublished(!vend)) {
            book.setPublished(vend);
            bookRepository.save(book);
        }
    }

    @Override
    public ConfigRepository getConfigRepository() {
        return configRepository;
    }
}
