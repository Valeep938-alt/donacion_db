package co.sena.cimm.adso.donacion.controller;

import co.sena.cimm.adso.donacion.dto.Request.DonanteRequest;
import co.sena.cimm.adso.donacion.dto.Response.DonanteResponse;
import co.sena.cimm.adso.donacion.enums.TipoSangre;
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

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "Donantes", description = "Gestión de donantes de sangre")
public class DonanteController {

    private final DonanteService donanteService;

    @GetMapping("/donantes")
    public String listarVista(Model model) {
        log.info("Vista GET /donantes");
        model.addAttribute("donantes", donanteService.listarTodos());
        model.addAttribute("titulo", "Donantes registrados");
        return "Donante/lista";
    }

    @GetMapping("/donantes/nuevo")
    public String nuevoFormulario(Model model) {
        log.info("Vista GET /donantes/nuevo");
        model.addAttribute("donante", new DonanteRequest());
        model.addAttribute("tiposSangre", TipoSangre.values());
        model.addAttribute("titulo", "Registrar donante");
        model.addAttribute("accion", "/donantes/guardar");
        return "Donante/formulario";
    }

    @PostMapping("/donantes/guardar")
    public String guardarVista(
            @Valid @ModelAttribute("donante") DonanteRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes ra) {

        if (result.hasErrors()) {
            model.addAttribute("tiposSangre", TipoSangre.values());
            model.addAttribute("titulo", "Registrar donante");
            model.addAttribute("accion", "/donantes/guardar");
            return "Donante/formulario";
        }

        try {
            donanteService.guardar(request);
            ra.addFlashAttribute("mensajeExito", "Donante registrado correctamente.");
            return "redirect:/donantes";
        } catch (RuntimeException ex) {
            model.addAttribute("mensajeError", ex.getMessage());
            model.addAttribute("tiposSangre", TipoSangre.values());
            model.addAttribute("titulo", "Registrar donante");
            model.addAttribute("accion", "/donantes/guardar");
            return "Donante/formulario";
        }
    }

    @GetMapping("/donantes/editar/{id}")
    public String editarFormulario(@PathVariable Long id, Model model) {
        log.info("Vista GET /donantes/editar/{}", id);
        DonanteResponse response = donanteService.buscarPorId(id);
        DonanteRequest request = new DonanteRequest();
        request.setNombres(response.getNombres());
        request.setApellidos(response.getApellidos());
        request.setDocumento(response.getDocumento());
        request.setFechaNacimiento(response.getFechaNacimiento());
        request.setTipoSangre(response.getTipoSangre());
        request.setPeso(response.getPeso());
        request.setTelefono(response.getTelefono());
        request.setCorreo(response.getCorreo());
        request.setDireccion(response.getDireccion());
        request.setFechaUltimaDonacion(response.getFechaUltimaDonacion());
        request.setAceptaConsentimiento(response.getAceptaConsentimiento());

        model.addAttribute("donante", request);
        model.addAttribute("donanteId", id);
        model.addAttribute("tiposSangre", TipoSangre.values());
        model.addAttribute("titulo", "Editar donante");
        model.addAttribute("accion", "/donantes/actualizar/" + id);
        return "Donante/formulario";
    }

    @PostMapping("/donantes/actualizar/{id}")
    public String actualizarVista(
            @PathVariable Long id,
            @Valid @ModelAttribute("donante") DonanteRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes ra) {

        if (result.hasErrors()) {
            model.addAttribute("tiposSangre", TipoSangre.values());
            model.addAttribute("donanteId", id);
            model.addAttribute("titulo", "Editar donante");
            model.addAttribute("accion", "/donantes/actualizar/" + id);
            return "Donante/formulario";
        }

        try {
            donanteService.actualizar(id, request);
            ra.addFlashAttribute("mensajeExito", "Donante actualizado correctamente.");
            return "redirect:/donantes";
        } catch (RuntimeException ex) {
            model.addAttribute("mensajeError", ex.getMessage());
            model.addAttribute("tiposSangre", TipoSangre.values());
            model.addAttribute("donanteId", id);
            model.addAttribute("titulo", "Editar donante");
            model.addAttribute("accion", "/donantes/actualizar/" + id);
            return "Donante/formulario";
        }
    }

    @GetMapping("/donantes/eliminar/{id}")
    public String eliminarVista(@PathVariable Long id, RedirectAttributes ra) {
        log.info("Vista GET /donantes/eliminar/{}", id);
        donanteService.eliminar(id);
        ra.addFlashAttribute("mensajeExito", "Donante eliminado correctamente.");
        return "redirect:/donantes";
    }

    @GetMapping("/donantes/detalle/{id}")
    public String detalleVista(@PathVariable Long id, Model model) {
        log.info("Vista GET /donantes/detalle/{}", id);
        model.addAttribute("donante", donanteService.buscarPorId(id));
        return "Donante/detalle";
    }

    @Operation(summary = "Listar todos los donantes")
    @GetMapping("/api/donantes")
    @ResponseBody
    public ResponseEntity<List<DonanteResponse>> listarTodos() {
        return ResponseEntity.ok(donanteService.listarTodos());
    }

    @Operation(summary = "Buscar donante por ID")
    @GetMapping("/api/donantes/{id}")
    @ResponseBody
    public ResponseEntity<DonanteResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(donanteService.buscarPorId(id));
    }

    @Operation(summary = "Registrar nuevo donante")
    @PostMapping("/api/donantes")
    @ResponseBody
    public ResponseEntity<DonanteResponse> registrar(@Valid @RequestBody DonanteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(donanteService.guardar(request));
    }

    @Operation(summary = "Actualizar donante por ID")
    @PutMapping("/api/donantes/{id}")
    @ResponseBody
    public ResponseEntity<DonanteResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody DonanteRequest request) {
        return ResponseEntity.ok(donanteService.actualizar(id, request));
    }

    @Operation(summary = "Eliminar donante por ID")
    @DeleteMapping("/api/donantes/{id}")
    @ResponseBody
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        donanteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}