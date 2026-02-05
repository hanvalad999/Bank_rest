package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransferResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class TransferService {

    private static final Logger log = LoggerFactory.getLogger(TransferService.class);

    private final CardRepository cardRepository;
    private final TransferRepository transferRepository;

    public TransferService(CardRepository cardRepository, TransferRepository transferRepository) {
        this.cardRepository = cardRepository;
        this.transferRepository = transferRepository;
    }

    /**
     * Выполняет перевод средств между картами пользователя с учетом блокировок и повторов при конфликте блокировок.
     *
     * @param username имя текущего пользователя
     * @param request  данные перевода
     * @return информация о выполненном переводе
     */
    @Retryable(retryFor = {PessimisticLockingFailureException.class}, maxAttempts = 3, backoff = @Backoff(delay = 100))
    @Transactional
    public TransferResponse transfer(String username, TransferRequest request) {
        if (request.fromCardId().equals(request.toCardId())) {
            throw new BadRequestException("Source and destination cards must differ");
        }

        BigDecimal amount = request.amount();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Transfer amount must be positive");
        }

        // Блокируем карты в определенном порядке для предотвращения deadlock
        Long fromId = request.fromCardId();
        Long toId = request.toCardId();
        Long firstId = fromId < toId ? fromId : toId;
        Long secondId = fromId < toId ? toId : fromId;

        Card first = cardRepository.findByIdForUpdate(firstId)
                .orElseThrow(() -> new BadRequestException("Card not found"));
        Card second = cardRepository.findByIdForUpdate(secondId)
                .orElseThrow(() -> new BadRequestException("Card not found"));

        // Определяем, какая карта from, какая to
        Card from = first.getId().equals(fromId) ? first : second;
        Card to = first.getId().equals(toId) ? first : second;

        validateOwnership(username, from, to);
        validateCardState(from);
        validateCardState(to);

        if (from.getBalance().compareTo(amount) < 0) {
            throw new BadRequestException("Insufficient funds");
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        cardRepository.save(from);
        cardRepository.save(to);
        cardRepository.flush();

        Transfer transfer = Transfer.builder()
                .fromCard(from)
                .toCard(to)
                .amount(amount)
                .createdAt(LocalDateTime.now())
                .build();
        transferRepository.save(transfer);

        log.info("Transfer completed: transferId={}, fromCardId={}, toCardId={}, amount={}, username={}",
                transfer.getId(), from.getId(), to.getId(), amount, username);

        return new TransferResponse(
                transfer.getId(),
                from.getId(),
                to.getId(),
                transfer.getAmount(),
                transfer.getCreatedAt()
        );
    }

    private void validateOwnership(String username, Card from, Card to) {
        if (!from.getUser().getUsername().equals(username) || !to.getUser().getUsername().equals(username)) {
            throw new BadRequestException("Cards must belong to current user");
        }
    }

    private void validateCardState(Card card) {
        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new BadRequestException("Card is not active");
        }
        LocalDate expiration = card.getExpirationDate();
        if (expiration != null && expiration.isBefore(LocalDate.now())) {
            throw new BadRequestException("Card is expired");
        }
    }
}
