package co.sena.cimm.adso.donacion.service.impl;

import co.sena.cimm.adso.donacion.domain.Consentimiento;
import co.sena.cimm.adso.donacion.domain.Donante;
import co.sena.cimm.adso.donacion.dto.Request.ConsentimientoRequest;
import co.sena.cimm.adso.donacion.dto.Response.ConsentimientoResponse;
import co.sena.cimm.adso.donacion.exception.BusinessException;
import co.sena.cimm.adso.donacion.exception.ResourceNotFoundException;
import co.sena.cimm.adso.donacion.repository.ConsentimientoRepository;
import co.sena.cimm.adso.donacion.repository.DonanteRepository;
import co.sena.cimm.adso.donacion.service.ConsentimientoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsentimientoServiceImpl implements ConsentimientoService {

    private final ConsentimientoRepository consentimientoRepository;
    private final DonanteRepository donanteRepository;

    @Override
    @Transactional
    public ConsentimientoResponse guardar(ConsentimientoRequest request) {
        log.info("Registrando consentimiento para donante ID: {}", request.getDonanteId());
        Donante donante = obtenerDonanteOFallar(request.getDonanteId());

        if (consentimientoRepository.existsByDonanteId(donante.getId())) {
            throw new BusinessException("El donante ya tiene un consentimiento registrado.");
        }

        Consentimiento consentimiento = new Consentimiento();
        consentimiento.setDonante(donante);
        consentimiento.setFirmaConsentimiento(request.getFirmaConsentimiento());
        consentimiento.setTipoFirma("BASE64");
        consentimiento.setFechaFirma(LocalDateTime.now());
        consentimiento.setActivo(request.getAceptaConsentimiento() != null
                ? request.getAceptaConsentimiento() : true);

        Consentimiento guardado = consentimientoRepository.save(consentimiento);
        log.info("Consentimiento guardado con ID: {}", guardado.getId());
        return mapToResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public ConsentimientoResponse buscarPorId(Long id) {
        log.info("Buscando consentimiento con ID: {}", id);
        return mapToResponse(obtenerConsentimientoOFallar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ConsentimientoResponse buscarPorDonanteId(Long donanteId) {
        log.info("Buscando consentimiento por donante ID: {}", donanteId);
        return consentimientoRepository.findByDonanteId(donanteId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                "No se encontró consentimiento para el donante ID: " + donanteId));
    }

    @Override
    @Transactional
    public ConsentimientoResponse actualizar(Long id, ConsentimientoRequest request) {
        log.info("Actualizando consentimiento con ID: {}", id);
        Consentimiento consentimiento = obtenerConsentimientoOFallar(id);

        consentimiento.setFirmaConsentimiento(request.getFirmaConsentimiento());
        consentimiento.setActivo(request.getAceptaConsentimiento() != null
                ? request.getAceptaConsentimiento() : true);
        consentimiento.setFechaFirma(LocalDateTime.now());

        Consentimiento actualizado = consentimientoRepository.save(consentimiento);
        return mapToResponse(actualizado);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando consentimiento con ID: {}", id);
        consentimientoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verificarFirmaValida(Long donanteId) {
        return consentimientoRepository.findByDonanteId(donanteId)
                .map(c -> Boolean.TRUE.equals(c.getActivo())
                && StringUtils.hasText(c.getFirmaConsentimiento()))
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConsentimientoResponse> listarTodos() {
        log.info("Listando todos los consentimientos");
        return consentimientoRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public ConsentimientoResponse guardarDesdeDonacion(Long donanteId, MultipartFile archivo) {
        Donante donante = obtenerDonanteOFallar(donanteId);

        if (consentimientoRepository.existsByDonanteId(donanteId)) {
            return buscarPorDonanteId(donanteId);
        }

        String rutaArchivo = procesarArchivo(donanteId, archivo);

        Consentimiento consentimiento = new Consentimiento();
        consentimiento.setDonante(donante);
        consentimiento.setFirmaConsentimiento(rutaArchivo != null
                ? rutaArchivo
                : donante.getNombres() + " " + donante.getApellidos());
        consentimiento.setTipoFirma(rutaArchivo != null
                ? (archivo.getContentType() != null && archivo.getContentType().startsWith("image/") ? "URL" : "PDF")
                : "BASE64");
        consentimiento.setFechaFirma(LocalDateTime.now());
        consentimiento.setActivo(true);
        consentimiento.setArchivoConsentimiento(rutaArchivo);

        return mapToResponse(consentimientoRepository.save(consentimiento));
    }

    @Override
    @Transactional
    public ConsentimientoResponse guardarConArchivo(ConsentimientoRequest request, MultipartFile archivo) {
        Donante donante = obtenerDonanteOFallar(request.getDonanteId());

        if (consentimientoRepository.existsByDonanteId(donante.getId())) {
            throw new BusinessException("El donante ya tiene un consentimiento registrado.");
        }

        String rutaArchivo = procesarArchivo(donante.getId(), archivo);

        Consentimiento consentimiento = new Consentimiento();
        consentimiento.setDonante(donante);
        consentimiento.setFechaFirma(LocalDateTime.now());
        consentimiento.setActivo(request.getAceptaConsentimiento() != null
                ? request.getAceptaConsentimiento() : true);

        if (rutaArchivo != null) {
            consentimiento.setArchivoConsentimiento(rutaArchivo);
            consentimiento.setFirmaConsentimiento(rutaArchivo);
            consentimiento.setTipoFirma(archivo.getContentType() != null
                    && archivo.getContentType().startsWith("image/") ? "URL" : "PDF");
        } else {
            consentimiento.setFirmaConsentimiento(request.getFirmaConsentimiento());
            consentimiento.setTipoFirma("BASE64");
        }

        return mapToResponse(consentimientoRepository.save(consentimiento));
    }

    @Override
    @Transactional
    public ConsentimientoResponse actualizarArchivoConsentimiento(Long donanteId, MultipartFile archivo) {
        Consentimiento consentimiento = consentimientoRepository.findByDonanteId(donanteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                "No se encontró consentimiento para el donante ID: " + donanteId));

        String rutaArchivo = procesarArchivo(donanteId, archivo);

        if (rutaArchivo != null) {
            consentimiento.setArchivoConsentimiento(rutaArchivo);
            consentimiento.setFirmaConsentimiento(rutaArchivo);
            consentimiento.setTipoFirma(archivo.getContentType() != null
                    && archivo.getContentType().startsWith("image/") ? "URL" : "PDF");
            consentimiento.setFechaFirma(LocalDateTime.now());
            consentimiento.setActivo(true);
        }

        return mapToResponse(consentimientoRepository.save(consentimiento));
    }

    @Override
    @Transactional
    public ConsentimientoResponse actualizarConArchivo(Long id, ConsentimientoRequest request, MultipartFile archivo) {
        Consentimiento consentimiento = obtenerConsentimientoOFallar(id);

        String rutaArchivo = procesarArchivo(consentimiento.getDonante().getId(), archivo);

        consentimiento.setActivo(request.getAceptaConsentimiento() != null ? request.getAceptaConsentimiento() : true);
        consentimiento.setFechaFirma(LocalDateTime.now());

        if (rutaArchivo != null) {
            consentimiento.setArchivoConsentimiento(rutaArchivo);
            consentimiento.setFirmaConsentimiento(rutaArchivo);
            String contentType = archivo.getContentType();
            consentimiento.setTipoFirma(contentType != null && contentType.startsWith("image/") ? "URL" : "PDF");
        } else if (StringUtils.hasText(request.getFirmaConsentimiento())) {
            // Solo actualiza la firma de texto si escribieron algo
            consentimiento.setFirmaConsentimiento(request.getFirmaConsentimiento());
            consentimiento.setTipoFirma("BASE64");
        }
        // Si ambos vacíos, conserva la firma anterior

        return mapToResponse(consentimientoRepository.save(consentimiento));
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id) {
        Consentimiento consentimiento = obtenerConsentimientoOFallar(id);
        consentimiento.setActivo(!consentimiento.getActivo());
        consentimientoRepository.save(consentimiento);
    }

    private String procesarArchivo(Long donanteId, MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            return null;
        }
        try {
            // Ruta relativa que coincide con WebMvcConfig: uploads/consentimientos/
            String carpetaRelativa = "uploads/consentimientos/";
            String carpetaAbsoluta = Paths.get(System.getProperty("user.dir"), "uploads", "consentimientos")
                    .toAbsolutePath().normalize().toString();
            Files.createDirectories(Paths.get(carpetaAbsoluta));
            String nombreArchivo = "consent_" + donanteId + "_"
                    + System.currentTimeMillis() + "_" + archivo.getOriginalFilename();
            Path destino = Paths.get(carpetaAbsoluta, nombreArchivo);
            Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            // Retorna la ruta relativa para guardar en BD y construir URLs
            return carpetaRelativa + nombreArchivo;
        } catch (Exception e) {
            log.warn("No se pudo guardar el archivo: {}", e.getMessage());
            return null;
        }
    }

    private Consentimiento obtenerConsentimientoOFallar(Long id) {
        return consentimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                "Consentimiento no encontrado con ID: " + id));
    }

    private Donante obtenerDonanteOFallar(Long id) {
        return donanteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                "Donante no encontrado con ID: " + id));
    }

    private ConsentimientoResponse mapToResponse(Consentimiento c) {
        return ConsentimientoResponse.builder()
                .id(c.getId())
                .donanteId(c.getDonante().getId())
                .donanteNombres(c.getDonante().getNombres() + " " + c.getDonante().getApellidos())
                .donanteDocumento(c.getDonante().getDocumento())
                .aceptaConsentimiento(c.getActivo())
                .firmaConsentimiento(c.getFirmaConsentimiento())
                .fechaFirma(c.getFechaFirma())
                .archivoConsentimiento(c.getArchivoConsentimiento()) // ← este campo
                .build();
    }
}
