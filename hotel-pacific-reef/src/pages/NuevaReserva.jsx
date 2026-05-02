import { useState, useEffect } from "react";
import { api } from "../services/api";

export default function NuevaReserva({ habPreseleccionada, onBack, onConfirmar }) {
  const [disponibles, setDisponibles] = useState([]);
  const [loading,     setLoading]     = useState(false);
  const [errors,      setErrors]      = useState({});
  const [submitted,   setSubmitted]   = useState(false);
  const [apiError,    setApiError]    = useState(null);

  const [form, setForm] = useState({
    nombre: "", apellido: "", rut: "", correo: "", telefono: "",
    habitacion_id: habPreseleccionada ? String(habPreseleccionada.id) : "",
    huespedes: 1, fecha_entrada: "", fecha_salida: "",
    observaciones: "", metodo_pago: "credito", estado_pago: "pendiente",
  });

  useEffect(() => {
    api.getHabitaciones("disponible", null).then(setDisponibles).catch(() => {});
  }, []);

  const habSeleccionada = habPreseleccionada?.id === Number(form.habitacion_id)
    ? habPreseleccionada
    : disponibles.find(h => h.id === Number(form.habitacion_id));

  const noches = (() => {
    if (!form.fecha_entrada || !form.fecha_salida) return 0;
    const diff = new Date(form.fecha_salida) - new Date(form.fecha_entrada);
    return Math.max(0, Math.floor(diff / 86400000));
  })();
  const subtotal = habSeleccionada ? habSeleccionada.precio * noches : 0;
  const impuesto = Math.round(subtotal * 0.19);
  const total    = subtotal + impuesto;
  const anticipo = Math.round(total * 0.30);

  function validate() {
    const e = {};
    if (!form.nombre.trim())        e.nombre        = "Requerido";
    if (!form.apellido.trim())      e.apellido      = "Requerido";
    if (!form.rut.trim())           e.rut           = "Requerido";
    if (!form.correo.includes("@")) e.correo        = "Correo inválido";
    if (!form.habitacion_id)        e.habitacion_id = "Selecciona una habitación";
    if (!form.fecha_entrada)        e.fecha_entrada = "Requerido";
    if (!form.fecha_salida)         e.fecha_salida  = "Requerido";
    if (noches <= 0 && form.fecha_entrada && form.fecha_salida)
                                    e.fecha_salida  = "La salida debe ser posterior a la entrada";
    return e;
  }

  function handleChange(e) {
    const { name, value } = e.target;
    setForm(f => ({ ...f, [name]: value }));
    setErrors(er => ({ ...er, [name]: undefined }));
    setApiError(null);
  }

  async function handleSubmit() {
    const e = validate();
    if (Object.keys(e).length > 0) { setErrors(e); return; }
    setLoading(true);
    setApiError(null);
    try {
      const payload = {
        ...form,
        habitacion_id: Number(form.habitacion_id),
        huespedes: Number(form.huespedes),
        fecha_entrada: form.fecha_entrada,
        fecha_salida:  form.fecha_salida,
      };
      const result = await api.crearReserva(payload);
      setSubmitted(true);
      setTimeout(() => onConfirmar(form, habSeleccionada, result.total), 1500);
    } catch (err) {
      setApiError(err.message);
    } finally {
      setLoading(false);
    }
  }

  if (submitted) {
    return (
      <div className="page" style={{display:"flex",alignItems:"center",justifyContent:"center",minHeight:400}}>
        <div style={{textAlign:"center"}}>
          <div style={{fontSize:56,marginBottom:16}}>✅</div>
          <h3 style={{fontSize:20,fontWeight:700,marginBottom:8}}>¡Reserva confirmada!</h3>
          <p style={{color:"var(--gray-500)"}}>Se enviará un ticket con código QR al correo <strong>{form.correo}</strong></p>
        </div>
      </div>
    );
  }

  return (
    <div className="page">
      <button className="back-link" onClick={onBack}>← Volver a habitaciones</button>
      <div className="page-header">
        <div><h2>Nueva Reserva</h2><p>Completa los datos para confirmar la reserva</p></div>
      </div>

      {apiError && (
        <div style={{background:"var(--danger-light)",color:"var(--danger)",padding:12,borderRadius:8,marginBottom:16,fontSize:13}}>
          ⚠️ {apiError}
        </div>
      )}

      <div className="reserva-layout">
        <div>
          <div className="card section-divider">
            <div className="card-header"><h3>Datos del cliente</h3></div>
            <div className="card-body">
              <div className="form-grid">
                <Field label="Nombre"          name="nombre"   value={form.nombre}   onChange={handleChange} error={errors.nombre}   />
                <Field label="Apellido"         name="apellido" value={form.apellido} onChange={handleChange} error={errors.apellido} />
                <Field label="RUT / Pasaporte"  name="rut"      value={form.rut}      onChange={handleChange} error={errors.rut} placeholder="12.345.678-9" />
                <Field label="Teléfono"         name="telefono" value={form.telefono} onChange={handleChange} placeholder="+56 9 1234 5678" />
                <div className="form-group full">
                  <label>Correo electrónico</label>
                  <input type="email" name="correo" value={form.correo} onChange={handleChange} placeholder="correo@ejemplo.com" />
                  {errors.correo && <span style={{color:"var(--danger)",fontSize:11}}>{errors.correo}</span>}
                </div>
              </div>
            </div>
          </div>

          <div className="card section-divider">
            <div className="card-header"><h3>Detalle de la reserva</h3></div>
            <div className="card-body">
              <div className="form-grid">
                <div className="form-group">
                  <label>Habitación</label>
                  <select name="habitacion_id" value={form.habitacion_id} onChange={handleChange}>
                    <option value="">Seleccionar…</option>
                    {disponibles.map(h => (
                      <option key={h.id} value={h.id}>{h.numero} — {h.tipo} (${h.precio}/noche)</option>
                    ))}
                  </select>
                  {errors.habitacion_id && <span style={{color:"var(--danger)",fontSize:11}}>{errors.habitacion_id}</span>}
                </div>
                <div className="form-group">
                  <label>Número de huéspedes</label>
                  <select name="huespedes" value={form.huespedes} onChange={handleChange}>
                    {[1,2,3,4].map(n => <option key={n} value={n}>{n} {n===1?"huésped":"huéspedes"}</option>)}
                  </select>
                </div>
                <Field label="Fecha de ingreso" name="fecha_entrada" type="date" value={form.fecha_entrada} onChange={handleChange} error={errors.fecha_entrada} />
                <Field label="Fecha de salida"  name="fecha_salida"  type="date" value={form.fecha_salida}  onChange={handleChange} error={errors.fecha_salida}  />
                <div className="form-group full">
                  <label>Observaciones</label>
                  <textarea name="observaciones" value={form.observaciones} onChange={handleChange} placeholder="Peticiones especiales, alergias, etc." />
                </div>
              </div>
            </div>
          </div>

          <div className="card">
            <div className="card-header"><h3>Método de pago</h3></div>
            <div className="card-body">
              <div className="form-grid">
                <div className="form-group">
                  <label>Tipo de pago</label>
                  <select name="metodo_pago" value={form.metodo_pago} onChange={handleChange}>
                    <option value="credito">Tarjeta de crédito</option>
                    <option value="debito">Tarjeta de débito</option>
                    <option value="transferencia">Transferencia bancaria</option>
                    <option value="efectivo">Efectivo</option>
                  </select>
                </div>
                <div className="form-group">
                  <label>Estado del pago</label>
                  <select name="estado_pago" value={form.estado_pago} onChange={handleChange}>
                    <option value="pendiente">Pendiente de pago</option>
                    <option value="pagado">Pagado</option>
                  </select>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div className="summary-card">
          <div className="section-title">Resumen de reserva</div>
          {habSeleccionada ? (
            <div className="summary-room">
              <div className="summary-room-name">{habSeleccionada.tipo} — Hab. {habSeleccionada.numero}</div>
              <div className="summary-room-sub">Piso {habSeleccionada.piso} · {habSeleccionada.m2} m²</div>
            </div>
          ) : (
            <div className="summary-room" style={{color:"var(--gray-400)",fontSize:12}}>Selecciona una habitación</div>
          )}
          <div className="price-row"><span>Noches</span><span>{noches || "—"}</span></div>
          <div className="price-row"><span>Tarifa / noche</span><span>{habSeleccionada ? `$${habSeleccionada.precio}` : "—"}</span></div>
          <div className="price-row"><span>Subtotal</span><span>{subtotal ? `$${subtotal}` : "—"}</span></div>
          <div className="price-row"><span>IVA (19%)</span><span>{impuesto ? `$${impuesto}` : "—"}</span></div>
          <div className="price-total"><span>Total</span><span>{total ? `$${total}` : "—"}</span></div>
          {total > 0 && (
            <div style={{marginTop:10,padding:"8px 12px",background:"var(--warning-light)",borderRadius:"var(--radius)",fontSize:12,color:"var(--warning)"}}>
              💳 Anticipo requerido (30%): <strong>${anticipo}</strong>
            </div>
          )}
          <button className="btn btn-primary btn-full" style={{marginTop:16}} onClick={handleSubmit} disabled={loading}>
            {loading ? "Confirmando…" : "Confirmar reserva"}
          </button>
          <p style={{fontSize:11,color:"var(--gray-400)",textAlign:"center",marginTop:8}}>
            Se enviará confirmación con código QR por correo
          </p>
        </div>
      </div>
    </div>
  );
}

function Field({ label, name, value, onChange, error, type="text", placeholder="" }) {
  return (
    <div className="form-group">
      <label>{label}</label>
      <input type={type} name={name} value={value} onChange={onChange} placeholder={placeholder} />
      {error && <span style={{color:"var(--danger)",fontSize:11}}>{error}</span>}
    </div>
  );
}
