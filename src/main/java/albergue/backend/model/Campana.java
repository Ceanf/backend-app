package albergue.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "campanas")
public class Campana {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private Double meta;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private String tipoAyuda; // Alimentos, Medicamentos, Sede

    @Column(name = "monto_recaudado")
    private Double montoRecaudado = 0.0; // Inicia en 0 soles por defecto

    // Relación relacional: Muchas campañas pertenecen a un Albergue
    @ManyToOne
    @JoinColumn(name = "albergue_id", nullable = false)
    private Albergue albergue;

    public Campana() {
    }

    public Campana(String titulo, Double meta, String descripcion, String tipoAyuda, Albergue albergue) {
        this.titulo = titulo;
        this.meta = meta;
        this.descripcion = descripcion;
        this.tipoAyuda = tipoAyuda;
        this.albergue = albergue;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public Double getMeta() { return meta; }
    public void setMeta(Double meta) { this.meta = meta; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getTipoAyuda() { return tipoAyuda; }
    public void setTipoAyuda(String tipoAyuda) { this.tipoAyuda = tipoAyuda; }

    public Double getMontoRecaudado() { return montoRecaudado; }
    public void setMontoRecaudado(Double montoRecaudado) { this.montoRecaudado = montoRecaudado; }

    public Albergue getAlbergue() { return albergue; }
    public void setAlbergue(Albergue albergue) { this.albergue = albergue; }
}