package com.example.md05_project.service.book;

import com.example.md05_project.exception.BookException;
import com.example.md05_project.exception.CustomException;
import com.example.md05_project.exception.GenreException;
import com.example.md05_project.model.dto.request.BookRequestDTO;
import com.example.md05_project.model.dto.response.BookResponseDTO;
import com.example.md05_project.model.dto.response.BorrowedCartResponseDTO;
import com.example.md05_project.model.dto.response.GenreResponseDTO;
import com.example.md05_project.model.entity.*;
import com.example.md05_project.repository.BookRepository;
import com.example.md05_project.repository.BorrowedCartDetailRepository;
import com.example.md05_project.repository.BorrowedCartRepository;
import com.example.md05_project.repository.GenreRepository;
import com.example.md05_project.service.author.AuthorService;
import com.example.md05_project.service.genre.GenreService;
import com.example.md05_project.service.upload.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorService authorService;
    @Autowired
    private GenreService genreService;
    @Autowired
    private UploadService uploadService;
    @Autowired
    private BorrowedCartRepository borrowedCartRepository;
    @Autowired
    private BorrowedCartDetailRepository borrowedCartDetailRepository;

    @Override
    public Page<BookResponseDTO> findAllWithPaginationAndSort(Pageable pageable) {
        Page<Book> list = bookRepository.findAll(pageable);
        return list.map(BookResponseDTO::new);
    }

    @Override
    public Page<BookResponseDTO> searchByNameWithPaginationAndSort(Pageable pageable, String name) {
        Page<Book> list = bookRepository.findAllByTitleContainingIgnoreCase(pageable, name);
        return list.map(BookResponseDTO::new);
    }

    @Override
    public BookResponseDTO findById(Long id) throws BookException {
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookException("Book is not found with this id " + id));
        return new BookResponseDTO(book);
    }

    @Override
    public BookResponseDTO saveOrUpdate(BookRequestDTO bookRequestDTO) throws BookException, GenreException, CustomException {
        String fileName = null;

        if (bookRequestDTO.getId() == null) {
            //check trung title
            if (bookRepository.existsByTitle(bookRequestDTO.getTitle())) {
                throw new BookException("Book's title existed");
            }

            //upload file
            if (bookRequestDTO.getFile() == null || bookRequestDTO.getFile().getSize() == 0) {
                throw new BookException("File image is not found");
            }
            fileName = uploadService.uploadImage(bookRequestDTO.getFile());
        } else {
            BookResponseDTO bookResponseDTO = findById(bookRequestDTO.getId());
            //check trung title
            boolean titleExist = bookRepository.findAll().stream().anyMatch(book ->
                    !bookRequestDTO.getTitle().equalsIgnoreCase(bookResponseDTO.getTitle())
                            && bookRequestDTO.getTitle().equalsIgnoreCase(book.getTitle()));
            if (titleExist) {
                throw new CustomException("Book's title existed");
            }

            //uploadfile
            //kiem tra xem co edit file anh khong
            if (bookRequestDTO.getFile() != null && bookRequestDTO.getFile().getSize() > 0) {
                fileName = uploadService.uploadImage(bookRequestDTO.getFile());
            } else {
                fileName = bookResponseDTO.getImage();
            }
        }

        //check Genre ton tai
        GenreResponseDTO genreResponseDTO = genreService.findById(bookRequestDTO.getGenreId());
        Genre genre = new Genre();
        if (genreResponseDTO != null) {
            genre = Genre.builder()
                    .id(genreResponseDTO.getId()).genreName(genreResponseDTO.getGenreName())
                    .status(genreResponseDTO.isStatus()).books(genreResponseDTO.getBooks())
                    .build();
        }

        //check Author
        Set<Author> authors = bookRequestDTO.getAuthors().stream().map(author -> authorService.findAuthorByName(author)).collect(Collectors.toSet());
        if (authors.isEmpty()) {
            throw new CustomException("Author id mandatory");
        }
        Book book = bookRepository.save(Book.builder()
                .id(bookRequestDTO.getId())
                .stock(bookRequestDTO.getStock()).status(bookRequestDTO.isStatus())
                .image(fileName).unitPrice(bookRequestDTO.getUnitPrice())
                .genre(genre).authors(authors).title(bookRequestDTO.getTitle())
                .description(bookRequestDTO.getDescription()).createdAt(bookRequestDTO.getCreatedAt())
                .build());
        return new BookResponseDTO(book);
    }

    @Override
    public void changeStatus(Long id) throws BookException {
        BookResponseDTO book = findById(id);
        if (book != null) {
            bookRepository.changeStatus(id);
        }
    }

    @Override
    public BookResponseDTO save(BookRequestDTO bookRequestDTO) throws BookException, GenreException, CustomException {
        //check trung title
        if (bookRepository.existsByTitle(bookRequestDTO.getTitle())) {
            throw new BookException("Book's title existed");
        }

        //check Genre ton tai
        GenreResponseDTO genreResponseDTO = genreService.findById(bookRequestDTO.getGenreId());
        Genre genre = new Genre();
        if (genreResponseDTO != null) {
            genre = Genre.builder()
                    .id(genreResponseDTO.getId()).genreName(genreResponseDTO.getGenreName())
                    .status(genreResponseDTO.isStatus()).books(genreResponseDTO.getBooks())
                    .build();
        }

        //check Author
        Set<Author> authors = bookRequestDTO.getAuthors().stream().map(author -> authorService.findAuthorByName(author)).collect(Collectors.toSet());
        if (authors.isEmpty()) {
            throw new CustomException("Author id mandatory");
        }

        //upload file
        if (bookRequestDTO.getFile() == null || bookRequestDTO.getFile().getSize() == 0) {
            throw new BookException("File image is not found");
        }
        String fileName = uploadService.uploadImage(bookRequestDTO.getFile());
        Book book = bookRepository.save(Book.builder()
                .stock(bookRequestDTO.getStock()).status(bookRequestDTO.isStatus())
                .image(fileName).unitPrice(bookRequestDTO.getUnitPrice())
                .genre(genre).authors(authors).title(bookRequestDTO.getTitle())
                .description(bookRequestDTO.getDescription()).createdAt(bookRequestDTO.getCreatedAt())
                .build());
        return new BookResponseDTO(book);
    }

    @Override
    public void increaseStock(Long bookId) {
        bookRepository.increaseStock(bookId);
    }

    @Override
    public void decreaseStock(Long bookId) throws BookException {
        BookResponseDTO bookResponseDTO = findById(bookId);
        if (bookResponseDTO.getStock() > 0) {
            bookRepository.decreaseStock(bookId);
        }
        if (bookResponseDTO.getStock() == 0) {
            bookRepository.setStatusFalse(bookId);
        }
    }

    @Override
    public List<BookResponseDTO> getBooksByGenreId(Long genreId) throws BookException {
        List<Book> list = bookRepository.findAllByGenre_Id(genreId);
        if (list.isEmpty()) {
            throw new BookException("Don't have any books belonging genre with id " + genreId);
        }
        return list.stream().map(BookResponseDTO::new).toList();
    }

    @Override
    public List<BookResponseDTO> findAllByStatus(boolean status) {
        List<Book> list = bookRepository.findAllByStatus(status);
        return list.stream().map(BookResponseDTO::new).toList();
    }

    @Override
    public Integer totalBorrowedBooks(List<BorrowedCartResponseDTO> list) {
        int total = 0;

        return total = list.stream().mapToInt(BorrowedCartResponseDTO::getCount).sum();
    }

    @Override
    public List<BookResponseDTO> getBestBorrowedBooks(List<BorrowedCartResponseDTO> list) throws CustomException {
        List<BookResponseDTO> bookResponseDTOList = new ArrayList<>();
        List<Book> books = bookRepository.findAll();
        for (Book book : books) {
            int countOfBorrowedBook = 0;
            for (BorrowedCartResponseDTO borrowedCartResponseDTO : list) {
                List<BorrowedCartDetail> cartDetailList = borrowedCartDetailRepository.findAllByBorrowedCart_Id(borrowedCartResponseDTO.getId());
                if (cartDetailList.stream().anyMatch(detail -> detail.getBook().getId().equals(book.getId()))) {
                    countOfBorrowedBook += 1;
                }
            }
            if (countOfBorrowedBook >= 2) {
                bookResponseDTOList.add(new BookResponseDTO(book));
            }
        }
        if(bookResponseDTOList.isEmpty()){
            throw  new CustomException("Dont have any books borrowed more than 2 times");
        }
        return bookResponseDTOList;
    }

}
