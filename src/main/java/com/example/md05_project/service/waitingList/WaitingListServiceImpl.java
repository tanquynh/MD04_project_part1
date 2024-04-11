package com.example.md05_project.service.waitingList;

import com.example.md05_project.exception.BookException;
import com.example.md05_project.exception.CustomException;
import com.example.md05_project.exception.UserNotFoundException;
import com.example.md05_project.model.dto.request.WaitingListRequestDTO;
import com.example.md05_project.model.dto.response.BookResponseDTO;
import com.example.md05_project.model.dto.response.WaitingListResponseDTO;
import com.example.md05_project.model.dto.response.userResponse.UserResponseDTO;
import com.example.md05_project.model.entity.Book;
import com.example.md05_project.model.entity.User;
import com.example.md05_project.model.entity.WaitingList;
import com.example.md05_project.repository.*;
import com.example.md05_project.service.book.BookService;
import com.example.md05_project.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WaitingListServiceImpl implements WaitingListService {
    @Autowired
    private WaitingListRepository waitingListRepository;
    @Autowired
    private BookService bookService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookRepository bookRepository;

    @Value("${books.max_wait}")
    private int max_wait_books;
    @Value("${books.max_wait_days}")
    private int max_wait_days;


    @Override
    public List<WaitingListResponseDTO> getAll(Long userId) throws CustomException {
        List<WaitingList> list = waitingListRepository.findAllByUserId(userId);
        if (list.isEmpty()) {
            throw new CustomException("Don't have any books in your waiting list");
        }
        return list.stream().map(WaitingListResponseDTO::new).toList();
    }

    @Override
    public WaitingListResponseDTO save(WaitingListRequestDTO waitingListRequestDTO) throws BookException, CustomException, UserNotFoundException {
        UserResponseDTO userResponseDTO = userService.findById(waitingListRequestDTO.getUserId());
        User user = User.builder()
                .id(userResponseDTO.getId())
                .roles(userResponseDTO.getRoles())
                .email(userResponseDTO.getEmail())
                .phone(userResponseDTO.getPhone())
                .address(userResponseDTO.getAddress())
                .fullName(userResponseDTO.getFullName())
                .username(userResponseDTO.getUsername())
                .status(userResponseDTO.isStatus())
                .build();

        BookResponseDTO bookResponseDTO = bookService.findById(waitingListRequestDTO.getBookId());
        Book book = Book.builder()
                .id(bookResponseDTO.getId())
                .unitPrice(bookResponseDTO.getUnitPrice())
//                .genre(genreRepository.findById(waitingListRequestDTO.getBookId()).orElse(null))
                .genre(genreRepository.findGenreByGenreName(bookResponseDTO.getGenre()))
                .image(bookResponseDTO.getImage())
                .stock(bookResponseDTO.getStock())
                .title(bookResponseDTO.getTitle())
                .authors(bookResponseDTO.getAuthors().stream().map(author -> authorRepository.findAuthorByName(author)).collect(Collectors.toSet()))
                .createdAt(bookResponseDTO.getCreatedAt())
                .description(bookResponseDTO.getDescription())
                .status(bookResponseDTO.isStatus())
                .build();

        List<WaitingListResponseDTO> waitingListResponseDTOList = waitingListRepository.findAllByUserId(waitingListRequestDTO.getUserId())
                .stream().map(WaitingListResponseDTO::new).toList();

        if (!waitingListResponseDTOList.isEmpty() && waitingListResponseDTOList.size() >= max_wait_books) {
            throw new CustomException("Maximum of books added to waiting list is " + max_wait_books);
        }

        if (waitingListResponseDTOList.stream().anyMatch(waitingBook -> bookRepository.findBookByTitle(waitingBook.getBook()).equals(waitingListRequestDTO.getBookId()))) {
            throw new CustomException("Book existed in your waitingList");
        }
        if (bookService.findById(waitingListRequestDTO.getBookId()).isStatus()
                || !genreRepository.findGenreByGenreName(bookService.findById(waitingListRequestDTO.getBookId()).getGenre()).isStatus()) {
            throw new CustomException("Can't add book to waitingList");
        }

        WaitingList waitingList = WaitingList.builder()
                .id(waitingListRequestDTO.getId())
                .user(user)
                .book(book)
                .build();

        WaitingList list = waitingListRepository.save(waitingList);
        return new WaitingListResponseDTO(list);
    }

    @Override
    public void delete(Long id) throws CustomException {
        WaitingListResponseDTO waitingListResponseDTO = findById(id);
        waitingListRepository.delete(WaitingList.builder()
                .id(waitingListResponseDTO.getId())
                .user(userRepository.findById(waitingListResponseDTO.getUserId()).orElse(null))
                .book(bookRepository.findBookByTitle(waitingListResponseDTO.getBook()))
                .build());
    }

    @Override
    public WaitingListResponseDTO findById(Long id) throws CustomException {
        WaitingList waitingList = waitingListRepository.findById(id).orElseThrow(() -> new CustomException("Waiting list has not been existed"));
        return new WaitingListResponseDTO(waitingList);
    }

    @Override
    public WaitingListResponseDTO findByBookId(Long bookId, Long userId) {
        return null;
    }

    @Override
    public Page<WaitingListResponseDTO> getAllWithPagination(Pageable pageable, Long userId) {
        Page<WaitingList> list = waitingListRepository.findAll(pageable);
        return null;
    }

    @Override
    public void deleteByUserId(Long userId) {
        waitingListRepository.deleteByUserId(userId);
    }
}
