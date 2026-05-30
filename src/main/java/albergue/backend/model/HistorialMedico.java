package albergue.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "historiales_medicos")
public class HistorialMedico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tipo_tratamiento", nullable = false)
    private String tipoTratamiento;

    @Column(name = "fecha_aplicacion", nullable = false)
    private LocalDate fechaAplicacion;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @ManyToOne
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    public HistorialMedico() {}

    public HistorialMedico(String tipoTratamiento, LocalDate fechaAplicacion, String observaciones, Mascota mascota) {
        this.tipoTratamiento = tipoTratamiento;
        this.fechaAplicacion = fechaAplicacion;
        this.observaciones = observaciones;
        this.mascota = mascota;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTipoTratamiento() { return tipoTratamiento; }
    public void setTipoTratamiento(String tipoTratamiento) { this.tipoTratamiento = tipoTratamiento; }
    public LocalDate getFechaAplicacion() { return fechaAplicacion; }
    public void setFechaAplicacion(LocalDate fechaAplicacion) { this.fechaAplicacion = fechaAplicacion; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public Mascota getMascota() { return mascota; }
    public void setMascota(Mascota mascota) { this.mascota = mascota; }
}