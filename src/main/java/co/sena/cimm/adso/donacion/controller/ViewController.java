package co.sena.cimm.adso.donacion.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class ViewController {

    @GetMapping("/")
    public String index() {
        log.info("Accediendo a landing page");
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        log.info("Accediendo a página de login");
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        log.info("Accediendo a dashboard");
        return "dashboard";
    }
}