package com.dynatrace.books.model;

import com.dynatrace.books.exception.BadRequestException;
import javax.persistence.*;

@Entity
@Table(name="books", uniqueConstraints = @UniqueConstraint(columnNames = "isbn"))
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="isbn", nullable = false, unique = true, length = 13)
    private String isbn;

    @Column(name="title", nullable = false)
    private String title;

    @Column(name="lang", nullable = false, length = 2)
    private String language;

    @Column(name="published", nullable = false)
    private boolean published;

    @Column(name="author", nullable = false)
    private String author;

    @Column(name="price", nullable = false, precision = 10, scale = 2)
    private double price;

//    @ManyToOne(fetch = FetchType.EAGER, optional = false)
//    @JoinColumn(name="author_id", nullable = false)
//    @OnDelete(action = OnDeleteAction.NO_ACTION)
//    @JsonIgnore
//    private Author author;
//
//    @ManyToOne(fetch = FetchType.EAGER, optional = false)
//    @JoinColumn(name="publisher_id", nullable = false)
//    @OnDelete(action = OnDeleteAction.NO_ACTION)
//    private Publisher publisher;

    public Book() {
    }

    public Book(long id, String isbn, String title, String author, String language, double price, boolean published) {
        this.id = id;
        this.setIsbn(isbn);
        this.setLanguage(language);
        this.setPrice(price);
        this.title = title;
        this.author = author;
        this.published = published;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        if (isbn.length() != 13 || !isbn.matches("^\\d{13}$")) {
            throw new BadRequestException("ISBN must be a 13-digits value");
        }
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public double getPrice() {
        return (double) Math.round(this.price * 100.0) / 100.0;
    }

    public void setPrice(double price) {
        price = (double) Math.round(price * 100.0) / 100.0;
        if (price <= 0 || price >= 1000000) {
            throw new BadRequestException("Invalid price. Must be between 0 and 1 million. Got: " + price);
        }
        this.price = price;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        if (language.length() != 2) {
            throw new BadRequestException("Country must be a 2-letter code");
        }
        this.language = language;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }
}
