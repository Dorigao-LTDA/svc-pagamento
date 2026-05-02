package com.dorigao.pagamento.service;

import com.dorigao.pagamento.model.PagamentoDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PagamentoService {

    private static final Logger log = LoggerFactory.getLogger(PagamentoService.class);
    private final Map<UUID, PagamentoDto> pagamentos = new ConcurrentHashMap<>();

    public PagamentoService() {
        seed();
        log.info("PagamentoService inicializado com {} pagamentos seed", pagamentos.size());
    }

    public List<PagamentoDto> listarTodos() {
        log.debug("Listando todos os pagamentos");
        return new ArrayList<>(pagamentos.values());
    }

    public Optional<PagamentoDto> buscarPorId(UUID id) {
        log.debug("Buscando pagamento por id: {}", id);
        return Optional.ofNullable(pagamentos.get(id));
    }

    public PagamentoDto criar(UUID pedidoId, BigDecimal valor, String metodoPagamento) {
        var pagamento = PagamentoDto.criar(pedidoId, valor, metodoPagamento);
        pagamentos.put(pagamento.id(), pagamento);
        log.info("Pagamento criado: id={}, pedidoId={}, valor={}", pagamento.id(), pedidoId, valor);
        
        // Simular processamento
        simularProcessamento(pagamento);
        return pagamento;
    }

    public Optional<PagamentoDto> atualizarStatus(UUID id, String novoStatus) {
        return Optional.ofNullable(pagamentos.get(id)).map(existente -> {
            var atualizado = new PagamentoDto(
                existente.id(),
                existente.pedidoId(),
                existente.valor(),
                novoStatus,
                existente.metodoPagamento(),
                existente.createdAt(),
                java.time.Instant.now()
            );
            pagamentos.put(id, atualizado);
            log.info("Pagamento {} atualizado para status: {}", id, novoStatus);
            return atualizado;
        });
    }

    private void simularProcessamento(PagamentoDto pagamento) {
        // Simula processamento assíncrono de pagamento
        new Thread(() -> {
            try {
                Thread.sleep(2000 + (long)(Math.random() * 3000));
                atualizarStatus(pagamento.id(), "APROVADO");
                log.info("Pagamento {} aprovado", pagamento.id());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void seed() {
        var pedidosUUID = List.of(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID()
        );
        
        criar(pedidosUUID.get(0), new BigDecimal("8999.90"), "CARTAO_CREDITO");
        criar(pedidosUUID.get(1), new BigDecimal("149.90"), "PIX");
        criar(pedidosUUID.get(2), new BigDecimal("2499.90"), "BOLETO");
    }
}
