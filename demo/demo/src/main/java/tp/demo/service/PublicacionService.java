package tp.demo.service;

import org.springframework.stereotype.Service;
import tp.demo.model.Publicacion;
import tp.demo.model.PublicacionRelevante;
import tp.demo.model.ResultadoAsignacion;
import tp.demo.model.ResultadoPortada;
import tp.demo.repository.PublicacionRepository;
import tp.demo.repository.PublicacionesRelevantesRepository;
import tp.demo.model.Usuario;
import tp.demo.repository.UsuarioRepository;
import tp.demo.utils.KnapsackOptimizador;
import tp.demo.utils.MergeSort;

import java.util.*;

import static tp.demo.utils.InsertionSort.insertionSortUsuariosDesc;

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
       //  mantenerTopKRelevantes();

        return nueva;
    }

    public List<PublicacionRelevante> actualizarK(int nuevoK) {
        this.k = nuevoK;
        mantenerTopKRelevantes();
        return obtenerPublicacionesRelevantes();
    }
    
    public boolean reaccionar(String idPublicacion, String idUsuario) {
        try {
            Optional<Publicacion> publicacionOpt = publicacionRepository.findById(idPublicacion);
            List<PublicacionRelevante> publicacionesRelevantes = obtenerPublicacionesRelevantes();

            if (publicacionOpt.isEmpty()) {
                return false;
            }

            Publicacion publicacion = publicacionOpt.get();
            publicacion.agregarReaccion(idUsuario);
            publicacionRepository.save(publicacion);
            if(publicacionesRelevantes.size() > 0) {
                for (PublicacionRelevante pr : publicacionesRelevantes) {
                    if (pr.getId().equals(publicacion.getId())) {
                        pr.setRelevancia(publicacion.getRelevancia());
                        relevantesRepository.save(pr);
                        
                        actualizarRelevantes();
                        break;
                    }
                }
            }


            // Verificar si necesitamos ajustar el top K
        //    mantenerTopKRelevantes();

            return true;
        } catch(Exception e) {
            System.err.println("Error al reaccionar: " + e.getMessage());
            return false;
        }
    }
    
    public List<PublicacionRelevante> actualizarRelevantes() {
        List<PublicacionRelevante> publicaciones = obtenerPublicacionesRelevantes();

       List<PublicacionRelevante> sortedPublicaciones = mergeSortUtil.MergeSortRelevantes(publicaciones, 0, publicaciones.size() - 1);
        relevantesRepository.saveAll(sortedPublicaciones);
        return sortedPublicaciones;
    }


    public boolean agregarLike(String idPublicacion, String idUsuario) {
        try {
            Optional<Publicacion> publicacionOpt = publicacionRepository.findById(idPublicacion);

            if (publicacionOpt.isEmpty()) {
                return false;
            }

            Publicacion publicacion = publicacionOpt.get();

            // Agregar like
            publicacion.agregarLike(idUsuario);

            publicacionRepository.save(publicacion);

            // Actualizar relevancia del creador
            actualizarRelevanciaUsuario(publicacion.getIdCreador());

            // Verificar si necesitamos ajustar el top K
            actualizarRelevantes();

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

            // Agregar comentarios
            publicacion.agregarComentarios(idUsuario, cantidad, textoComentario);

            publicacionRepository.save(publicacion);

            // Actualizar relevancia del creador
            actualizarRelevanciaUsuario(publicacion.getIdCreador());

            // Verificar si necesitamos ajustar el top K
            actualizarRelevantes();

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

            // Agregar reacción
            publicacion.agregarReaccion(idUsuario, like, comentarios, textoComentario);

            publicacionRepository.save(publicacion);

            // Actualizar relevancia del creador
            actualizarRelevanciaUsuario(publicacion.getIdCreador());

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
         //   relevantes.sort((a, b) -> Float.compare(b.getRelevancia(), a.getRelevancia()));

            return relevantes;
        } catch(Exception e) {
            System.err.println("Error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Recalcula la relevancia de todas las publicaciones y actualiza el Top K.
     * Este método debe llamarse cuando:
     * - Se agregan nuevas publicaciones
     * - Cambia el engagement (likes/comentarios)
     * - Se quiere refrescar el Top K manualmente
     */
    public void calcularYActualizarRelevancia() {
        List<Publicacion> todasPublicaciones = publicacionRepository.findAll();
        for (Publicacion pub : todasPublicaciones) {
            pub.getRelevancia();
            publicacionRepository.save(pub);
        }

        // Actualizar tabla de publicaciones relevantes (Top K)
        mantenerTopKRelevantes();
    }

    /**
     * Genera una campaña publicitaria optimizada.
     * Estrategia: Cuando el presupuesto es limitado, es mejor asignar anuncios
     * a usuarios que crean contenido popular, ya que tienen mayor engagement
     * y potencial de conversión.
     *
     * @param presupuestoMaximoEmpresa Presupuesto total disponible
     * @return Lista de asignaciones de anuncios por usuario
     */
    public List<ResultadoAsignacion> generarCampaña(int presupuestoMaximoEmpresa) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        List<Publicacion> todasPublicaciones = publicacionRepository.findAll();

        insertionSortUsuariosDesc(usuarios);

        List<ResultadoAsignacion> resultadosFinales = new ArrayList<>();
        int costoTotalAcumulado = 0;

        for (Usuario usuario : usuarios) {
            ResultadoAsignacion asignacion =
                KnapsackOptimizador.optimizarParaUsuario(usuario, todasPublicaciones);

            if (costoTotalAcumulado + asignacion.getCostoEconomicoTotal() <= presupuestoMaximoEmpresa) {
                resultadosFinales.add(asignacion);
                costoTotalAcumulado += asignacion.getCostoEconomicoTotal();
            } else {
                // Los usuarios con contenido menos relevante se quedarán sin asignación
                ResultadoAsignacion asignacionVacia =
                    new ResultadoAsignacion(usuario.getNombre());
                resultadosFinales.add(asignacionVacia);
            }
        }

        return resultadosFinales;
    }
    
    // ========== MÉTODOS PRIVADOS ==========
    
    /**
     * Actualiza la relevancia acumulada del usuario recalculando desde todas sus publicaciones.
     * Esto garantiza que el cache siempre esté correcto.
     */
    private void actualizarRelevanciaUsuario(String idUsuario) {
        if (idUsuario == null) {
            return;
        }

        try {
            // Obtener usuario
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
            if (usuarioOpt.isEmpty()) {
                return;
            }

            Usuario usuario = usuarioOpt.get();

            // Recalcular relevancia total desde todas sus publicaciones
            List<Publicacion> todasPublicaciones = publicacionRepository.findAll();
            int relevanciaTotal = 0;

            for (Publicacion p : todasPublicaciones) {
                if (p.getIdCreador() != null && p.getIdCreador().equals(idUsuario)) {
                    relevanciaTotal += p.getRelevancia();
                }
            }

            // Actualizar cache
            usuario.setRelevanciaEnPosteos(relevanciaTotal);
            usuarioRepository.save(usuario);

            System.out.println("Usuario " + usuario.getNombre() + " relevancia actualizada: " + relevanciaTotal);

        } catch (Exception e) {
            System.err.println("Error al actualizar relevancia de usuario: " + e.getMessage());
        }
    }

    private void mantenerTopKRelevantes() {
        try {
            List<Publicacion> publicaciones = publicacionRepository.findAll();

            if (publicaciones.isEmpty()) {
                return;
            }
            
            List<Publicacion> ordenadas = mergeSortUtil.MergeSortByRelevancia(
                new ArrayList<>(publicaciones), 0, publicaciones.size() - 1);

            int limite = Math.min(this.k, ordenadas.size());
            List<Publicacion> topK = ordenadas.subList(0, limite);

            Set<String> idsTopK = new HashSet<>();
            for (Publicacion p : topK) {
                idsTopK.add(p.getId());
            }

            // Limpiar publicaciones_relevantes que ya no están en top K
            List<PublicacionRelevante> relevantesActuales = relevantesRepository.findAll();
            for (PublicacionRelevante pr : relevantesActuales) {
                if (!idsTopK.contains(pr.getId())) {
                    relevantesRepository.delete(pr);
                }
            }
            
            // Actualizar o insertar las top K en publicaciones_relevantes
            for (Publicacion p : topK) {
                PublicacionRelevante relevante = new PublicacionRelevante(p);
                relevantesRepository.save(relevante);
            }


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
     * Usa Programación Dinámica sobre el algoritmo de la mochila.
     *
     * @param espacioDisponible Espacio máximo en la portada
     * @return Resultado con publicaciones seleccionadas y métricas
     */
    public ResultadoPortada optimizarPortada(int espacioDisponible) {
        List<Publicacion> todas = publicacionRepository.findAll();
        return KnapsackOptimizador.optimizarPortada(todas, espacioDisponible);
    }
}