package co.sena.cimm.adso.donacion.service;

import co.sena.cimm.adso.donacion.dto.Request.DonanteRequest;
import co.sena.cimm.adso.donacion.dto.Response.DonanteResponse;

import java.util.List;

public interface DonanteService {

    List<DonanteResponse> listarTodos();
    DonanteResponse buscarPorId(Long id);
    DonanteResponse guardar(DonanteRequest request);
    DonanteResponse actualizar(Long id, DonanteRequest request);
    void eliminar(Long id);
}