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

    private LocalDateTime fecha = LocalDateTime.now();

    // 🔗 RELACIÓN: Muchas donaciones pueden ser hechas por un mismo Usuario
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // 🔗 RELACIÓN: Muchas donaciones pueden ir destinadas a una misma Campaña
    @ManyToOne
    @JoinColumn(name = "campana_id", nullable = false)
    private Campana campana;

    public Donacion() {}

    public Donacion(Double monto, Usuario usuario, Campana campana) {
        this.monto = monto;
        this.usuario = usuario;
        this.campana = campana;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Campana getCampana() { return campana; }
    public void setCampana(Campana campana) { this.campana = campana; }
}