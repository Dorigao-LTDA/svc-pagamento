package com.dorigao.pagamento;

import com.dorigao.pagamento.controller.PagamentoController.CriarPagamentoRequest;
import com.dorigao.pagamento.service.PagamentoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PagamentoApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PagamentoService service;

    @Test
    void healthEndpointShouldReturnUp() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void readinessEndpointShouldReturnUp() throws Exception {
        mockMvc.perform(get("/health/readiness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void shouldListAllPagamentoDtos() throws Exception {
        mockMvc.perform(get("/api/pagamento"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].valor", notNullValue()));
    }

    @Test
    void shouldCreatePagamentoDto() throws Exception {
        String json = """
            {
                "pedidoId": "550e8400-e29b-41d4-a716-446655440000",
                "valor": 99.90,
                "metodoPagamento": "PIX"
            }
            """;

        mockMvc.perform(post("/api/pagamento")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pedidoId").value("550e8400-e29b-41d4-a716-446655440000"))
                .andExpect(jsonPath("$.valor").value(99.90))
                .andExpect(jsonPath("$.metodoPagamento").value("PIX"))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void shouldReturn404ForInvalidId() throws Exception {
        mockMvc.perform(get("/api/pagamento/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnMetrics() throws Exception {
        mockMvc.perform(get("/metrics"))
                .andExpect(status().isOk());
    }
}
