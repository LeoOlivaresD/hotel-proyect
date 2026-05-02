import { useState, useEffect } from "react";
import { api } from "../services/api";

const estadoConfig = {
  disponible:    { label: "Disponible",    cls: "pill-success" },
  ocupada:       { label: "Ocupada",       cls: "pill-danger"  },
  mantenimiento: { label: "Mantenimiento", cls: "pill-warning" },
};

const tipos   = ["Todos", "Simple", "Doble", "Suite", "Junior Suite"];
const estados = ["Todos", "disponible", "ocupada", "mantenimiento"];

export default function Habitaciones({ onReservar }) {
  const [lista,       setLista]       = useState([]);
  const [loading,     setLoading]     = useState(true);
  const [error,       setError]       = useState(null);
  const [busqueda,    setBusqueda]    = useState("");
  const [filtrTipo,   setFiltrTipo]   = useState("Todos");
  const [filtrEstado, setFiltrEstado] = useState("Todos");

  const fetchHabitaciones = (estado, tipo) => {
    setLoading(true);
    api.getHabitaciones(
        estado !== "Todos" ? estado : null,
        tipo   !== "Todos" ? tipo   : null
      )
      .then(setLista)
      .catch(err => setError(err.message))
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchHabitaciones(filtrEstado, filtrTipo); }, [filtrEstado, filtrTipo]);

  const filtered = lista.filter(h =>
    h.numero.includes(busqueda) ||
    (h.tipo || "").toLowerCase().includes(busqueda.toLowerCase())
  );

  if (error) return (
    <div className="page">
      <div style={{background:"var(--danger-light)",color:"var(--danger)",padding:16,borderRadius:8}}>
        Error: {error}
      </div>
    </div>
  );

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h2>Habitaciones</h2>
          <p>{loading ? "Cargando…" : `${filtered.length} habitaciones encontradas`}</p>
        </div>
        <button className="btn btn-primary">+ Nueva habitación</button>
      </div>

      <div className="filters-bar">
        <input className="search-input" placeholder="Buscar por número o tipo…"
          value={busqueda} onChange={e => setBusqueda(e.target.value)} />
        {tipos.map(t => (
          <button key={t}
            className={`filter-btn ${filtrTipo === t ? "active" : ""}`}
            onClick={() => setFiltrTipo(t)}>{t}</button>
        ))}
        {estados.slice(1).map(e => (
          <button key={e}
            className={`filter-btn ${filtrEstado === e ? "active" : ""}`}
            onClick={() => setFiltrEstado(filtrEstado === e ? "Todos" : e)}>
            {estadoConfig[e].label}
          </button>
        ))}
      </div>

      {loading ? (
        <div className="empty-state"><div className="icon">⏳</div><p>Cargando habitaciones…</p></div>
      ) : filtered.length === 0 ? (
        <div className="empty-state"><div className="icon">🔍</div><p>No se encontraron habitaciones.</p></div>
      ) : (
        <div className="habitaciones-grid">
          {filtered.map(h => {
            const cfg = estadoConfig[h.estado] || estadoConfig.disponible;
            const amenidades = (h.amenidades || "").split(",").filter(Boolean);
            return (
              <div className="hab-card" key={h.id}>
                <div className="hab-card-img">
                  <span>{h.numero}</span>
                  <span className={`hab-status pill ${cfg.cls}`}>{cfg.label}</span>
                </div>
                <div className="hab-body">
                  <div className="hab-tipo">{h.tipo}</div>
                  <div className="hab-meta">
                    Piso {h.piso} · {h.capacidad} {h.capacidad === 1 ? "persona" : "personas"} · {h.m2} m²
                    <br/>
                    {amenidades.map(a => (
                      <span key={a} className="amenidad-tag">{a.trim()}</span>
                    ))}
                  </div>
                  <div className="hab-footer">
                    <div className="hab-precio">${h.precio} <span>/ noche</span></div>
                    {h.estado === "disponible" ? (
                      <button className="btn btn-primary btn-sm" onClick={() => onReservar(h)}>
                        Reservar
                      </button>
                    ) : (
                      <button className="btn btn-outline btn-sm">Ver detalle</button>
                    )}
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
