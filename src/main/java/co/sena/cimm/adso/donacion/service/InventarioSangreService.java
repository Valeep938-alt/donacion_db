package co.sena.cimm.adso.donacion.service;

import co.sena.cimm.adso.donacion.dto.Request.InventarioRequest;
import co.sena.cimm.adso.donacion.dto.Response.InventarioResponse;
import co.sena.cimm.adso.donacion.enums.TipoSangre;

import java.util.List;

public interface InventarioSangreService {

    List<InventarioResponse> listarTodos();
    InventarioResponse buscarPorTipo(TipoSangre tipoSangre);
    InventarioResponse registrarMovimiento(InventarioRequest request);
    InventarioResponse actualizar(Long id, InventarioRequest request);
    void eliminar(Long id);
    void descontarStock(TipoSangre tipoSangre, Double cantidadML);
}