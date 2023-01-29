package com.dynatrace.books.controller;

import com.dynatrace.books.exception.BadRequestException;
import com.dynatrace.books.exception.ResourceNotFoundException;
import com.dynatrace.books.model.Book;
import com.dynatrace.books.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/")
public class BookController {
    @Value("${added.workload.cpu}")
    private long cpuPressure;
    @Value("${added.workload.ram}")
    private int memPressureMb;

    @Autowired
    private BookRepository bookRepository;

    // get all books
    @GetMapping("/books")
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // get a book by id
    @GetMapping("/books/{id}")
    public Book getBookById(@PathVariable Long id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isEmpty()) {
            throw new ResourceNotFoundException("Book not found");
        }
        return book.get();
    }

    // find a book by isbn
    @GetMapping("/books/find")
    public Book getBookByIsbn(@RequestParam String isbn) {
        Book bookDb = bookRepository.findByIsbn(isbn);
        if (bookDb == null) {
            throw new ResourceNotFoundException("Book does not exist, ISBN: " + isbn);
        }
        return bookDb;
    }

    // ingest a book
    @PostMapping("/books")
    public Book ingestBook(@RequestBody Book book) {
        simulateHardWork();
        return bookRepository.save(book);
    }

    // update a book
    @PutMapping("/books/{id}")
    public Book updateBookById(@PathVariable Long id, @RequestBody Book book) {
        Optional<Book> bookDB = bookRepository.findById(id);
        if (bookDB.isEmpty()) {
            throw new ResourceNotFoundException("Book not found");
        } else if (book.getId() != id || bookDB.get().getId() != id) {
            throw new BadRequestException("bad book id");
        }
        return bookRepository.save(book);
    }

    // delete a book
    @DeleteMapping("/books/{id}")
    public void deleteBookById(@PathVariable Long id) {
        bookRepository.deleteById(id);
    }

    // delete all books
    @DeleteMapping("/books/delete-all")
    public void deleteAllBooks() {
        bookRepository.deleteAll();
    }

    // vend a book by isbn
    @PostMapping("/books/vend")
    public Book vendBookByIsbn(@RequestParam String isbn) {
        Book book = bookRepository.findByIsbn(isbn);
        if (book == null) {
            throw new ResourceNotFoundException("Book not found, ISBN: " + isbn);
        }
        simulateHardWork();

        book.setPublished(!book.isPublished());
        return bookRepository.save(book);
    }

    // vend all books
    @PostMapping("/books/vend-all")
    public void vendAllBooks() {
        // empty loop to simulate hard work
        simulateHardWork();

//        bookRepository.bulkBookVending(true);
        this.bulkVending(true);
    }

    // unvend all books
    @DeleteMapping("/books/vend-all")
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

    private void simulateHardWork() {
        int arraySize = (int)((long)this.memPressureMb * 1024L * 1024L / 8L);
        if (arraySize < 0) {
            arraySize = Integer.MAX_VALUE;
        }
        long[] longs = new long[arraySize];
        int j = 0;
        for(long i = 0; i < this.cpuPressure; i++, j++) {
            j++;
            if (j >= arraySize) {
                j = 0;
            }
            try {
                if (longs[j] > Integer.MAX_VALUE) {
                    longs[j] = (long) Integer.MIN_VALUE;
                }
            } catch (Exception ignored) {};
        }
    }
}
