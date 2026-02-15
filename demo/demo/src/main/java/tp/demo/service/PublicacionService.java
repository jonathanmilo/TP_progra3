package tp.demo.service;

import org.springframework.stereotype.Service;
import tp.demo.model.Publicacion;
import tp.demo.model.PublicacionRelevante;
import tp.demo.repository.PublicacionRepository;
import tp.demo.repository.PublicacionesRelevantesRepository;
import tp.demo.model.Usuario;
import tp.demo.repository.UsuarioRepository;
import tp.demo.utils.KnapsackOptimizador;
import tp.demo.utils.MergeSort;

import java.util.*;

@Service
public class PublicacionService {
    private final UsuarioRepository usuarioRepository;
    private int k = 10;
    private final PublicacionRepository publicacionRepository;
    private final PublicacionesRelevantesRepository relevantesRepository;
    private final MergeSort mergeSortUtil;

    public PublicacionService(PublicacionRepository publicacionRepository,
                              PublicacionesRelevantesRepository relevantesRepository,
                              UsuarioRepository usuarioRepository) {
        this.publicacionRepository = publicacionRepository;
        this.relevantesRepository = relevantesRepository;
        this.mergeSortUtil = new MergeSort();
        this.usuarioRepository = usuarioRepository;
    }
    
    // ========== MÉTODOS PÚBLICOS ==========
    
    public Publicacion nuevaPublicacion(Publicacion publicacion) {
        if (publicacion.getFechaCreacion() == null) {
            publicacion.setFechaCreacion(new Date());
        }
        
        // Guardar directamente
        Publicacion nueva = publicacionRepository.save(publicacion);

        // Limpiar publicaciones menos relevantes si excedemos K
        mantenerTopKRelevantes();

        return nueva;
    }
    
    public boolean reaccionar(String idPublicacion, String idUsuario) {
        try {
            Optional<Publicacion> publicacionOpt = publicacionRepository.findById(idPublicacion);

            if (publicacionOpt.isEmpty()) {
                return false;
            }

            Publicacion publicacion = publicacionOpt.get();
            publicacion.agregarReaccion(idUsuario);
            publicacionRepository.save(publicacion);

            // Verificar si necesitamos ajustar el top K
            mantenerTopKRelevantes();

            return true;
        } catch(Exception e) {
            System.err.println("Error al reaccionar: " + e.getMessage());
            return false;
        }
    }
    
    public boolean agregarLike(String idPublicacion, String idUsuario) {
        try {
            Optional<Publicacion> publicacionOpt = publicacionRepository.findById(idPublicacion);

            if (publicacionOpt.isEmpty()) {
                return false;
            }

            Publicacion publicacion = publicacionOpt.get();
            publicacion.agregarLike(idUsuario);
            publicacionRepository.save(publicacion);

            // Verificar si necesitamos ajustar el top K
            mantenerTopKRelevantes();

            return true;
        } catch(Exception e) {
            System.err.println("Error al agregar like: " + e.getMessage());
            return false;
        }
    }

    public boolean agregarComentarios(String idPublicacion, String idUsuario, int cantidad) {
        return agregarComentarios(idPublicacion, idUsuario, cantidad, null);
    }

    public boolean agregarComentarios(String idPublicacion, String idUsuario, int cantidad, String textoComentario) {
        try {
            Optional<Publicacion> publicacionOpt = publicacionRepository.findById(idPublicacion);

            if (publicacionOpt.isEmpty()) {
                return false;
            }

            Publicacion publicacion = publicacionOpt.get();
            publicacion.agregarComentarios(idUsuario, cantidad, textoComentario);
            publicacionRepository.save(publicacion);

            // Verificar si necesitamos ajustar el top K
            mantenerTopKRelevantes();

            return true;
        } catch(Exception e) {
            System.err.println("Error al agregar comentarios: " + e.getMessage());
            return false;
        }
    }

    public boolean agregarReaccionCompleta(String idPublicacion, String idUsuario, Boolean like, int comentarios) {
        return agregarReaccionCompleta(idPublicacion, idUsuario, like, comentarios, null);
    }

