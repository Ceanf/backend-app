package albergue.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes")
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "fecha_envio", nullable = false)
    private LocalDateTime fechaEnvio;

    // Usamos TEXT para poder guardar sin problemas las respuestas del cuestionario en formato JSON
    @Column(name = "respuestas_json", columnDefinition = "TEXT")
    private String respuestasJson;

    @Column(name = "estado_proceso", nullable = false)
    private String estadoProceso; // Ej: "Pendiente", "Entrevista", "Visita", "Aprobado"

    // ==========================================
    // RELACIÓN 1: Muchas Solicitudes -> Un Usuario
    // ==========================================
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // ==========================================
    // RELACIÓN 2: Muchas Solicitudes -> Una Mascota
    // ==========================================
    @ManyToOne
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    // Constructores
    public Solicitud() {
    }

    public Solicitud(LocalDateTime fechaEnvio, String respuestasJson, String estadoProceso, Usuario usuario, Mascota mascota) {
        this.fechaEnvio = fechaEnvio;
        this.respuestasJson = respuestasJson;
        this.estadoProceso = estadoProceso;
        this.usuario = usuario;
        this.mascota = mascota;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public String getRespuestasJson() {
        return respuestasJson;
    }

    public void setRespuestasJson(String respuestasJson) {
        this.respuestasJson = respuestasJson;
    }

    public String getEstadoProceso() {
        return estadoProceso;
    }

    public void setEstadoProceso(String estadoProceso) {
        this.estadoProceso = estadoProceso;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Mascota getMascota() {
        return mascota;
    }

    public void setMascota(Mascota mascota) {
        this.mascota = mascota;
    }
}