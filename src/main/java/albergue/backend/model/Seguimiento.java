package albergue.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "seguimientos")
public class Seguimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "fecha_visita", nullable = false)
    private LocalDate fechaVisita;

    @Column(name = "estado_mascota", nullable = false)
    private String estadoMascota;

    @Column(columnDefinition = "TEXT")
    private String comentarios;

    @Column(name = "foto_evidencia_url")
    private String fotoEvidenciaUrl;

    @ManyToOne
    @JoinColumn(name = "solicitud_id", nullable = false)
    private Solicitud solicitud;

    public Seguimiento() {}

    public Seguimiento(LocalDate fechaVisita, String estadoMascota, String comentarios, String fotoEvidenciaUrl, Solicitud solicitud) {
        this.fechaVisita = fechaVisita;
        this.estadoMascota = estadoMascota;
        this.comentarios = comentarios;
        this.fotoEvidenciaUrl = fotoEvidenciaUrl;
        this.solicitud = solicitud;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public LocalDate getFechaVisita() { return fechaVisita; }
    public void setFechaVisita(LocalDate fechaVisita) { this.fechaVisita = fechaVisita; }
    public String getEstadoMascota() { return estadoMascota; }
    public void setEstadoMascota(String estadoMascota) { this.estadoMascota = estadoMascota; }
    public String getComentarios() { return comentarios; }
    public void setComentarios(String comentarios) { this.comentarios = comentarios; }
    public String getFotoEvidenciaUrl() { return fotoEvidenciaUrl; }
    public void setFotoEvidenciaUrl(String fotoEvidenciaUrl) { this.fotoEvidenciaUrl = fotoEvidenciaUrl; }
    public Solicitud getSolicitud() { return solicitud; }
    public void setSolicitud(Solicitud solicitud) { this.solicitud = solicitud; }
}