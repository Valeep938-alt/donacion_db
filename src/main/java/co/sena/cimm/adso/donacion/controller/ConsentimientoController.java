package co.sena.cimm.adso.donacion.controller;

import co.sena.cimm.adso.donacion.dto.Request.ConsentimientoRequest;
import co.sena.cimm.adso.donacion.dto.Response.ConsentimientoResponse;
import co.sena.cimm.adso.donacion.service.ConsentimientoService;
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
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "Consentimientos", description = "Gestión de firmas de consentimiento informado")
public class ConsentimientoController {

    private final ConsentimientoService consentimientoService;
    private final DonanteService donanteService;

    @GetMapping("/consentimientos/nuevo")
    public String nuevoFormulario(Model model) {
        log.info("Vista GET /consentimientos/nuevo");
        model.addAttribute("consentimiento", new ConsentimientoRequest());
        model.addAttribute("donantes", donanteService.listarTodos());
        model.addAttribute("titulo", "Registrar consentimiento");
        return "Consentimiento/formulario";
    }

    @PostMapping("/consentimientos/guardar")
    public String guardarVista(
            @Valid @ModelAttribute("consentimiento") ConsentimientoRequest request,
            BindingResult result,
            @RequestParam(value = "archivoConsentimiento", required = false) MultipartFile archivoConsentimiento,
            Model model,
            RedirectAttributes ra) {

        if (result.hasErrors()) {
            model.addAttribute("donantes", donanteService.listarTodos());
            model.addAttribute("titulo", "Registrar consentimiento");
            return "Consentimiento/formulario";
        }
        try {
            consentimientoService.guardarConArchivo(request, archivoConsentimiento);
            ra.addFlashAttribute("mensajeExito", "Consentimiento registrado correctamente.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/consentimientos";
    }

    @GetMapping("/consentimientos/donante/{donanteId}")
    public String verPorDonante(@PathVariable Long donanteId, Model model) {
        log.info("Vista GET /consentimientos/donante/{}", donanteId);
        model.addAttribute("consentimiento", consentimientoService.buscarPorDonanteId(donanteId));
        model.addAttribute("donante", donanteService.buscarPorId(donanteId));
        return "Consentimiento/detalle";
    }

    @GetMapping("/consentimientos")
    public String listaVista(Model model) {
        log.info("Vista GET /consentimientos");
        model.addAttribute("consentimientos", consentimientoService.listarTodos());
        model.addAttribute("currentView", "consentimientos");
        return "Consentimiento/lista";
    }

    @GetMapping("/consentimientos/editar/{id}")
    public String editarFormulario(@PathVariable Long id, Model model) {
        ConsentimientoResponse resp = consentimientoService.buscarPorId(id);
        ConsentimientoRequest request = new ConsentimientoRequest();
        request.setDonanteId(resp.getDonanteId());
        request.setAceptaConsentimiento(resp.getAceptaConsentimiento());

        // Determinar qué mostrar como firma actual
        String archivoActual = resp.getArchivoConsentimiento();
        String firmaActual = resp.getFirmaConsentimiento();

        // Si no hay archivoConsentimiento pero firmaConsentimiento ES una ruta de archivo
        // (guardado desde donación), tratarla como archivo
        if (archivoActual == null && firmaActual != null
                && (firmaActual.startsWith("uploads/") || firmaActual.contains("consentimientos/"))) {
            archivoActual = firmaActual;
            firmaActual = null;
        }

        model.addAttribute("consentimiento", request);
        model.addAttribute("consentimientoId", id);
        model.addAttribute("firmaActual", firmaActual);
        model.addAttribute("archivoActual", archivoActual);
        model.addAttribute("donantes", donanteService.listarTodos());
        model.addAttribute("titulo", "Editar consentimiento");
        return "Consentimiento/formulario";
    }

    @GetMapping("/consentimientos/cambiarEstado/{id}")
    public String cambiarEstado(@PathVariable Long id, RedirectAttributes ra) {
        consentimientoService.cambiarEstado(id);
        ra.addFlashAttribute("mensajeExito", "Estado actualizado correctamente.");
        return "redirect:/consentimientos";
    }

    @PostMapping("/consentimientos/actualizar/{id}")
    public String actualizarVista(
            @PathVariable Long id,
            @Valid @ModelAttribute("consentimiento") ConsentimientoRequest request,
            BindingResult result,
            @RequestParam(value = "archivoConsentimiento", required = false) MultipartFile archivoConsentimiento,
            Model model,
            RedirectAttributes ra) {

        if (result.hasErrors()) {
            model.addAttribute("donantes", donanteService.listarTodos());
            model.addAttribute("consentimientoId", id);
            return "Consentimiento/formulario";
        }
        consentimientoService.actualizarConArchivo(id, request, archivoConsentimiento);
        ra.addFlashAttribute("mensajeExito", "Consentimiento actualizado.");
        return "redirect:/consentimientos";
    }

    @Operation(summary = "Registrar consentimiento firmado")
    @PostMapping("/api/consentimientos")
    @ResponseBody
    public ResponseEntity<ConsentimientoResponse> registrar(
            @Valid @RequestBody ConsentimientoRequest request) {
        log.info("API POST /api/consentimientos - donanteId: {}", request.getDonanteId());
        return ResponseEntity.status(HttpStatus.CREATED).body(consentimientoService.guardar(request));
    }

    @Operation(summary = "Consultar consentimiento por donante")
    @GetMapping("/api/consentimientos/{donanteId}")
    @ResponseBody
    public ResponseEntity<ConsentimientoResponse> buscarPorDonante(@PathVariable Long donanteId) {
        log.info("API GET /api/consentimientos/{}", donanteId);
        return ResponseEntity.ok(consentimientoService.buscarPorDonanteId(donanteId));
    }

    @Operation(summary = "Actualizar consentimiento por ID")
    @PutMapping("/api/consentimientos/{id}")
    @ResponseBody
    public ResponseEntity<ConsentimientoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ConsentimientoRequest request) {
        log.info("API PUT /api/consentimientos/{}", id);
        return ResponseEntity.ok(consentimientoService.actualizar(id, request));
    }

    @Operation(summary = "Eliminar consentimiento por ID")
    @DeleteMapping("/api/consentimientos/{id}")
    @ResponseBody
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("API DELETE /api/consentimientos/{}", id);
        consentimientoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
