package it.giorgiaauroraadorni.booktique.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

@Component
public class BookTestFactory implements EntityTestFactory<Book> {
    @Autowired
    private EntityTestFactory<Author> authorFactory;

    @Override
    public Book createValidEntity(int idx) {
        var book = new Book();
        var author = authorFactory.createValidEntity(idx);

        // mandatory attribute
        book.setIsbn("978-00-00-00000-" + idx);
        book.setTitle("Titolo");
        book.setPublisher("Editore");

        // other attributes
        book.setSubtitle("Sottotitolo");
        book.setBookFormat(Book.Format.HARDCOVER);
        book.setEdition(1);
        book.setLanguage("Lingua");
        book.setPublicationDate(LocalDate.of(1999, 1, 1));

        // association withs authors
        Set<Author> authors = Set.of(author);
        book.setAuthors(authors);

        // the bidirectional self-association between prequel and sequel isn't created, so the attribute is initially null
        
        return book;
    }

    @Override
    public void updateValidEntity(Book entity) {

    }
}
