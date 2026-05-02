import { useEffect, useState } from "react";
import { api } from "../services/api";

const ocupacionData = [
  { tipo: "Simple",       pct: 78 },
  { tipo: "Doble",        pct: 91 },
  { tipo: "Suite",        pct: 55 },
  { tipo: "Junior Suite", pct: 40 },
];

function estadoPill(estado) {
  const map = { confirmada: "pill-success", pendiente: "pill-warning", cancelada: "pill-danger" };
  const labels = { confirmada: "✓ Confirmada", pendiente: "⏳ Pendiente", cancelada: "✗ Cancelada" };
  return <span className={`pill ${map[estado] || "pill-info"}`}>{labels[estado] || estado}</span>;
}

export default function Dashboard() {
  const [stats, setStats]     = useState(null);
  const [reservas, setReservas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError]     = useState(null);

  useEffect(() => {
    Promise.all([api.getDashboardStats(), api.getReservas()])
      .then(([s, r]) => { setStats(s); setReservas(r); })
      .catch(err => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="page"><p style={{color:"var(--gray-400)"}}>Cargando datos…</p></div>;
  if (error)   return (
    <div className="page">
      <div style={{background:"var(--danger-light)",color:"var(--danger)",padding:16,borderRadius:8}}>
        Error al conectar con el backend: {error}
        <br/><small>Asegúrate de que el backend Spring Boot está corriendo en http://localhost:8080</small>
      </div>
    </div>
  );

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h2>Dashboard</h2>
          <p>Resumen del sistema — Hotel Pacific Reef</p>
        </div>
        <span className="badge">En vivo</span>
      </div>

      <div className="kpi-grid">
        <div className="kpi-card">
          <div className="kpi-label">Reservas activas</div>
          <div className="kpi-value">{stats?.reservasActivas ?? "—"}</div>
          <div className="kpi-change up">▲ Confirmadas</div>
        </div>
        <div className="kpi-card">
          <div className="kpi-label">Habitaciones ocupadas</div>
          <div className="kpi-value">{stats?.habitacionesOcupadas ?? "—"}</div>
          <div className="kpi-change up">▲ En uso</div>
        </div>
        <div className="kpi-card">
          <div className="kpi-label">Ingresos (confirmadas)</div>
          <div className="kpi-value">${stats?.ingresosMes?.toLocaleString() ?? "—"}</div>
          <div className="kpi-change up">▲ Total acumulado</div>
        </div>
        <div className="kpi-card">
          <div className="kpi-label">Check-ins hoy</div>
          <div className="kpi-value">{stats?.checkinshoy ?? "—"}</div>
          <div className="kpi-change">Fecha actual</div>
        </div>
      </div>

      <div className="dash-grid">
        <div className="card">
          <div className="card-header"><h3>Ocupación por tipo de habitación</h3></div>
          <div className="card-body">
            {ocupacionData.map((d) => (
              <div className="bar-row" key={d.tipo}>
                <span className="bar-label">{d.tipo}</span>
                <div className="bar-track">
                  <div className="bar-fill" style={{ width: `${d.pct}%` }} />
                </div>
                <span className="bar-pct">{d.pct}%</span>
              </div>
            ))}
          </div>
        </div>

        <div className="card">
          <div className="card-header">
            <h3>Últimas reservas</h3>
            <span style={{fontSize:11,color:"var(--gray-400)"}}>desde la BD</span>
          </div>
          <div className="table-wrapper">
            <table>
              <thead>
                <tr><th>#</th><th>Cliente</th><th>Habitación</th><th>Estado</th></tr>
              </thead>
              <tbody>
                {reservas.slice(0,5).map((r) => (
                  <tr key={r.id}>
                    <td style={{color:"var(--gray-400)",fontWeight:500}}>#{r.id}</td>
                    <td>{r.cliente?.nombre} {r.cliente?.apellido}</td>
                    <td>{r.habitacion?.numero} — {r.habitacion?.tipo}</td>
                    <td>{estadoPill(r.estado)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}
