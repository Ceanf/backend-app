package albergue.backend.model;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "albergues")
public class Albergue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre_refugio", nullable = false)
    private String nombreRefugio;

    @Column(nullable = false)
    private String ubicacion;

    @Column(nullable = false)
    private String contacto;

    @Column(name = "metas_donacion")
    private Double metasDonacion;

    // ==========================================
    // RELACIÓN: Un Albergue -> Muchas Mascotas
    // ==========================================
    // "mappedBy" indica que la relación ya fue configurada en la clase Mascota bajo el campo "albergue"
    @OneToMany(mappedBy = "albergue", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // 2. Agrega esta anotación para romper el bucle infinito
    private List<Mascota> mascotas;

    // Constructores
    public Albergue() {
    }

    public Albergue(String nombreRefugio, String ubicacion, String contacto, Double metasDonacion) {
        this.nombreRefugio = nombreRefugio;
        this.ubicacion = ubicacion;
        this.contacto = contacto;
        this.metasDonacion = metasDonacion;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombreRefugio() {
        return nombreRefugio;
    }

    public void setNombreRefugio(String nombreRefugio) {
        this.nombreRefugio = nombreRefugio;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public Double getMetasDonacion() {
        return metasDonacion;
    }

    public void setMetasDonacion(Double metasDonacion) {
        this.metasDonacion = metasDonacion;
    }

    public List<Mascota> getMascotas() {
        return mascotas;
    }

    public void setMascotas(List<Mascota> mascotas) {
        this.mascotas = mascotas;
    }
}