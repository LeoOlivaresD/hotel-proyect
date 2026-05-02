const navItems = [
  { id: "dashboard",    label: "Dashboard",      icon: "📊" },
  { id: "habitaciones", label: "Habitaciones",   icon: "🛏️" },
  { id: "reservas",     label: "Nueva Reserva",  icon: "📋" },
];
export default function Sidebar({ page, setPage }) {
  return (
    <aside className="sidebar">
      <div className="sidebar-logo">
        <h2>Hotel Pacific Reef</h2>
        <span>Sistema de Gestión</span>
      </div>
      <nav className="sidebar-nav">
        {navItems.map(item => (
          <button key={item.id} className={`nav-item ${page===item.id?"active":""}`} onClick={() => setPage(item.id)}>
            <span className="nav-icon">{item.icon}</span>{item.label}
          </button>
        ))}
      </nav>
      <div style={{padding:"12px 16px",borderTop:"1px solid rgba(255,255,255,.1)"}}>
        <div style={{display:"flex",alignItems:"center",gap:10}}>
          <div className="avatar" style={{width:32,height:32,fontSize:12}}>LO</div>
          <div>
            <div style={{color:"white",fontSize:12,fontWeight:500}}>Leonardo Olivares</div>
            <div style={{color:"var(--gray-400)",fontSize:11}}>Scrum Master</div>
          </div>
        </div>
      </div>
    </aside>
  );
}
