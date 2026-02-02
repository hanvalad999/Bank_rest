package com.example.bankcards.service;

import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.CardNumberMasker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final UserService userService;

    public CardService(CardRepository cardRepository, UserService userService) {
        this.cardRepository = cardRepository;
        this.userService = userService;
    }

    @Transactional
    public CardResponse createCard(CardCreateRequest request) {
        validateCardNumber(request.cardNumber());
        User user = userService.getUserEntityById(request.userId());
        CardStatus status = request.expirationDate().isBefore(LocalDate.now()) ? CardStatus.EXPIRED : CardStatus.ACTIVE;
        Card card = Card.builder()
                .user(user)
                .cardNumber(request.cardNumber())
                .balance(request.initialBalance())
                .status(status)
                .expirationDate(request.expirationDate())
                .build();
        cardRepository.save(card);
        return toResponse(card);
    }

    public Page<CardResponse> getCardsForUser(String username, CardStatus status, Pageable pageable) {
        Page<Card> page;
        if (status == null) {
            page = cardRepository.findAllByUserUsername(username, pageable);
        } else {
            page = cardRepository.findAllByUserUsernameAndStatus(username, status, pageable);
        }
        return page.map(this::toResponse);
    }

    public Page<CardResponse> getAllCards(CardStatus status, Pageable pageable) {
        Page<Card> page = (status == null) ? cardRepository.findAll(pageable) : cardRepository.findAllByStatus(status, pageable);
        return page.map(this::toResponse);
    }

    public CardResponse getCard(Long id) {
        Card card = cardRepository.findById(id).orElseThrow(() -> new NotFoundException("Card not found"));
        return toResponse(card);
    }

    public CardResponse getCardForUser(Long id, String username) {
        Card card = cardRepository.findById(id).orElseThrow(() -> new NotFoundException("Card not found"));
        if (!card.getUser().getUsername().equals(username)) {
            throw new BadRequestException("Card does not belong to current user");
        }
        return toResponse(card);
    }

    @Transactional
    public CardResponse updateStatus(Long id, CardStatus status) {
        Card card = cardRepository.findById(id).orElseThrow(() -> new NotFoundException("Card not found"));
        card.setStatus(status);
        return toResponse(card);
    }

    @Transactional
    public CardResponse requestBlock(Long id, String username) {
        Card card = cardRepository.findById(id).orElseThrow(() -> new NotFoundException("Card not found"));
        if (!card.getUser().getUsername().equals(username)) {
            throw new BadRequestException("Card does not belong to current user");
        }
        card.setStatus(CardStatus.BLOCKED);
        return toResponse(card);
    }

    public BigDecimal getBalance(Long id) {
        Card card = cardRepository.findById(id).orElseThrow(() -> new NotFoundException("Card not found"));
        return card.getBalance();
    }

    @Transactional
    public void deleteCard(Long id) {
        if (!cardRepository.existsById(id)) {
            throw new NotFoundException("Card not found");
        }
        cardRepository.deleteById(id);
    }

    public Card getCardEntityForUpdate(Long id) {
        return cardRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new NotFoundException("Card not found"));
    }

    private void validateCardNumber(String cardNumber) {
        if (!cardNumber.matches("\\d{12,19}")) {
            throw new BadRequestException("Card number must contain 12-19 digits");
        }
    }

    private CardResponse toResponse(Card card) {
        return new CardResponse(
                card.getId(),
                card.getUser().getId(),
                card.getUser().getUsername(),
                CardNumberMasker.mask(card.getCardNumber()),
                card.getBalance(),
                card.getStatus().name(),
                card.getExpirationDate()
        );
    }
}
