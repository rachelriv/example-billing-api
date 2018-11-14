package example.billing.service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class HomeController {

    @RequestMapping("/")
    public RedirectView home() {
        return new RedirectView("redirect:/swagger-ui.html");
    }

}
