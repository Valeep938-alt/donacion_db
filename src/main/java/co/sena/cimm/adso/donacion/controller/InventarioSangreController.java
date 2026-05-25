package co.sena.cimm.adso.donacion.controller;

import co.sena.cimm.adso.donacion.domain.InventarioSangre;
import co.sena.cimm.adso.donacion.dto.Response.InventarioResponse;
import co.sena.cimm.adso.donacion.enums.TipoSangre;
import co.sena.cimm.adso.donacion.service.InventarioSangreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "Inventario", description = "Consulta de inventario de sangre disponible")
public class InventarioSangreController {

    private final InventarioSangreService inventarioSangreService;
    
    @Operation(summary = "Consultar todo el inventario de sangre")
    @GetMapping("/api/inventario")
    @ResponseBody
    public ResponseEntity<List<InventarioResponse>> listarTodo() {
        log.info("API GET /api/inventario");
        return ResponseEntity.ok(inventarioSangreService.listarTodos());
    }

    @Operation(summary = "Consultar inventario por tipo de sangre")
    @GetMapping("/api/inventario/{tipoSangre}")
    @ResponseBody
    public ResponseEntity<InventarioResponse> buscarPorTipo(@PathVariable TipoSangre tipoSangre) {
        log.info("API GET /api/inventario/{}", tipoSangre);
        return ResponseEntity.ok(inventarioSangreService.buscarPorTipo(tipoSangre));
    }

    @GetMapping("/inventario")
    public String listaVista(Model model) {
        List<InventarioResponse> inventario = inventarioSangreService.listarTodos();

        double totalVolumen = inventario.stream()
        .mapToDouble(i -> i.getVolumenDisponibleML() != null ? i.getVolumenDisponibleML() : 0.0)
        .sum();

        model.addAttribute("inventario", inventario);
        model.addAttribute("totalVolumen", totalVolumen);
        return "inventario";
    }
}
