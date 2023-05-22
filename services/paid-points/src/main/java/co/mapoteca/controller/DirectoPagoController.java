package co.mapoteca.controller;


import co.mapoteca.dto.directopago.DirectoPagoWebhook;
import co.mapoteca.service.DirectoPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("directopago")
public class DirectoPagoController {


    private final DirectoPagoService directoPagoService;

    public DirectoPagoController(DirectoPagoService directoPagoService) {
        this.directoPagoService = directoPagoService;
    }

    @PostMapping("/webhook")
    ResponseEntity<?> webhook(HttpServletRequest request, @RequestBody DirectoPagoWebhook directoPagoWebhook) throws Exception {
        directoPagoService.webhook(request,directoPagoWebhook);
        return ResponseEntity.ok().build();
    }
}
