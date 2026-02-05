package com.example.bankcards.controller;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransferResponse;
import com.example.bankcards.service.TransferService;
import com.example.bankcards.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    /**
     * Выполняет перевод средств между картами текущего пользователя.
     *
     * @param request данные перевода
     * @return информация о выполненном переводе
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public TransferResponse transfer(@Valid @RequestBody TransferRequest request) {
        return transferService.transfer(SecurityUtil.getCurrentUsername(), request);
    }
}