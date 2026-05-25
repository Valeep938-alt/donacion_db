package co.sena.cimm.adso.donacion.service;

import co.sena.cimm.adso.donacion.dto.Request.ConsentimientoRequest;
import co.sena.cimm.adso.donacion.dto.Response.ConsentimientoResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ConsentimientoService {

    List<ConsentimientoResponse> listarTodos();
    ConsentimientoResponse guardar(ConsentimientoRequest request);
    ConsentimientoResponse buscarPorId(Long id);
    ConsentimientoResponse buscarPorDonanteId(Long donanteId);
    ConsentimientoResponse actualizar(Long id, ConsentimientoRequest request);
    void eliminar(Long id);
    boolean verificarFirmaValida(Long donanteId);
    ConsentimientoResponse guardarDesdeDonacion(Long donanteId, MultipartFile archivo);
    ConsentimientoResponse guardarConArchivo(ConsentimientoRequest request, MultipartFile archivo);
    ConsentimientoResponse actualizarConArchivo(Long id, ConsentimientoRequest request, MultipartFile archivo);
    ConsentimientoResponse actualizarArchivoConsentimiento(Long donanteId, MultipartFile archivo);
    void cambiarEstado(Long id);
}