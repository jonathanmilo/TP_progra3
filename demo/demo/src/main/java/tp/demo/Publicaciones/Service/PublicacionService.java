package tp.demo.Publicaciones.Service;

import org.springframework.stereotype.Service;
import tp.demo.Publicaciones.Entidad.Publicacion;
import tp.demo.Publicaciones.Entidad.PublicacionRelevante;
import tp.demo.Publicaciones.Repository.PublicacionRepository;
import tp.demo.Publicaciones.Repository.PublicacionesRelevantesRespository;
import tp.demo.Usuarios.Entidad.Usuario;
import tp.demo.Usuarios.Repository.UsuarioRepository;
import tp.demo.recursos.KnapsackOptimizador;
import tp.demo.recursos.funciones;

import java.util.*;

@Service
public class PublicacionService {
    private final UsuarioRepository usuarioRepository;
    private int k = 10;
    private final PublicacionRepository publicacionRepository;
    private final PublicacionesRelevantesRespository publicacionesRelevantesRespository;
    private final funciones funcionesUtil;

    public PublicacionService(PublicacionRepository publicacionRepository,
                              PublicacionesRelevantesRespository publicacionesRelevantesRespository, UsuarioRepository usuarioRepository) {
        this.publicacionRepository = publicacionRepository;
        this.publicacionesRelevantesRespository = publicacionesRelevantesRespository;
        this.funcionesUtil = new funciones();
        
        inicializarPublicacionesRelevantes();
        this.usuarioRepository = usuarioRepository;
    }
    
    // ========== MÉTODOS PÚBLICOS ==========
    
    public Publicacion nuevaPublicacion(Publicacion publicacion) {
        if (publicacion.getFechaCreacion() == null) {
            publicacion.setFechaCreacion(new Date());
        }
        
        // Guardar publicación original
        Publicacion nueva = publicacionRepository.save(publicacion);
        
        // Procesar para relevantes
        procesarNuevaPublicacionParaRelevantes(nueva);
        
        return nueva;
    }
    
    public boolean reaccionar(String idPublicacion, String idUsuario) {
        try {
            // 1. Buscar y actualizar publicación original
            Publicacion publicacion = publicacionRepository.findById(idPublicacion).orElse(null);
            if (publicacion == null) return false;
            
            publicacion.agregarReaccion(idUsuario);
            publicacionRepository.save(publicacion);
            
            // 2. Verificar si está en relevantes
            Optional<PublicacionRelevante> relevanteOpt = 
                publicacionesRelevantesRespository.findById(idPublicacion); // ← BUSCA POR MISMO ID
            
            if (relevanteOpt.isPresent()) {
                // Actualizar la existente
                PublicacionRelevante relevante = relevanteOpt.get();
                relevante.actualizarDesdePublicacion(publicacion);
                publicacionesRelevantesRespository.save(relevante);
                
                // Verificar si sigue siendo de las K más relevantes
                mantenerTopKRelevantes();
            } else {
                // Verificar si debería entrar en relevantes
                verificarYAgregarARelevantes(publicacion);
            }
            
            return true;
        } catch(Exception e) {
            System.err.println("Error al reaccionar: " + e.getMessage());
            return false;
        }
    }
    
