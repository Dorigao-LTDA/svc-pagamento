package com.dorigao.pagamento.controller;

import com.dorigao.pagamento.model.PagamentoDto;
import com.dorigao.pagamento.service.PagamentoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pagamento")
public class PagamentoController {

    private static final Logger log = LoggerFactory.getLogger(PagamentoController.class);
    private final PagamentoService service;

    public PagamentoController(PagamentoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PagamentoDto>> listar() {
        log.info("GET /api/pagamento");
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagamentoDto> buscarPorId(@PathVariable UUID id) {
        log.info("GET /api/pagamento/{}", id);
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PagamentoDto> criar(@Valid @RequestBody CriarPagamentoRequest request) {
        log.info("POST /api/pagamento - pedidoId={}, valor={}", request.pedidoId(), request.valor());
        var pagamento = service.criar(request.pedidoId(), request.valor(), request.metodoPagamento());
        return ResponseEntity.status(HttpStatus.CREATED).body(pagamento);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PagamentoDto> atualizarStatus(@PathVariable UUID id, @RequestParam String status) {
        log.info("PATCH /api/pagamento/{}/status - status={}", id, status);
        return service.atualizarStatus(id, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public record CriarPagamentoRequest(
        @NotNull UUID pedidoId,
        @Positive BigDecimal valor,
        @NotBlank String metodoPagamento
    ) {}
}
