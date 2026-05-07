// Baseline test — carga constante para medir performance baseline
import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Rate } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://pagamento.app.svc.cluster.local:8080';

// Métricas personalizadas
const endpointDurations = {
  list: new Trend('pagamento_list_duration'),
  get: new Trend('pagamento_get_duration'),
  create: new Trend('pagamento_create_duration'),
};
const errors = new Rate('pagamento_errors');

export const options = {
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<300', 'p(99)<800'],
    http_reqs: ['rate>=50'],
    pagamento_errors: ['rate<0.05'],
  },
  scenarios: {
    baseline: {
      executor: 'constant-vus',
      vus: __ENV.VUS ? parseInt(__ENV.VUS) : 10,
      duration: __ENV.DURATION || '5m',
      gracefulStop: '30s',
    },
  },
};

// IDs capturados para operações de GET
let pagamentoIds = [];

export default function () {
  // Operações de leitura (70%)
  if (Math.random() < 0.7) {
    const listRes = http.get(`${BASE_URL}/api/pagamento`, { tags: { operation: 'list' } });
    endpointDurations.list.add(listRes.timings.duration);
    check(listRes, {
      'GET list 200': (r) => r.status === 200,
    }) || errors.add(1);

    // Extrair IDs para busca individual
    try {
      const ids = JSON.parse(listRes.body).map((p) => p.id);
      if (ids.length > 0) pagamentoIds = ids.slice(0, 5);
    } catch (e) { /* ignore */ }
  }

  // Busca por ID (15%)
  if (pagamentoIds.length > 0 && Math.random() < 0.15) {
    const id = pagamentoIds[Math.floor(Math.random() * pagamentoIds.length)];
    const getRes = http.get(`${BASE_URL}/api/pagamento/${id}`, { tags: { operation: 'get' } });
    endpointDurations.get.add(getRes.timings.duration);
    check(getRes, {
      'GET id 200 or 404': (r) => r.status === 200 || r.status === 404,
    }) || errors.add(1);
  }

  // Criação (10%)
  if (Math.random() < 0.1) {
    const createRes = http.post(`${BASE_URL}/api/pagamento`, JSON.stringify({
      pedidoId: '550e8400-e29b-41d4-a716-446655440000',
      valor: 99.90,
      metodoPagamento: 'PIX',
    }), {
      headers: { 'Content-Type': 'application/json' },
      tags: { operation: 'create' },
    });
    endpointDurations.create.add(createRes.timings.duration);
    check(createRes, {
      'POST status 201': (r) => r.status === 201,
    }) || errors.add(1);
  }

  // Health check (5%)
  if (Math.random() < 0.05) {
    http.get(`${BASE_URL}/health`, { tags: { operation: 'health' } });
  }

  sleep(0.5 + Math.random() * 1.5);
}
