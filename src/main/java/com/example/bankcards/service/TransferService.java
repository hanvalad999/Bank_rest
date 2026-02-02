package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransferResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class TransferService {

    private final CardRepository cardRepository;
    private final TransferRepository transferRepository;

    public TransferService(CardRepository cardRepository, TransferRepository transferRepository) {
        this.cardRepository = cardRepository;
        this.transferRepository = transferRepository;
    }

    @Transactional
    public TransferResponse transfer(String username, TransferRequest request) {
        if (request.fromCardId().equals(request.toCardId())) {
            throw new BadRequestException("Source and destination cards must differ");
        }
        Card from = cardRepository.findByIdForUpdate(request.fromCardId())
                .orElseThrow(() -> new BadRequestException("Source card not found"));
        Card to = cardRepository.findByIdForUpdate(request.toCardId())
                .orElseThrow(() -> new BadRequestException("Destination card not found"));
        validateOwnership(username, from, to);
        validateCardState(from);
        validateCardState(to);
        BigDecimal amount = request.amount();
        if (from.getBalance().compareTo(amount) < 0) {
            throw new BadRequestException("Insufficient funds");
        }
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        Transfer transfer = Transfer.builder()
                .fromCard(from)
                .toCard(to)
                .amount(amount)
                .createdAt(LocalDateTime.now())
                .build();
        transferRepository.save(transfer);

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
            card.setStatus(CardStatus.EXPIRED);
            throw new BadRequestException("Card is expired");
        }
    }
}
