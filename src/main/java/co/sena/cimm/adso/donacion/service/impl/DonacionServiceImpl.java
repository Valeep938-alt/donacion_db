package co.sena.cimm.adso.donacion.service.impl;

import co.sena.cimm.adso.donacion.domain.Donacion;
import co.sena.cimm.adso.donacion.domain.Donante;
import co.sena.cimm.adso.donacion.domain.InventarioSangre;
import co.sena.cimm.adso.donacion.dto.Request.DonacionRequest;
import co.sena.cimm.adso.donacion.dto.Response.DonacionResponse;
import co.sena.cimm.adso.donacion.enums.TipoSangre;
import co.sena.cimm.adso.donacion.exception.DonanteNoAptoException;
import co.sena.cimm.adso.donacion.exception.ResourceNotFoundException;
import co.sena.cimm.adso.donacion.repository.ConsentimientoRepository;
import co.sena.cimm.adso.donacion.repository.DonacionRepository;
import co.sena.cimm.adso.donacion.repository.DonanteRepository;
import co.sena.cimm.adso.donacion.repository.InventarioSangreRepository;
import co.sena.cimm.adso.donacion.service.DonacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class DonacionServiceImpl implements DonacionService {

    private final DonacionRepository donacionRepository;
    private final DonanteRepository donanteRepository;
    private final InventarioSangreRepository inventarioRepository;
    private final ConsentimientoRepository consentimientoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DonacionResponse> listarTodos() {
        return donacionRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DonacionResponse buscarPorId(Long id) {
        return mapToResponse(obtenerDonacionOFallar(id));
    }

    @Override
    @Transactional
    public DonacionResponse guardar(DonacionRequest request) {
        log.info("Registrando nueva donación para donante ID: {}", request.getDonanteId());

        Donante donante = obtenerDonanteOFallar(request.getDonanteId());
        validarAptitudDonante(donante);

        String codigoDonacion = generarCodigoUnico();

        Donacion donacion = new Donacion();
        donacion.setCodigoDonacion(codigoDonacion);
        donacion.setDonante(donante);
        donacion.setCantidadML(request.getCantidadML().intValue());
        donacion.setFechaDonacion(request.getFechaDonacion() != null ? request.getFechaDonacion() : LocalDate.now());
        donacion.setObservaciones(request.getObservaciones());
        donacion.setTipoSangre(donante.getTipoSangre().getDescripcion());

        Donacion guardada = donacionRepository.save(donacion);

        donante.setFechaUltimaDonacion(guardada.getFechaDonacion());
        donanteRepository.save(donante);

        actualizarInventario(donante.getTipoSangre(), guardada.getCantidadML());

        log.info("Donación registrada exitosamente con código: {}", codigoDonacion);
        return mapToResponse(guardada);
    }

    @Override
    @Transactional
    public DonacionResponse actualizar(Long id, DonacionRequest request) {
        log.info("Actualizando donación con ID: {}", id);
        Donacion donacion = obtenerDonacionOFallar(id);
        donacion.setCantidadML(request.getCantidadML().intValue());
        donacion.setFechaDonacion(request.getFechaDonacion() != null ? request.getFechaDonacion() : donacion.getFechaDonacion());
        donacion.setObservaciones(request.getObservaciones());
        return mapToResponse(donacionRepository.save(donacion));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando donación con ID: {}", id);
        donacionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonacionResponse> buscarPorDonante(Long donanteId) {
        return donacionRepository.findByDonanteIdOrderByFechaDonacionDesc(donanteId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ── Helpers privados ────────────────────────────────────────────────────
    private Donacion obtenerDonacionOFallar(Long id) {
        return donacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donación no encontrada con ID: " + id));
    }

    private Donante obtenerDonanteOFallar(Long id) {
        return donanteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donante no encontrado con ID: " + id));
    }

    private void validarAptitudDonante(Donante donante) {
        int edad = Period.between(donante.getFechaNacimiento(), LocalDate.now()).getYears();
        if (edad < 18) {
            throw new DonanteNoAptoException("El donante es menor de edad. Debe tener al menos 18 años. Edad actual: " + edad + " años.");
        }

        if (donante.getPeso() < 50.0) {
            throw new DonanteNoAptoException("El donante no cumple el peso mínimo. Debe pesar al menos 50 kg. Peso actual: " + donante.getPeso() + " kg.");
        }

        if (donante.getFechaUltimaDonacion() != null) {
            LocalDate proxFechaPermitida = donante.getFechaUltimaDonacion().plusMonths(3);
            if (LocalDate.now().isBefore(proxFechaPermitida)) {
                throw new DonanteNoAptoException(
                        "Deben pasar al menos 3 meses entre donaciones. "
                        + "Última donación: " + donante.getFechaUltimaDonacion()
                        + ". Próxima fecha permitida: " + proxFechaPermitida + ".");
            }
        }

        boolean tieneConsentimiento = consentimientoRepository
                .findByDonanteId(donante.getId())
                .map(c -> Boolean.TRUE.equals(c.getActivo())
                && StringUtils.hasText(c.getFirmaConsentimiento()))
                .orElse(false);

        if (!tieneConsentimiento) {
            throw new DonanteNoAptoException("El donante no tiene un consentimiento informado firmado válido.");
        }
    }

    private String generarCodigoUnico() {
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = String.format("%04d", new Random().nextInt(10000));
        return "DON-" + fecha + "-" + random;
    }

    private void actualizarInventario(TipoSangre tipo, int cantidad) {
        InventarioSangre inventario = inventarioRepository.findByTipoSangre(tipo)
                .orElseGet(() -> {
                    InventarioSangre nuevo = new InventarioSangre();
                    nuevo.setTipoSangre(tipo);
                    nuevo.setCantidadML(0);
                    return nuevo;
                });
        inventario.setCantidadML(inventario.getCantidadML() + cantidad);
        inventarioRepository.save(inventario);
    }

    private DonacionResponse mapToResponse(Donacion d) {
        return DonacionResponse.builder()
                .id(d.getId())
                .codigoDonacion(d.getCodigoDonacion())
                .donanteId(d.getDonante().getId())
                .donanteNombres(d.getDonante().getNombres())
                .donanteApellidos(d.getDonante().getApellidos())
                .donanteDocumento(d.getDonante().getDocumento())
                .cantidadML((double) d.getCantidadML())
                .fechaDonacion(d.getFechaDonacion())
                .observaciones(d.getObservaciones())
                .archivoConsentimiento(
                        consentimientoRepository.findByDonanteId(d.getDonante().getId())
                                .map(c -> c.getArchivoConsentimiento())
                                .orElse(null)
                )
                .build();
    }
}
