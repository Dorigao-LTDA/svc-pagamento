package com.dorigao.pagamento.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PagamentoDto(
    UUID id,
    @NotBlank UUID pedidoId,
    @Positive BigDecimal valor,
    String status,
    String metodoPagamento,
    Instant createdAt,
    Instant updatedAt
) {
    public static PagamentoDto criar(UUID pedidoId, BigDecimal valor, String metodoPagamento) {
        var agora = Instant.now();
        return new PagamentoDto(
            UUID.randomUUID(),
            pedidoId,
            valor,
            "PENDENTE",
            metodoPagamento,
            agora,
            agora
        );
    }
}
