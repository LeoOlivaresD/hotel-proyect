import { useState } from "react";
import "./index.css";
import Sidebar from "./components/Sidebar";
import Dashboard from "./pages/Dashboard";
import Habitaciones from "./pages/Habitaciones";
import NuevaReserva from "./pages/NuevaReserva";
const pageTitles = { dashboard:"Dashboard", habitaciones:"Habitaciones", reservas:"Nueva Reserva" };
export default function App() {
  const [page, setPage] = useState("dashboard");
  const [habPre, setHabPre] = useState(null);
  const [toast, setToast] = useState(null);
  function showToast(msg, type="") { setToast({msg,type}); setTimeout(()=>setToast(null),3500); }
  function handleReservar(hab) { setHabPre(hab); setPage("reservas"); }
  function handleConfirmar(form, hab, total) {
    showToast(`Reserva confirmada — $${total}`, "success");
    setTimeout(() => { setPage("dashboard"); setHabPre(null); }, 2000);
  }
  return (
    <div className="app">
      <Sidebar page={page} setPage={p => { setPage(p); setHabPre(null); }} />
      <div className="main-content">
        <header className="topbar">
          <span className="topbar-title">{pageTitles[page]}</span>
          <div className="topbar-right">
            <span className="badge">Hotel Pacific Reef</span>
            <div className="avatar">LO</div>
          </div>
        </header>
        {page==="dashboard"    && <Dashboard />}
        {page==="habitaciones" && <Habitaciones onReservar={handleReservar} />}
        {page==="reservas"     && <NuevaReserva habPreseleccionada={habPre} onBack={()=>setPage("habitaciones")} onConfirmar={handleConfirmar} />}
      </div>
      {toast && <div className={`toast ${toast.type}`}>{toast.msg}</div>}
    </div>
  );
}
