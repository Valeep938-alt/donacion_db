package co.sena.cimm.adso.donacion.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "consents")
public class Consentimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "donante_id", nullable = false)
    private Donante donante;

    @Column(name = "fecha_firma", nullable = false)
    private LocalDateTime fechaFirma;

    @Column(nullable = true)
    private String archivoConsentimiento;

    @Column(name = "firma_consentimiento", nullable = true)
    private String firmaConsentimiento;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "tipo_firma", nullable = false)
    private String tipoFirma = "PDF"; // o BASE64, URL según el caso

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Donante getDonante() {
        return donante;
    }

    public void setDonante(Donante donante) {
        this.donante = donante;
    }

    public LocalDateTime getFechaFirma() {
        return fechaFirma;
    }

    public void setFechaFirma(LocalDateTime fechaFirma) {
        this.fechaFirma = fechaFirma;
    }

    public String getArchivoConsentimiento() {
        return archivoConsentimiento;
    }

    public void setArchivoConsentimiento(String archivoConsentimiento) {
        this.archivoConsentimiento = archivoConsentimiento;
    }

    public String getFirmaConsentimiento() {
        return firmaConsentimiento;
    }

    public void setFirmaConsentimiento(String f) {
        this.firmaConsentimiento = f;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getTipoFirma() {
        return tipoFirma;
    }

    public void setTipoFirma(String tipoFirma) {
        this.tipoFirma = tipoFirma;
    }
}
