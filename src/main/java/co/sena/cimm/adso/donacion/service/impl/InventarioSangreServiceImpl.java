package co.sena.cimm.adso.donacion.service.impl;

import co.sena.cimm.adso.donacion.domain.InventarioSangre;
import co.sena.cimm.adso.donacion.dto.Request.InventarioRequest;
import co.sena.cimm.adso.donacion.dto.Response.InventarioResponse;
import co.sena.cimm.adso.donacion.enums.TipoSangre;
import co.sena.cimm.adso.donacion.exception.BusinessException;
import co.sena.cimm.adso.donacion.exception.ResourceNotFoundException;
import co.sena.cimm.adso.donacion.repository.InventarioSangreRepository;
import co.sena.cimm.adso.donacion.service.InventarioSangreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventarioSangreServiceImpl implements InventarioSangreService {

    private final InventarioSangreRepository inventarioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<InventarioResponse> listarTodos() {
        inicializarInventarioSiEstaVacio();
        return inventarioRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InventarioResponse buscarPorTipo(TipoSangre tipoSangre) {
        InventarioSangre inventario = inventarioRepository.findByTipoSangre(tipoSangre)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventario no encontrado para tipo de sangre: " + tipoSangre));
        return mapToResponse(inventario);
    }

    @Override
    @Transactional
    public InventarioResponse registrarMovimiento(InventarioRequest request) {
        log.info("Registrando movimiento de inventario para tipo: {}", request.getTipoSangre());
        InventarioSangre inventario = obtenerOInicializar(request.getTipoSangre());

        int cantidadAnterior = inventario.getCantidadML();
        int cantidadNueva = cantidadAnterior + request.getCantidadML().intValue();

        inventario.setCantidadML(cantidadNueva);
        InventarioSangre guardado = inventarioRepository.save(inventario);

        log.info("Inventario actualizado. Anterior: {}ml, Nuevo: {}ml", cantidadAnterior, cantidadNueva);
        return mapToResponse(guardado);
    }

    @Override
    @Transactional
    public InventarioResponse actualizar(Long id, InventarioRequest request) {
        log.info("Actualizando inventario con ID: {}", id);
        InventarioSangre inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado con ID: " + id));

        inventario.setTipoSangre(request.getTipoSangre());
        inventario.setCantidadML(request.getCantidadML().intValue());

        InventarioSangre actualizado = inventarioRepository.save(inventario);
        return mapToResponse(actualizado);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando registro de inventario con ID: {}", id);
        inventarioRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void descontarStock(TipoSangre tipoSangre, Double cantidadML) {
        InventarioSangre inventario = obtenerOInicializar(tipoSangre);
        int cantidadRestante = inventario.getCantidadML() - cantidadML.intValue();

        if (cantidadRestante < 0) {
            throw new BusinessException("Stock insuficiente para el tipo de sangre " + tipoSangre);
        }

        inventario.setCantidadML(cantidadRestante);
        inventarioRepository.save(inventario);
        log.info("Stock descontado para {}: {}ml restantes", tipoSangre, cantidadRestante);
    }

    private InventarioSangre obtenerOInicializar(TipoSangre tipo) {
        return inventarioRepository.findByTipoSangre(tipo)
                .orElseGet(() -> {
                    InventarioSangre nuevo = new InventarioSangre();
                    nuevo.setTipoSangre(tipo);
                    nuevo.setCantidadML(0);
                    return inventarioRepository.save(nuevo);
                });
    }

    private void inicializarInventarioSiEstaVacio() {
        if (inventarioRepository.count() == 0) {
            log.info("Inicializando inventario de sangre...");
            Stream.of(TipoSangre.values()).forEach(tipo -> {
                if (inventarioRepository.findByTipoSangre(tipo).isEmpty()) {
                    InventarioSangre nuevo = new InventarioSangre();
                    nuevo.setTipoSangre(tipo);
                    nuevo.setCantidadML(0);
                    inventarioRepository.save(nuevo);
                }
            });
        }
    }

    private InventarioResponse mapToResponse(InventarioSangre i) {
        return InventarioResponse.builder()
                .tipoSangre(i.getTipoSangre())
                .volumenDisponibleML(i.getCantidadML().doubleValue())
                .fechaActualizacion(i.getUltimaActualizacion() != null
                        ? i.getUltimaActualizacion().toLocalDate()
                        : LocalDate.now())
                .build();
    }
}