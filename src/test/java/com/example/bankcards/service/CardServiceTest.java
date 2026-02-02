package com.example.bankcards.service;

import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.repository.CardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CardService cardService;

    @Test
    void createCardSetsExpiredStatusWhenDateInPast() {
        User user = User.builder().id(1L).username("user").build();
        when(userService.getUserEntityById(1L)).thenReturn(user);
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CardCreateRequest request = new CardCreateRequest(
                1L,
                "123456789012",
                LocalDate.now().minusDays(1),
                new BigDecimal("0.00")
        );

        var response = cardService.createCard(request);

        assertThat(response.status()).isEqualTo(CardStatus.EXPIRED.name());
    }

    @Test
    void createCardRejectsInvalidCardNumber() {
        CardCreateRequest request = new CardCreateRequest(
                1L,
                "invalid",
                LocalDate.now().plusDays(1),
                new BigDecimal("10.00")
        );

        assertThatThrownBy(() -> cardService.createCard(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Card number must contain 12-19 digits");
    }
}
