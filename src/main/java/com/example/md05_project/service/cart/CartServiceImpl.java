package com.example.md05_project.service.cart;

import com.example.md05_project.exception.BookException;
import com.example.md05_project.exception.CustomException;
import com.example.md05_project.exception.UserNotFoundException;
import com.example.md05_project.model.dto.request.CartRequestDTO;
import com.example.md05_project.model.dto.response.BookResponseDTO;
import com.example.md05_project.model.dto.response.CartResponseDTO;
import com.example.md05_project.model.dto.response.userResponse.UserResponseDTO;
import com.example.md05_project.model.entity.Book;
import com.example.md05_project.model.entity.Cart;
import com.example.md05_project.model.entity.User;
import com.example.md05_project.repository.AuthorRepository;
import com.example.md05_project.repository.BookRepository;
import com.example.md05_project.repository.CartRepository;
import com.example.md05_project.repository.GenreRepository;
import com.example.md05_project.service.author.AuthorService;
import com.example.md05_project.service.book.BookService;
import com.example.md05_project.service.genre.GenreService;
import com.example.md05_project.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    @Value("${books.max_allowed}")
    private int max_allowed_books;
    @Value(("${books.max_allowed_days}"))
    private int max_allowed_days;
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;
    @Autowired
    private AuthorService authorService;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookRepository bookRepository;

    @Override
    public CartResponseDTO findBookById(Long bookId, Long userId) {
        Cart cart = cartRepository.findBookById(bookId, userId);
        return new CartResponseDTO(cart);
    }

    @Override
    public List<CartResponseDTO> findAllByUserId(Long userId) throws CustomException {
        List<Cart> list = cartRepository.findAllByUserId(userId);
        if (list.isEmpty()) {
            throw new CustomException("Don't have any books in your cart");
        }
        return list.stream().map(CartResponseDTO::new).toList();
    }

    @Override
    public void deleteByUserId(Long userId) {
        cartRepository.deleteByUserId(userId);
    }

    @Override
    public CartResponseDTO findById(Long id) throws CustomException {
        Cart cart = cartRepository.findById(id).orElseThrow(() ->
                new CustomException("Cart has not been existed"));
        return CartResponseDTO.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .book(cart.getBook().getTitle())
                .build();
    }

    @Override
    public void delete(Long id) {
        cartRepository.deleteById(id);
    }

    @Override
    public CartResponseDTO save(CartRequestDTO cartRequestDTO) throws UserNotFoundException, BookException, CustomException {
        UserResponseDTO userResponseDTO = userService.findById(cartRequestDTO.getUserId());
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

        BookResponseDTO bookResponseDTO = bookService.findById(cartRequestDTO.getBookId());
        Book book = Book.builder()
                .id(bookResponseDTO.getId())
                .unitPrice(bookResponseDTO.getUnitPrice())
                .genre(genreRepository.findGenreByGenreName(bookResponseDTO.getGenre()))
                .image(bookResponseDTO.getImage())
                .stock(bookResponseDTO.getStock())
                .title(bookResponseDTO.getTitle())
                .authors(bookResponseDTO.getAuthors().stream().map(author -> authorRepository.findAuthorByName(author)).collect(Collectors.toSet()))
                .createdAt(bookResponseDTO.getCreatedAt())
                .description(bookResponseDTO.getDescription())
                .status(bookResponseDTO.isStatus())
                .build();

        List<CartResponseDTO> cartResponseDTOS = cartRepository.findAllByUserId(cartRequestDTO.getUserId())
                .stream().map(CartResponseDTO::new).toList();


        if (!cartResponseDTOS.isEmpty() && cartResponseDTOS.size() >= max_allowed_books) {
            throw new CustomException("Maximum of books added to cart is " + max_allowed_books);
        }

        if (cartResponseDTOS.stream().anyMatch(cart ->bookRepository.findBookByTitle(cart.getBook()).getId().equals(cartRequestDTO.getBookId()))) {
            throw new CustomException("Book existed in your waitingList");
        }
        if (!bookService.findById(cartRequestDTO.getBookId()).isStatus() ||
                bookService.findById(cartRequestDTO.getBookId()).getStock() == 0) {
            throw new CustomException("Can't add book to waitingList");
        }
        //
        if(!genreRepository.findGenreByGenreName(bookService.findById(cartRequestDTO.getBookId()).getGenre()).isStatus()){
            throw new CustomException("Can't add book to waitingList");
        }


        Cart cart = Cart.builder()
                .id(cartRequestDTO.getId())
                .user(user)
                .book(book)
                .build();
        Cart newCart = cartRepository.save(cart);
//        bookService.decreaseStock(book.getId());

        return new CartResponseDTO(newCart);
    }
}
