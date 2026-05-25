package co.sena.cimm.adso.donacion.service;

import co.sena.cimm.adso.donacion.dto.Request.DonacionRequest;
import co.sena.cimm.adso.donacion.dto.Response.DonacionResponse;

import java.util.List;

public interface DonacionService {

    List<DonacionResponse> listarTodos();
    DonacionResponse buscarPorId(Long id);
    DonacionResponse guardar(DonacionRequest request);
    DonacionResponse actualizar(Long id, DonacionRequest request);
    void eliminar(Long id);
    List<DonacionResponse> buscarPorDonante(Long donanteId);
}