package tp.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tp.demo.model.Publicacion;
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
        
        // Agregar estadísticas del árbol para mostrar en la vista
        var arbol = Publicacion.getArbolGlobal();
        model.addAttribute("totalEnArbol", arbol.getTamaño());
        model.addAttribute("alturaArbol", arbol.getAltura());
        
        return "view/publicaciones";
    }

    /**
     * Vista de publicaciones ordenadas por relevancia usando el árbol binario
     */
    @GetMapping("/view/publicaciones-ordenadas")
    public String publicacionesOrdenadas(Model model) {
        List<Publicacion> publicaciones = publicacionService.obtenerPublicacionesOrdenadas();
        model.addAttribute("publicaciones", publicaciones);
        model.addAttribute("totalPublicaciones", publicaciones.size());
        
        // Agregar estadísticas del árbol
        var arbol = Publicacion.getArbolGlobal();
        model.addAttribute("estadisticas", new ArbolEstadisticasDTO(
            arbol.getTamaño(),
            arbol.getAltura(),
            arbol.estaVacio()
        ));
        
        return "view/publicaciones-ordenadas";
    }

    /**
     * Vista de top publicaciones más relevantes
     */
    @GetMapping("/view/top-publicaciones")
    public String topPublicaciones(Model model,
                                   @RequestParam(required = false, defaultValue = "10") int k) {
        List<Publicacion> topPublicaciones = publicacionService.obtenerTopPublicaciones(k);
        
        model.addAttribute("publicaciones", topPublicaciones);
        model.addAttribute("k", k);
        model.addAttribute("totalMostradas", topPublicaciones.size());
        
        // Agregar la relevancia mínima y máxima del top
        if (!topPublicaciones.isEmpty()) {
            float minRelevancia = topPublicaciones.get(topPublicaciones.size() - 1).getRelevancia();
            float maxRelevancia = topPublicaciones.get(0).getRelevancia();
            model.addAttribute("minRelevancia", minRelevancia);
            model.addAttribute("maxRelevancia", maxRelevancia);
        }
        
        return "view/top-publicaciones";
    }

    /**
     * Vista de búsqueda por rango de relevancia
     */
    @GetMapping("/view/publicaciones-por-rango")
    public String publicacionesPorRango(Model model,
                                        @RequestParam(required = false, defaultValue = "0") float min,
                                        @RequestParam(required = false, defaultValue = "100") float max) {
        List<Publicacion> publicaciones = publicacionService.obtenerPublicacionesPorRango(min, max);
        
        model.addAttribute("publicaciones", publicaciones);
        model.addAttribute("min", min);
        model.addAttribute("max", max);
        model.addAttribute("totalEncontradas", publicaciones.size());
        
        return "view/publicaciones-por-rango";
    }

    /**
     * Vista de estadísticas del árbol binario
     */
    @GetMapping("/view/estadisticas-arbol")
    public String estadisticasArbol(Model model) {
        var arbol = Publicacion.getArbolGlobal();
        
        model.addAttribute("tamaño", arbol.getTamaño());
        model.addAttribute("altura", arbol.getAltura());
        model.addAttribute("vacio", arbol.estaVacio());
        
        // Obtener algunas muestras para mostrar
        List<Publicacion> top5 = publicacionService.obtenerTopPublicaciones(5);
        List<Publicacion> menosRelevantes = publicacionService.obtenerPublicacionesPorRango(0, 5);
        
        model.addAttribute("top5", top5);
        model.addAttribute("menosRelevantes", 
            menosRelevantes.size() > 5 ? menosRelevantes.subList(0, 5) : menosRelevantes);
        
        return "view/estadisticas-arbol";
    }

    /**
     * Vista de publicaciones relevantes (mantenida por compatibilidad)
     * Ahora redirige a top-publicaciones
     */
    @GetMapping("/view/relevantes")
    public String publicacionesRelevantes(Model model) {
        // Redirigir al nuevo endpoint con valor por defecto de 10
        return "redirect:/view/top-publicaciones?k=10";
    }

    @GetMapping("/view/optimizar-publicidad")
    public String optimizarPublicidad(Model model, 
                                      @RequestParam(required = false, defaultValue = "10000") int presupuesto) {
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
        model.addAttribute("resultados", resultadosConAnuncios);
        model.addAttribute("costoTotal", costoTotal);
        model.addAttribute("alcanceTotal", alcanceTotal);
        model.addAttribute("tiempoTotal", tiempoTotal);
        model.addAttribute("usuariosSinPresupuesto", resultados.size() - resultadosConAnuncios.size());
        
        // Agregar eficiencia del gasto (alcance por unidad de costo)
        if (costoTotal > 0) {
            float eficiencia = (float) alcanceTotal / costoTotal;
            model.addAttribute("eficiencia", String.format("%.2f", eficiencia));
        }

        return "view/optimizar-publicidad";
    }

    @GetMapping("/view/optimizar-portada")
    public String optimizarPortada(Model model,
                                @RequestParam(required = false, defaultValue = "20") int espacioDisponible) {
        // Obtener resultado optimizado
        ResultadoPortada resultado = publicacionService.optimizarPortada(espacioDisponible);
        
        // Agregar al modelo usando los nuevos métodos de ResultadoPortada
        model.addAttribute("espacioDefault", espacioDisponible);
        model.addAttribute("resultado", resultado);
        
        // Métricas ahora disponibles directamente desde resultado
        if (resultado != null) {
            model.addAttribute("totalLikes", resultado.getLikesTotales());
            model.addAttribute("totalComentarios", resultado.getComentariosTotales());
            model.addAttribute("beneficioTotal", resultado.getBeneficioTotal());
            model.addAttribute("espacioUsado", resultado.getEspacioUsado());
            model.addAttribute("espacioLibre", resultado.getEspacioLibre());
            model.addAttribute("eficiencia", resultado.getEficiencia());
            model.addAttribute("relevanciaPromedio", resultado.getRelevanciaPromedio());
            model.addAttribute("porcentajeUso", resultado.getPorcentajeUso());
            model.addAttribute("cantidadPublicaciones", resultado.getCantidadPublicaciones());
            
            // Métricas adicionales de utilidad
            if (resultado.hayResultados()) {
                model.addAttribute("hayResultados", true);
                model.addAttribute("publicacionMasRelevante", resultado.getPublicacionMasRelevante());
                model.addAttribute("publicacionMasGrande", resultado.getPublicacionMasGrande());
                model.addAttribute("publicacionMasPequena", resultado.getPublicacionMasPequena());
                model.addAttribute("idsPublicaciones", resultado.getIdsPublicaciones());
            } else {
                model.addAttribute("hayResultados", false);
                model.addAttribute("mensaje", "No se encontraron publicaciones para optimizar");
            }
        }

        return "view/optimizar-portada";
    }

    /**
     * DTO para estadísticas del árbol (para pasar a la vista)
     */
    public static class ArbolEstadisticasDTO {
        private final int tamaño;
        private final int altura;
        private final boolean vacio;

        public ArbolEstadisticasDTO(int tamaño, int altura, boolean vacio) {
            this.tamaño = tamaño;
            this.altura = altura;
            this.vacio = vacio;
        }

        public int getTamaño() { return tamaño; }
        public int getAltura() { return altura; }
        public boolean isVacio() { return vacio; }
    }
}