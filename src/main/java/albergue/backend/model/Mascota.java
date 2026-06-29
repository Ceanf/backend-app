package albergue.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "mascotas")
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    private String raza;
    
    // 👇 🚀 NUEVO CAMPO: Guardará de forma estricta "PERROS", "GATOS" u "OTROS"
    private String especie; 
    
    private String edad; // Ej: "Cachorro", "Joven", "Adulto"
    
    private String nivelEnergia;
    
    private String tamano; // Ej: "Pequeño", "Mediano", "Grande"
    
    private String estado; // Ej: "Disponible", "En Proceso", "Adoptado"

    @Column(columnDefinition = "TEXT")
    private String historia;

    @Column(name = "foto_url")
    private String fotoUrl;

    // ==========================================
    // RELACIÓN: Muchas mascotas -> Un Albergue
    // ==========================================
    @ManyToOne
    @JoinColumn(name = "albergue_id", nullable = false)
    private Albergue albergue;

    // Constructores
    public Mascota() {
    }

    // Constructor completo actualizado con el nuevo parámetro 'especie'
    public Mascota(String nombre, String raza, String especie, String edad, String nivelEnergia, String tamano, String estado, String historia, String fotoUrl, Albergue albergue) {
        this.nombre = nombre;
        this.raza = raza;
        this.especie = especie;
        this.edad = edad;
        this.nivelEnergia = nivelEnergia;
        this.tamano = tamano;
        this.estado = estado;
        this.historia = historia;
        this.fotoUrl = fotoUrl;
        this.albergue = albergue;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    // 👇 Getters y Setters para el nuevo campo 'especie'
    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getNivelEnergia() {
        return nivelEnergia;
    }

    public void setNivelEnergia(String nivelEnergia) {
        this.nivelEnergia = nivelEnergia;
    }

    public String getTamano() {
        return tamano;
    }

    public void setTamano(String tamano) {
        this.tamano = tamano;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getHistoria() {
        return historia;
    }

    public void setHistoria(String historia) {
        this.historia = historia;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public Albergue getAlbergue() {
        return albergue;
    }

    public void setAlbergue(Albergue albergue) {
        this.albergue = albergue;
    }
}