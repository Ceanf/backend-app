package albergue.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "donaciones")
public class Donacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Double monto;

    @Column(nullable = false)
    private LocalDateTime fecha;

    // Ej: "Comida", "Salud", "Infraestructura"
    @Column(name = "tipo_impacto", nullable = false)
    private String tipoImpacto;

    // ==========================================
    // RELACIÓN 1: Muchas Donaciones -> Un Usuario
    // ==========================================
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // ==========================================
    // RELACIÓN 2: Muchas Donaciones -> Un Albergue
    // ==========================================
    @ManyToOne
    @JoinColumn(name = "albergue_id", nullable = false)
    private Albergue albergue;

    // Constructores
    public Donacion() {
    }

    public Donacion(Double monto, LocalDateTime fecha, String tipoImpacto, Usuario usuario, Albergue albergue) {
        this.monto = monto;
        this.fecha = fecha;
        this.tipoImpacto = tipoImpacto;
        this.usuario = usuario;
        this.albergue = albergue;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getTipoImpacto() {
        return tipoImpacto;
    }

    public void setTipoImpacto(String tipoImpacto) {
        this.tipoImpacto = tipoImpacto;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Albergue getAlbergue() {
        return albergue;
    }

    public void setAlbergue(Albergue albergue) {
        this.albergue = albergue;
    }
}