package co.sena.cimm.adso.donacion.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import co.sena.cimm.adso.donacion.config.converter.TipoSangreConverter;
import co.sena.cimm.adso.donacion.enums.TipoSangre;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "donors")
public class Donante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombres;

    @Column(nullable = false)
    private String apellidos;

    @Column(nullable = false, unique = true)
    private String documento;

    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    @Convert(converter = TipoSangreConverter.class)
    @Column(name = "tipo_sangre", nullable = false, length = 10)
    private TipoSangre tipoSangre;

    @Column(nullable = false)
    private Double peso;

    private String telefono;

    @Column(unique = true)
    private String correo;

    private String direccion;

    private LocalDate fechaUltimaDonacion;

    @Column(nullable = false)
    private Boolean aceptaConsentimiento = false;

    private String firmaConsentimiento;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public TipoSangre getTipoSangre() { return tipoSangre; }
    public void setTipoSangre(TipoSangre tipoSangre) { this.tipoSangre = tipoSangre; }
    public Double getPeso() { return peso; }
    public void setPeso(Double peso) { this.peso = peso; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public LocalDate getFechaUltimaDonacion() { return fechaUltimaDonacion; }
    public void setFechaUltimaDonacion(LocalDate fechaUltimaDonacion) { this.fechaUltimaDonacion = fechaUltimaDonacion; }
    public Boolean getAceptaConsentimiento() { return aceptaConsentimiento; }
    public void setAceptaConsentimiento(Boolean aceptaConsentimiento) { this.aceptaConsentimiento = aceptaConsentimiento; }
    public String getFirmaConsentimiento() { return firmaConsentimiento; }
    public void setFirmaConsentimiento(String firmaConsentimiento) { this.firmaConsentimiento = firmaConsentimiento; }
}