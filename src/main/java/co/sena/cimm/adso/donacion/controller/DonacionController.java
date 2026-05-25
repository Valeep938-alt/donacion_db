package co.sena.cimm.adso.donacion.controller;

import co.sena.cimm.adso.donacion.dto.Request.DonacionRequest;
import co.sena.cimm.adso.donacion.dto.Response.DonacionResponse;
import co.sena.cimm.adso.donacion.dto.Response.DonanteResponse;
import co.sena.cimm.adso.donacion.service.DonacionService;
import co.sena.cimm.adso.donacion.service.DonanteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import co.sena.cimm.adso.donacion.service.ConsentimientoService;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.time.LocalDate;
import java.time.Period;

@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "Donaciones", description = "Gestión de donaciones de sangre")
public class DonacionController {

    private final DonacionService donacionService;
    private final DonanteService donanteService;
    private final ConsentimientoService consentimientoService;

    @GetMapping("/donaciones")
    public String listarVista(Model model) {
        log.info("Vista GET /donaciones");
        model.addAttribute("donaciones", donacionService.listarTodos());
        model.addAttribute("titulo", "Historial de donaciones");
        return "Donacion/lista";
    }

    @GetMapping("/donaciones/nueva")
    public String nuevaFormulario(Model model) {
        LocalDate hoy = LocalDate.now();
        List<DonanteResponse> aptos = donanteService.listarTodos().stream()
                .filter(d -> {
                    if (!consentimientoService.verificarFirmaValida(d.getId())) {
                        return false;
                    }
                    if (d.getFechaUltimaDonacion() != null) {
                        LocalDate proxPermitida = d.getFechaUltimaDonacion().plusMonths(3);
                        if (hoy.isBefore(proxPermitida)) {
                            return false;
                        }
                    }
                    return true;
                })
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("donacion", new DonacionRequest());
        model.addAttribute("donantes", aptos);
        model.addAttribute("titulo", "Registrar donación");
        model.addAttribute("accion", "/donaciones/guardar");
        return "Donacion/formulario";
    }

