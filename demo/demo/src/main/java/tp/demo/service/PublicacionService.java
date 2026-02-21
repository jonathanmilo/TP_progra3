package tp.demo.service;

import org.springframework.stereotype.Service;

import tp.demo.model.ArbolBinarioPublicaciones;
import tp.demo.model.Publicacion;
import tp.demo.model.ResultadoAsignacion;
import tp.demo.model.ResultadoPortada;
import tp.demo.repository.PublicacionRepository;
import tp.demo.model.Usuario;
import tp.demo.repository.UsuarioRepository;
import tp.demo.utils.KnapsackOptimizador;
import tp.demo.utils.MergeSort;

import java.util.*;

import static tp.demo.utils.InsertionSort.insertionSortUsuariosDesc;

@Service
public class PublicacionService {
    private final UsuarioRepository usuarioRepository;
    private final PublicacionRepository publicacionRepository;
    private final MergeSort mergeSortUtil;

    public PublicacionService(PublicacionRepository publicacionRepository,
                              UsuarioRepository usuarioRepository) {
        this.publicacionRepository = publicacionRepository;
        this.mergeSortUtil = new MergeSort();
        this.usuarioRepository = usuarioRepository;
    }
    
    // ========== MÉTODOS PÚBLICOS ==========
    
    public Publicacion nuevaPublicacion(Publicacion publicacion) {
        if (publicacion.getFechaCreacion() == null) {
            publicacion.setFechaCreacion(new Date());
        }
        
        // Guardar en MongoDB
        Publicacion nueva = publicacionRepository.save(publicacion);

        // Insertar en el árbol binario global
        Publicacion.getArbolGlobal().insertar(nueva);

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
            
            // Guardar en MongoDB
            publicacionRepository.save(publicacion);
            
            // La relevancia se actualiza automáticamente en el método getRelevancia()
            // que ya tiene la lógica para actualizar el árbol

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

            // Agregar like
            publicacion.agregarLike(idUsuario);

            // Guardar en MongoDB
            publicacionRepository.save(publicacion);

            // Actualizar relevancia del creador
            actualizarRelevanciaUsuario(publicacion.getIdCreador());

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

            // Guardar en MongoDB
            publicacionRepository.save(publicacion);

            // Actualizar relevancia del creador
            actualizarRelevanciaUsuario(publicacion.getIdCreador());

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

            // Guardar en MongoDB
            publicacionRepository.save(publicacion);

            // Actualizar relevancia del creador
            actualizarRelevanciaUsuario(publicacion.getIdCreador());

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
    
    /**
     * Obtiene las publicaciones ordenadas por relevancia usando el árbol binario
     */
    public List<Publicacion> obtenerPublicacionesOrdenadas() {
        return Publicacion.getArbolGlobal().obtenerPublicacionesOrdenadas();
    }

    /**
     * Obtiene las top K publicaciones más relevantes usando el árbol binario
     */
    public List<Publicacion> obtenerTopPublicaciones(int k) {
        return Publicacion.getArbolGlobal().obtenerTopPublicaciones(k);
    }

    /**
     * Obtiene publicaciones en un rango de relevancia
     */
    public List<Publicacion> obtenerPublicacionesPorRango(float minRelevancia, float maxRelevancia) {
        return Publicacion.getArbolGlobal().obtenerPublicacionesPorRango(minRelevancia, maxRelevancia);
    }

    /**
     * Recalcula la relevancia de todas las publicaciones y actualiza el árbol
     */
    public void recalcularTodasLasRelevancias() {
        List<Publicacion> todasPublicaciones = publicacionRepository.findAll();
        
        // Limpiar el árbol actual
        Publicacion.setArbolGlobal(new ArbolBinarioPublicaciones());
        
        for (Publicacion pub : todasPublicaciones) {
            // Forzar recálculo de relevancia
            pub.getRelevancia();
            publicacionRepository.save(pub);
            
            // Reinsertar en el árbol
            Publicacion.getArbolGlobal().insertar(pub);
        }
    }

    /**
     * Genera una campaña publicitaria optimizada.
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
                ResultadoAsignacion asignacionVacia =
                    new ResultadoAsignacion(usuario.getNombre());
                resultadosFinales.add(asignacionVacia);
            }
        }

        return resultadosFinales;
    }
    
    // ========== MÉTODOS PRIVADOS ==========
    
    /**
     * Actualiza la relevancia acumulada del usuario
     */
    private void actualizarRelevanciaUsuario(String idUsuario) {
        if (idUsuario == null) {
            return;
        }

        try {
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

            usuario.setRelevanciaEnPosteos(relevanciaTotal);
            usuarioRepository.save(usuario);

            System.out.println("Usuario " + usuario.getNombre() + " relevancia actualizada: " + relevanciaTotal);

        } catch (Exception e) {
            System.err.println("Error al actualizar relevancia de usuario: " + e.getMessage());
        }
    }

    public void eliminarTodas() {
        publicacionRepository.deleteAll();
        Publicacion.setArbolGlobal(new ArbolBinarioPublicaciones()); // Reiniciar árbol
    }

    public void eliminar(String id) {
        // Eliminar del árbol primero
        Publicacion.getArbolGlobal().eliminar(id);
        // Luego de MongoDB
        publicacionRepository.deleteById(id);
    }

    /**
     * PROBLEMA 3: Optimización de Interacciones en la Portada
     */
    public ResultadoPortada optimizarPortada(int espacioDisponible) {
        List<Publicacion> todas = publicacionRepository.findAll();
        return KnapsackOptimizador.optimizarPortada(todas, espacioDisponible);
    }

    /**
     * Método para inicializar el árbol al arrancar la aplicación
     */
    public void inicializarArbol() {
        List<Publicacion> todas = publicacionRepository.findAll();
        ArbolBinarioPublicaciones nuevoArbol = new ArbolBinarioPublicaciones();
        
        for (Publicacion pub : todas) {
            nuevoArbol.insertar(pub);
        }
        
        Publicacion.setArbolGlobal(nuevoArbol);
        System.out.println("Árbol binario inicializado con " + todas.size() + " publicaciones");
    }




}