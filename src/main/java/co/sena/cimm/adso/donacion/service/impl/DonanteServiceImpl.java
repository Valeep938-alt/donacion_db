package co.sena.cimm.adso.donacion.service.impl;

import co.sena.cimm.adso.donacion.Mapper.DonanteMapper;
import co.sena.cimm.adso.donacion.domain.Donante;
import co.sena.cimm.adso.donacion.dto.Request.DonanteRequest;
import co.sena.cimm.adso.donacion.dto.Response.DonanteResponse;
import co.sena.cimm.adso.donacion.exception.BusinessException;
import co.sena.cimm.adso.donacion.exception.ResourceNotFoundException;
import co.sena.cimm.adso.donacion.repository.DonanteRepository;
import co.sena.cimm.adso.donacion.service.DonanteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DonanteServiceImpl implements DonanteService {

    private final DonanteRepository donanteRepository;
    private final DonanteMapper donanteMapper;

    @Override
    @Transactional(readOnly = true)
    public List<DonanteResponse> listarTodos() {
        log.info("Listando todos los donantes");
        return donanteRepository.findAll()
                .stream()
                .map(donanteMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DonanteResponse buscarPorId(Long id) {
        log.info("Buscando donante con id: {}", id);
        return donanteMapper.toResponse(obtenerDonanteOFallar(id));
    }

    @Override
    @Transactional
    public DonanteResponse guardar(DonanteRequest request) {
        log.info("Registrando nuevo donante con documento: {}", request.getDocumento());

        validarDocumentoUnico(request.getDocumento());
        validarEdadMinima(request);
        validarPesoMinimo(request);

        Donante donante = donanteMapper.toEntity(request);
        donante.setFirmaConsentimiento(request.getFirmaConsentimiento());
        donante.setAceptaConsentimiento(request.getAceptaConsentimiento());

        Donante guardado = donanteRepository.save(donante);
        log.info("Donante registrado con id: {}", guardado.getId());
        return donanteMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public DonanteResponse actualizar(Long id, DonanteRequest request) {
        log.info("Actualizando donante con id: {}", id);
        Donante donante = obtenerDonanteOFallar(id);

        if (!donante.getDocumento().equals(request.getDocumento())) {
            validarDocumentoUnico(request.getDocumento());
        }

        validarEdadMinima(request);
        validarPesoMinimo(request);

        donanteMapper.updateEntity(request, donante);
        donante.setAceptaConsentimiento(request.getAceptaConsentimiento());
        donante.setFirmaConsentimiento(request.getFirmaConsentimiento());

        Donante actualizado = donanteRepository.save(donante);
        log.info("Donante actualizado con id: {}", actualizado.getId());
        return donanteMapper.toResponse(actualizado);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando donante con id: {}", id);
        Donante donante = obtenerDonanteOFallar(id);
        donanteRepository.delete(donante);
        log.info("Donante con id: {} eliminado", id);
    }

    // ── Helpers privados ─────────────────────────────────────────────────

    private Donante obtenerDonanteOFallar(Long id) {
        return donanteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donante no encontrado con id: " + id));
    }

    private void validarDocumentoUnico(String documento) {
        if (donanteRepository.existsByDocumento(documento)) {
            throw new BusinessException("Ya existe un donante registrado con el documento: " + documento);
        }
    }

    private void validarEdadMinima(DonanteRequest request) {
        if (request.getFechaNacimiento() == null) return;
        int edad = Period.between(request.getFechaNacimiento(), LocalDate.now()).getYears();
        if (edad < 18) {
            throw new BusinessException(
                "El donante es menor de edad. Debe tener al menos 18 años. Edad actual: " + edad + " años.");
        }
    }

    private void validarPesoMinimo(DonanteRequest request) {
        if (request.getPeso() == null) return;
        if (request.getPeso() < 50.0) {
            throw new BusinessException(
                "El donante no cumple el peso mínimo requerido. Debe pesar al menos 50 kg. Peso registrado: "
                + request.getPeso() + " kg.");
        }
    }
}