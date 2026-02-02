package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private TransferRepository transferRepository;

    @InjectMocks
    private TransferService transferService;

    @Test
    void transferMovesFundsBetweenOwnCards() {
        User user = User.builder().id(1L).username("user").build();
        Card from = Card.builder()
                .id(1L)
                .user(user)
                .balance(new BigDecimal("100.00"))
                .status(CardStatus.ACTIVE)
                .expirationDate(LocalDate.now().plusDays(1))
                .build();
        Card to = Card.builder()
                .id(2L)
                .user(user)
                .balance(new BigDecimal("10.00"))
                .status(CardStatus.ACTIVE)
                .expirationDate(LocalDate.now().plusDays(10))
                .build();

        when(cardRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(from));
        when(cardRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(to));
        when(transferRepository.save(any(Transfer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("25.00"));
        var response = transferService.transfer("user", request);

        assertThat(from.getBalance()).isEqualByComparingTo("75.00");
        assertThat(to.getBalance()).isEqualByComparingTo("35.00");
        assertThat(response.fromCardId()).isEqualTo(1L);
        assertThat(response.toCardId()).isEqualTo(2L);
        assertThat(response.amount()).isEqualByComparingTo("25.00");
        verify(transferRepository).save(any(Transfer.class));
    }

    @Test
    void transferFailsWhenInsufficientFunds() {
        User user = User.builder().id(1L).username("user").build();
        Card from = Card.builder()
                .id(1L)
                .user(user)
                .balance(new BigDecimal("10.00"))
                .status(CardStatus.ACTIVE)
                .expirationDate(LocalDate.now().plusDays(1))
                .build();
        Card to = Card.builder()
                .id(2L)
                .user(user)
                .balance(new BigDecimal("10.00"))
                .status(CardStatus.ACTIVE)
                .expirationDate(LocalDate.now().plusDays(1))
                .build();

        when(cardRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(from));
        when(cardRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(to));

        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("25.00"));

        assertThatThrownBy(() -> transferService.transfer("user", request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Insufficient funds");
    }
}
