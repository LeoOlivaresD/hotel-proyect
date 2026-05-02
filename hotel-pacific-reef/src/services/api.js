const BASE = "http://localhost:8080/api";

async function request(path, options = {}) {
  const res = await fetch(`${BASE}${path}`, {
    headers: { "Content-Type": "application/json" },
    ...options,
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || data.message || "Error del servidor");
  return data;
}

// ── Habitaciones ──────────────────────────────────────────────────────────────
export const api = {
  getHabitaciones: (estado, tipo) => {
    const params = new URLSearchParams();
    if (estado) params.append("estado", estado);
    if (tipo)   params.append("tipo", tipo);
    const qs = params.toString();
    return request(`/habitaciones${qs ? "?" + qs : ""}`);
  },

  // ── Reservas ──────────────────────────────────────────────────────────────
  getReservas: () => request("/reservas"),

  crearReserva: (body) => request("/reservas", {
    method: "POST",
    body: JSON.stringify(body),
  }),

  cancelarReserva: (id) => request(`/reservas/${id}/cancelar`, { method: "PATCH" }),

  // ── Dashboard ─────────────────────────────────────────────────────────────
  getDashboardStats: () => request("/reservas/dashboard"),
};