    public List<Publicacion> listarTodas() {
        try {
            return publicacionRepository.findAll();
        } catch(Exception e) {
            System.err.println("Error: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public Publicacion getPublicacion(String id) {
        return publicacionRepository.findById(id).orElse(null);
    }
    
    public List<PublicacionRelevante> obtenerPublicacionesRelevantes() {
        try {
            List<PublicacionRelevante> relevantes = publicacionesRelevantesRespository.findAll();
            
            // Ordenar por relevancia usando tu MergeSort
            // Necesitamos convertir a lista de Publicacion temporalmente
            List<Publicacion> comoPublicaciones = convertirRelevantesAPublicaciones(relevantes);
            
            if (!comoPublicaciones.isEmpty()) {
                List<Publicacion> ordenadas = funcionesUtil.MergeSortByRelevancia(
                    comoPublicaciones, 0, comoPublicaciones.size() - 1);
                
                // Convertir de vuelta manteniendo el orden
                return convertirPublicacionesARelevantes(ordenadas, relevantes);
            }
            
            return relevantes;
        } catch(Exception e) {
            System.err.println("Error: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public List<PublicacionRelevante> actualizarK(int nuevoK) {
        this.k = nuevoK;
        recalcularPublicacionesRelevantesCompleto();
        return obtenerPublicacionesRelevantes();
    }

    /**
     * Este es el método "generarCampaña" que coordina todo el Problema 2.
     */
    public List<KnapsackOptimizador.ResultadoAsignacion> generarCampaña(int presupuestoMaximoEmpresa) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        List<Publicacion> anuncios = publicacionRepository.findAll();
        List<KnapsackOptimizador.ResultadoAsignacion> resultadosFinales = new ArrayList<>();

        int costoTotalAcumulado = 0;

        for (Usuario usuario : usuarios) {
            KnapsackOptimizador.ResultadoAsignacion asignacion = KnapsackOptimizador.optimizarParaUsuario(usuario, anuncios);

            // Verificación del presupuesto global de la empresa
            if (costoTotalAcumulado + asignacion.getCostoEconomicoTotal() <= presupuestoMaximoEmpresa) {
                resultadosFinales.add(asignacion);
                costoTotalAcumulado += asignacion.getCostoEconomicoTotal();
            }
        }

        return resultadosFinales;
    }
    
    // ========== MÉTODOS PRIVADOS ==========
    
    private void inicializarPublicacionesRelevantes() {
        if (publicacionesRelevantesRespository.count() == 0) {
            recalcularPublicacionesRelevantesCompleto();
        }
    }
    
    private void procesarNuevaPublicacionParaRelevantes(Publicacion nueva) {
        long totalRelevantes = publicacionesRelevantesRespository.count();
        
        if (totalRelevantes < k) {
            // Hay espacio, agregar
            PublicacionRelevante nuevaRelevante = new PublicacionRelevante(nueva);
            publicacionesRelevantesRespository.save(nuevaRelevante);
        } else {
            // Buscar la menos relevante
            List<PublicacionRelevante> relevantes = publicacionesRelevantesRespository.findAll();
            PublicacionRelevante menosRelevante = encontrarMenosRelevante(relevantes);
            
            if (menosRelevante != null && nueva.getRelevancia() > menosRelevante.getRelevancia()) {
                // Reemplazar
                publicacionesRelevantesRespository.delete(menosRelevante);
                PublicacionRelevante nuevaRelevante = new PublicacionRelevante(nueva);
                publicacionesRelevantesRespository.save(nuevaRelevante);
            }
        }
    }
    
    private void verificarYAgregarARelevantes(Publicacion publicacion) {
        List<PublicacionRelevante> relevantes = publicacionesRelevantesRespository.findAll();
        
        if (relevantes.size() < k) {
            // Hay espacio, agregar
            PublicacionRelevante nuevaRelevante = new PublicacionRelevante(publicacion);
            publicacionesRelevantesRespository.save(nuevaRelevante);
        } else {
            // Encontrar la menos relevante
            PublicacionRelevante menosRelevante = encontrarMenosRelevante(relevantes);
            
            if (menosRelevante != null && publicacion.getRelevancia() > menosRelevante.getRelevancia()) {
                // Reemplazar
                publicacionesRelevantesRespository.delete(menosRelevante);
                PublicacionRelevante nuevaRelevante = new PublicacionRelevante(publicacion);
                publicacionesRelevantesRespository.save(nuevaRelevante);
            }
        }
    }
    
    private void mantenerTopKRelevantes() {
        List<PublicacionRelevante> relevantes = publicacionesRelevantesRespository.findAll();
        
        if (relevantes.size() <= k) {
            return; // Ya tenemos K o menos
        }
        
        // Ordenar para encontrar las K más relevantes
        List<Publicacion> comoPublicaciones = convertirRelevantesAPublicaciones(relevantes);
        List<Publicacion> ordenadas = funcionesUtil.MergeSortByRelevancia(
            comoPublicaciones, 0, comoPublicaciones.size() - 1);
        
        // Tomar solo las primeras K
        List<String> idsTopK = new ArrayList<>();
        int limite = Math.min(k, ordenadas.size());
        for (int i = 0; i < limite; i++) {
            idsTopK.add(ordenadas.get(i).getId());
        }
        
        // Eliminar las que no están en top K
        for (PublicacionRelevante relevante : relevantes) {
            if (!idsTopK.contains(relevante.getId())) {
                publicacionesRelevantesRespository.delete(relevante);
            }
        }
    }
    
    private void recalcularPublicacionesRelevantesCompleto() {
        try {
            // Obtener todas las publicaciones originales
            List<Publicacion> todas = publicacionRepository.findAll();
            
            if (todas.isEmpty()) {
                publicacionesRelevantesRespository.deleteAll();
                return;
            }
            
            // Ordenar por relevancia
            List<Publicacion> ordenadas = funcionesUtil.MergeSortByRelevancia(
                new ArrayList<>(todas), 0, todas.size() - 1);
            
            // Tomar las K más relevantes
            int limite = Math.min(k, ordenadas.size());
            
            // Limpiar tabla de relevantes
            publicacionesRelevantesRespository.deleteAll();
            
            // Guardar como PublicacionRelevante (con mismo ID)
            for (int i = 0; i < limite; i++) {
                PublicacionRelevante relevante = new PublicacionRelevante(ordenadas.get(i));
                publicacionesRelevantesRespository.save(relevante);
            }
            
            System.out.println("Relevantes recalculados. K=" + k + ", guardadas=" + limite);
            
        } catch(Exception e) {
            System.err.println("Error al recalcular: " + e.getMessage());
        }
    }
    
    // ========== MÉTODOS AUXILIARES ==========
    
    private PublicacionRelevante encontrarMenosRelevante(List<PublicacionRelevante> relevantes) {
        if (relevantes.isEmpty()) return null;
        
        PublicacionRelevante menosRelevante = relevantes.get(0);
        for (PublicacionRelevante pr : relevantes) {
            if (pr.getRelevancia() < menosRelevante.getRelevancia()) {
                menosRelevante = pr;
            }
        }
        return menosRelevante;
    }
    
    private List<Publicacion> convertirRelevantesAPublicaciones(List<PublicacionRelevante> relevantes) {
        List<Publicacion> publicaciones = new ArrayList<>();
        
        for (PublicacionRelevante pr : relevantes) {
            // Crear una Publicacion temporal para ordenar
            Publicacion p = new Publicacion();
            p.setId(pr.getId());
            p.setContenido(pr.getContenido());
            p.setIdCreador(pr.getIdCreador());
            p.setFechaCreacion(pr.getFechaCreacion());
            p.setReacciones(pr.getReacciones());
            // La relevancia se calculará automáticamente con getRelevancia()
            publicaciones.add(p);
        }
        
        return publicaciones;
    }
    
    private List<PublicacionRelevante> convertirPublicacionesARelevantes(
            List<Publicacion> publicaciones, 
            List<PublicacionRelevante> originales) {
        
        Map<String, PublicacionRelevante> mapaOriginales = new HashMap<>();
        for (PublicacionRelevante pr : originales) {
            mapaOriginales.put(pr.getId(), pr);
        }
        
        List<PublicacionRelevante> resultado = new ArrayList<>();
        for (Publicacion p : publicaciones) {
            PublicacionRelevante pr = mapaOriginales.get(p.getId());
            if (pr != null) {
                resultado.add(pr);
            }
        }
        
        return resultado;
    }
    
    // ========== MÉTODOS DE CONSULTA RÁPIDA ==========
    
    /**
     * Verificar si una publicación está en relevantes (MUY RÁPIDO por ID)
     */
    public boolean esPublicacionRelevante(String idPublicacion) {
        return publicacionesRelevantesRespository.existsById(idPublicacion);
    }
    
    /**
     * Obtener publicación relevante por ID (rápido)
     */
    public Optional<PublicacionRelevante> obtenerPublicacionRelevante(String idPublicacion) {
        return publicacionesRelevantesRespository.findById(idPublicacion);
    }
    
    /**
     * Eliminar publicación relevante (si existe)
     */
    public void eliminarDeRelevantes(String idPublicacion) {
        if (publicacionesRelevantesRespository.existsById(idPublicacion)) {
            publicacionesRelevantesRespository.deleteById(idPublicacion);
        }
    }
}