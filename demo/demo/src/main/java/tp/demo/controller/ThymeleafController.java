package tp.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tp.demo.model.ResultadoAsignacion;
import tp.demo.model.ResultadoPortada;
import tp.demo.service.PublicacionService;
import tp.demo.service.UsuarioService;

import java.util.List;

@Controller
public class ThymeleafController {

    private final UsuarioService usuarioService;
    private final PublicacionService publicacionService;

    public ThymeleafController(UsuarioService usuarioService, PublicacionService publicacionService) {
        this.usuarioService = usuarioService;
        this.publicacionService = publicacionService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/view/usuarios")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        return "view/usuarios";
    }

    @GetMapping("/view/publicaciones")
    public String listarPublicaciones(Model model) {
        model.addAttribute("publicaciones", publicacionService.listarTodas());
        return "view/publicaciones";
    }

    @GetMapping("/view/relevantes")
    public String publicacionesRelevantes(Model model) {
        model.addAttribute("publicaciones", publicacionService.obtenerPublicacionesRelevantes());
        return "view/relevantes";
    }

    @GetMapping("/view/optimizar-publicidad")
    public String optimizarPublicidad(Model model, @RequestParam(required = false, defaultValue = "10000") int presupuesto) {
        List<ResultadoAsignacion> resultados = publicacionService.generarCampaña(presupuesto);

        // Filtrar solo usuarios que recibieron anuncios (tienen costo > 0)
        List<ResultadoAsignacion> resultadosConAnuncios = resultados.stream()
            .filter(r -> r.getCostoEconomicoTotal() > 0)
            .toList();

        // Calcular totales
        int costoTotal = resultadosConAnuncios.stream().mapToInt(ResultadoAsignacion::getCostoEconomicoTotal).sum();
        int alcanceTotal = resultadosConAnuncios.stream().mapToInt(ResultadoAsignacion::getAlcanceTotal).sum();
        int tiempoTotal = resultadosConAnuncios.stream().mapToInt(ResultadoAsignacion::getTiempoTotalUsado).sum();

        model.addAttribute("presupuestoDefault", presupuesto);
        model.addAttribute("resultados", resultadosConAnuncios); // Solo usuarios con anuncios
        model.addAttribute("costoTotal", costoTotal);
        model.addAttribute("alcanceTotal", alcanceTotal);
        model.addAttribute("tiempoTotal", tiempoTotal);
        model.addAttribute("usuariosSinPresupuesto", resultados.size() - resultadosConAnuncios.size());

        return "view/optimizar-publicidad";
    }

    @GetMapping("/view/optimizar-portada")
    public String optimizarPortada(Model model,
                                   @RequestParam(required = false, defaultValue = "20") int espacioDisponible) {
        ResultadoPortada resultado = publicacionService.optimizarPortada(espacioDisponible);

        model.addAttribute("espacioDefault", espacioDisponible);
        model.addAttribute("resultado", resultado);

        return "view/optimizar-portada";
    }
}