    public boolean agregarReaccionCompleta(String idPublicacion, String idUsuario, Boolean like, int comentarios, String textoComentario) {
        try {
            Optional<Publicacion> publicacionOpt = publicacionRepository.findById(idPublicacion);

            if (publicacionOpt.isEmpty()) {
                return false;
            }

            Publicacion publicacion = publicacionOpt.get();
            publicacion.agregarReaccion(idUsuario, like, comentarios, textoComentario);
            publicacionRepository.save(publicacion);

            // Verificar si necesitamos ajustar el top K
            mantenerTopKRelevantes();

            return true;
        } catch(Exception e) {
            System.err.println("Error al agregar reacción completa: " + e.getMessage());
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
            // Retornar directamente de la colección pre-calculada
            // NO necesita ordenar cada vez, ya está ordenado
            List<PublicacionRelevante> relevantes = relevantesRepository.findAll();

            if (relevantes.isEmpty()) {
                // Si está vacía, calcular por primera vez
                mantenerTopKRelevantes();
                relevantes = relevantesRepository.findAll();
            }

            // Ordenar una vez más para asegurar el orden correcto
            // (esto es O(K log K) en lugar de O(N log N))
            relevantes.sort((a, b) -> Float.compare(b.getRelevancia(), a.getRelevancia()));

            return relevantes;
        } catch(Exception e) {
            System.err.println("Error: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public List<PublicacionRelevante> actualizarK(int nuevoK) {
        this.k = nuevoK;
        mantenerTopKRelevantes();
        return obtenerPublicacionesRelevantes();
    }

    public List<KnapsackOptimizador.ResultadoAsignacion> generarCampaña(int presupuestoMaximoEmpresa) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        List<Publicacion> anuncios = publicacionRepository.findAll();
        List<KnapsackOptimizador.ResultadoAsignacion> resultadosFinales = new ArrayList<>();

        int costoTotalAcumulado = 0;

        for (Usuario usuario : usuarios) {
            KnapsackOptimizador.ResultadoAsignacion asignacion = KnapsackOptimizador.optimizarParaUsuario(usuario, anuncios);

            if (costoTotalAcumulado + asignacion.getCostoEconomicoTotal() <= presupuestoMaximoEmpresa) {
                resultadosFinales.add(asignacion);
                costoTotalAcumulado += asignacion.getCostoEconomicoTotal();
            }
        }

        return resultadosFinales;
    }
    
    // ========== MÉTODOS PRIVADOS ==========
    
    private void mantenerTopKRelevantes() {
        try {
            // 1. Obtener TODAS las publicaciones
            List<Publicacion> todas = publicacionRepository.findAll();

            if (todas.isEmpty()) {
                return;
            }
            
            // 2. Ordenar por relevancia usando MergeSort
            List<Publicacion> ordenadas = mergeSortUtil.MergeSortByRelevancia(
                new ArrayList<>(todas), 0, todas.size() - 1);

            // 3. Tomar solo las K más relevantes
            int limite = Math.min(k, ordenadas.size());
            List<Publicacion> topK = ordenadas.subList(0, limite);

            // 4. Obtener IDs de las top K
            Set<String> idsTopK = new HashSet<>();
            for (Publicacion p : topK) {
                idsTopK.add(p.getId());
            }

            // 5. Limpiar publicaciones_relevantes que ya no están en top K
            List<PublicacionRelevante> relevantesActuales = relevantesRepository.findAll();
            for (PublicacionRelevante pr : relevantesActuales) {
                if (!idsTopK.contains(pr.getId())) {
                    relevantesRepository.delete(pr);
                }
            }
            
            // 6. Actualizar o insertar las top K en publicaciones_relevantes
            for (Publicacion p : topK) {
                PublicacionRelevante relevante = new PublicacionRelevante(p);
                relevantesRepository.save(relevante);
            }

            System.out.println("✅ Top K=" + k + " publicaciones relevantes actualizadas. Total: " + topK.size());

        } catch(Exception e) {
            System.err.println("Error al mantener top K: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void eliminarTodas() {
        publicacionRepository.deleteAll();
    }

    public void eliminar(String id) {
        publicacionRepository.deleteById(id);
    }

    /**
     * PROBLEMA 3: Optimización de Interacciones en la Portada
     *
     * Selecciona publicaciones destacadas que maximizan beneficio (likes + comentarios)
     * sin exceder el espacio disponible en la portada.
     *
     * Usa Programación Dinámica (Knapsack 0/1) para encontrar la solución óptima.
     *
     * @param espacioDisponible Espacio máximo en la portada
     * @return Resultado con publicaciones seleccionadas y métricas
     */
    public KnapsackOptimizador.ResultadoPortada optimizarPortada(int espacioDisponible) {
        List<Publicacion> todas = publicacionRepository.findAll();
        return KnapsackOptimizador.optimizarPortada(todas, espacioDisponible);
    }
}