    @PostMapping("/donaciones/guardar")
    public String guardarVista(
            @Valid @ModelAttribute("donacion") DonacionRequest request,
            BindingResult result,
            @RequestParam(value = "archivoConsentimiento", required = false) MultipartFile archivoConsentimiento,
            @RequestParam(value = "aceptaConsentimiento", required = false) Boolean acepta,
            Model model,
            RedirectAttributes ra) {

        if (result.hasErrors()) {
            model.addAttribute("donantes", donanteService.listarTodos());
            model.addAttribute("titulo", "Registrar donación");
            model.addAttribute("accion", "/donaciones/guardar");
            return "Donacion/formulario";
        }
        try {
            // 1. Primero el consentimiento (si ya existe lo ignora, si no lo crea)
            consentimientoService.guardarDesdeDonacion(request.getDonanteId(), archivoConsentimiento);
            // 2. Luego la donación (ya encuentra consentimiento válido en consents)
            donacionService.guardar(request);
            ra.addFlashAttribute("mensajeExito", "Donación registrada correctamente.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/donaciones";
    }

    @GetMapping("/donaciones/donante/{donanteId}")
    public String historialPorDonante(@PathVariable Long donanteId, Model model) {
        log.info("Vista GET /donaciones/donante/{}", donanteId);
        DonanteResponse donante = donanteService.buscarPorId(donanteId);
        model.addAttribute("donaciones", donacionService.buscarPorDonante(donanteId));
        model.addAttribute("titulo", "Historial de " + donante.getNombres() + " " + donante.getApellidos());
        return "Donacion/lista";
    }

    @GetMapping("/donaciones/detalle/{id}")
    public String detalleVista(@PathVariable Long id, Model model) {
        model.addAttribute("donacion", donacionService.buscarPorId(id));
        return "Donacion/detalle";
    }

    @GetMapping("/donaciones/eliminar/{id}")
    public String eliminarVista(@PathVariable Long id, RedirectAttributes ra) {
        donacionService.eliminar(id);
        ra.addFlashAttribute("mensajeExito", "Donación eliminada.");
        return "redirect:/donaciones";
    }

    @GetMapping("/donaciones/editar/{id}")
    public String editarFormulario(@PathVariable Long id, Model model) {
        DonacionResponse donacion = donacionService.buscarPorId(id);
        DonacionRequest request = new DonacionRequest();
        request.setDonanteId(donacion.getDonanteId());
        request.setCantidadML(donacion.getCantidadML());
        request.setFechaDonacion(donacion.getFechaDonacion());
        request.setObservaciones(donacion.getObservaciones());

        model.addAttribute("donacion", request);
        model.addAttribute("donacionId", id);
        model.addAttribute("donantes", donanteService.listarTodos());
        model.addAttribute("titulo", "Editar donación");
        model.addAttribute("accion", "/donaciones/actualizar/" + id);
        log.info("Fecha donacion: {}", donacion.getFechaDonacion());
        log.info("CantidadML: {}", donacion.getCantidadML());
        log.info("DonanteId: {}", donacion.getDonanteId());
        return "Donacion/formulario";
    }

    @PostMapping("/donaciones/actualizar/{id}")
    public String actualizarVista(
            @PathVariable Long id,
            @ModelAttribute("donacion") DonacionRequest request,
            BindingResult result,
            @RequestParam(value = "archivoConsentimiento", required = false) MultipartFile archivoConsentimiento,
            Model model,
            RedirectAttributes ra) {

        // Validación manual de campos obligatorios en edición
        boolean hayError = false;

        if (request.getCantidadML() == null || request.getCantidadML() <= 0) {
            model.addAttribute("mensajeError", "La cantidad en ml es obligatoria y debe ser positiva.");
            hayError = true;
        }

        if (request.getFechaDonacion() != null && request.getFechaDonacion().isAfter(LocalDate.now())) {
            model.addAttribute("mensajeError", "La fecha de donación no puede ser futura.");
            hayError = true;
        }

        if (hayError) {
            model.addAttribute("donantes", donanteService.listarTodos());
            model.addAttribute("donacionId", id);
            model.addAttribute("titulo", "Editar donación");
            model.addAttribute("accion", "/donaciones/actualizar/" + id);
            return "Donacion/formulario";
        }

        try {
            donacionService.actualizar(id, request);

            // Obtener el donanteId real desde la BD, no del request
            DonacionResponse donacionActualizada = donacionService.buscarPorId(id);
            Long donanteId = donacionActualizada.getDonanteId();

            // Guardar archivo si se subió uno nuevo
            if (archivoConsentimiento != null && !archivoConsentimiento.isEmpty()) {
                consentimientoService.actualizarArchivoConsentimiento(donanteId, archivoConsentimiento);
            }

            ra.addFlashAttribute("mensajeExito", "Donación actualizada correctamente.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/donaciones";
    }

    // ══════════════════════════════════════════════════════════════════════
    // API REST — /api/donaciones
    // ══════════════════════════════════════════════════════════════════════
    @Operation(summary = "Listar todas las donaciones")
    @GetMapping("/api/donaciones")
    @ResponseBody
    public ResponseEntity<List<DonacionResponse>> listarTodas() {
        log.info("API GET /api/donaciones");
        return ResponseEntity.ok(donacionService.listarTodos());
    }

    @Operation(summary = "Buscar donación por ID")
    @GetMapping("/api/donaciones/{id}")
    @ResponseBody
    public ResponseEntity<DonacionResponse> buscarPorId(@PathVariable Long id) {
        log.info("API GET /api/donaciones/{}", id);
        return ResponseEntity.ok(donacionService.buscarPorId(id));
    }

    @Operation(summary = "Registrar nueva donación")
    @PostMapping("/api/donaciones")
    @ResponseBody
    public ResponseEntity<DonacionResponse> registrar(@Valid @RequestBody DonacionRequest request) {
        log.info("API POST /api/donaciones - donanteId: {}", request.getDonanteId());
        return ResponseEntity.status(HttpStatus.CREATED).body(donacionService.guardar(request));
    }

    @Operation(summary = "Actualizar donación por ID")
    @PutMapping("/api/donaciones/{id}")
    @ResponseBody
    public ResponseEntity<DonacionResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody DonacionRequest request) {
        log.info("API PUT /api/donaciones/{}", id);
        return ResponseEntity.ok(donacionService.actualizar(id, request));
    }

    @Operation(summary = "Eliminar donación por ID")
    @DeleteMapping("/api/donaciones/{id}")
    @ResponseBody
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("API DELETE /api/donaciones/{}", id);
        donacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Historial de donaciones por donante")
    @GetMapping("/api/donaciones/donante/{donanteId}")
    @ResponseBody
    public ResponseEntity<List<DonacionResponse>> historialApi(@PathVariable Long donanteId) {
        log.info("API GET /api/donaciones/donante/{}", donanteId);
        return ResponseEntity.ok(donacionService.buscarPorDonante(donanteId));
    }
}
