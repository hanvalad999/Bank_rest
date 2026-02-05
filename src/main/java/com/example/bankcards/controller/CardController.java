package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CardStatusUpdateRequest;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    /**
     * Создает новую банковскую карту для указанного пользователя.
     *
     * @param request данные для создания карты
     * @return созданная карта
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public CardResponse createCard(@Valid @RequestBody CardCreateRequest request) {
        return cardService.createCard(request);
    }

    /**
     * Возвращает страницу карт с возможной фильтрацией по статусу.
     *
     * @param status   необязательный статус карты для фильтрации
     * @param pageable параметры пагинации
     * @return страница карт
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public Page<CardResponse> getAllCards(@RequestParam(required = false) CardStatus status, Pageable pageable) {
        return cardService.getAllCards(status, pageable);
    }

    /**
     * Возвращает карту по идентификатору.
     * <p>
     * Администратор может получать любую карту, обычный пользователь — только свои.
     *
     * @param id идентификатор карты
     * @return карта
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public CardResponse getCard(@PathVariable Long id) {
        if (SecurityUtil.hasRole("ADMIN")) {
            return cardService.getCard(id);
        }
        return cardService.getCardForUser(id, SecurityUtil.getCurrentUsername());
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/my")
    public Page<CardResponse> getMyCards(@RequestParam(required = false) CardStatus status, Pageable pageable) {
        return cardService.getCardsForUser(SecurityUtil.getCurrentUsername(), status, pageable);
    }

    /**
     * Возвращает баланс карты.
     * <p>
     * Администратор может запрашивать баланс любой карты, пользователь — только своей.
     *
     * @param id идентификатор карты
     * @return баланс карты
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}/balance")
    public BigDecimal getBalance(@PathVariable Long id) {
        if (SecurityUtil.hasRole("ADMIN")) {
            return cardService.getBalance(id);
        }
        CardResponse card = cardService.getCardForUser(id, SecurityUtil.getCurrentUsername());
        return card.balance();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public CardResponse updateStatus(@PathVariable Long id, @Valid @RequestBody CardStatusUpdateRequest request) {
        return cardService.updateStatus(id, request.status());
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{id}/block-request")
    public CardResponse requestBlock(@PathVariable Long id) {
        return cardService.requestBlock(id, SecurityUtil.getCurrentUsername());
    }

    /**
     * Удаляет карту по идентификатору.
     *
     * @param id идентификатор карты
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
    }
}
