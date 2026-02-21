package tp.demo.model;

import java.util.ArrayList;
import java.util.List;

public class ArbolBinarioPublicaciones {
    private NodoPublicacion raiz;
    private int tamaño;

    public ArbolBinarioPublicaciones() {
        this.raiz = null;
        this.tamaño = 0;
    }

    // Insertar una publicación ordenada por relevancia
    public void insertar(Publicacion publicacion) {
        NodoPublicacion nuevoNodo = new NodoPublicacion(publicacion);
        
        if (raiz == null) {
            raiz = nuevoNodo;
            tamaño++;
            return;
        }

        insertarRecursivo(raiz, nuevoNodo);
        tamaño++;
    }

    private void insertarRecursivo(NodoPublicacion actual, NodoPublicacion nuevo) {
        if (nuevo.getPeso() < actual.getPeso()) {
            if (actual.getIzquierdo() == null) {
                actual.setIzquierdo(nuevo);
            } else {
                insertarRecursivo(actual.getIzquierdo(), nuevo);
            }
        } else {
            if (actual.getDerecho() == null) {
                actual.setDerecho(nuevo);
            } else {
                insertarRecursivo(actual.getDerecho(), nuevo);
            }
        }
    }

    // Buscar publicación por ID
    public Publicacion buscar(String idPublicacion) {
        return buscarRecursivo(raiz, idPublicacion);
    }

    private Publicacion buscarRecursivo(NodoPublicacion nodo, String idPublicacion) {
        if (nodo == null) {
            return null;
        }

        if (nodo.getPublicacion().getId().equals(idPublicacion)) {
            return nodo.getPublicacion();
        }

        Publicacion izquierdo = buscarRecursivo(nodo.getIzquierdo(), idPublicacion);
        if (izquierdo != null) {
            return izquierdo;
        }

        return buscarRecursivo(nodo.getDerecho(), idPublicacion);
    }

    // Eliminar publicación
    public boolean eliminar(String idPublicacion) {
        if (raiz == null) {
            return false;
        }

        try {
            raiz = eliminarRecursivo(raiz, idPublicacion);
            tamaño--;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private NodoPublicacion eliminarRecursivo(NodoPublicacion nodo, String idPublicacion) {
        if (nodo == null) {
            return null;
        }

        // Buscar el nodo a eliminar
        if (idPublicacion.equals(nodo.getPublicacion().getId())) {
            // Caso 1: Nodo hoja
            if (nodo.esHoja()) {
                return null;
            }
            
            // Caso 2: Un solo hijo
            if (nodo.getIzquierdo() == null) {
                return nodo.getDerecho();
            }
            if (nodo.getDerecho() == null) {
                return nodo.getIzquierdo();
            }
            
            // Caso 3: Dos hijos - encontrar el sucesor (menor del subárbol derecho)
            NodoPublicacion sucesor = encontrarMinimo(nodo.getDerecho());
            nodo.setPublicacion(sucesor.getPublicacion());
            nodo.setPeso(sucesor.getPeso());
            nodo.setDerecho(eliminarRecursivo(nodo.getDerecho(), sucesor.getPublicacion().getId()));
            
        } else if (idPublicacion.compareTo(nodo.getPublicacion().getId()) < 0) {
            nodo.setIzquierdo(eliminarRecursivo(nodo.getIzquierdo(), idPublicacion));
        } else {
            nodo.setDerecho(eliminarRecursivo(nodo.getDerecho(), idPublicacion));
        }
        
        return nodo;
    }

    private NodoPublicacion encontrarMinimo(NodoPublicacion nodo) {
        while (nodo.getIzquierdo() != null) {
            nodo = nodo.getIzquierdo();
        }
        return nodo;
    }

    // Obtener publicaciones ordenadas por relevancia (in-order traversal)
    public List<Publicacion> obtenerPublicacionesOrdenadas() {
        List<Publicacion> resultado = new ArrayList<>();
        inOrderRecursivo(raiz, resultado);
        return resultado;
    }

    private void inOrderRecursivo(NodoPublicacion nodo, List<Publicacion> resultado) {
        if (nodo != null) {
            inOrderRecursivo(nodo.getIzquierdo(), resultado);
            resultado.add(nodo.getPublicacion());
            inOrderRecursivo(nodo.getDerecho(), resultado);
        }
    }

    // Obtener publicaciones por rango de relevancia
    public List<Publicacion> obtenerPublicacionesPorRango(float minRelevancia, float maxRelevancia) {
        List<Publicacion> resultado = new ArrayList<>();
        buscarPorRangoRecursivo(raiz, minRelevancia, maxRelevancia, resultado);
        return resultado;
    }

    private void buscarPorRangoRecursivo(NodoPublicacion nodo, float min, float max, List<Publicacion> resultado) {
        if (nodo == null) {
            return;
        }

        if (nodo.getPeso() > min) {
            buscarPorRangoRecursivo(nodo.getIzquierdo(), min, max, resultado);
        }

        if (nodo.getPeso() >= min && nodo.getPeso() <= max) {
            resultado.add(nodo.getPublicacion());
        }

        if (nodo.getPeso() < max) {
            buscarPorRangoRecursivo(nodo.getDerecho(), min, max, resultado);
        }
    }

    // Obtener las N publicaciones con mayor relevancia
    public List<Publicacion> obtenerTopPublicaciones(int n) {
        List<Publicacion> resultado = new ArrayList<>();
        obtenerTopRecursivo(raiz, resultado, n);
        return resultado;
    }

    private void obtenerTopRecursivo(NodoPublicacion nodo, List<Publicacion> resultado, int n) {
        if (nodo == null || resultado.size() >= n) {
            return;
        }

        // Recorrer de derecha a izquierda para obtener las mayores relevancias primero
        obtenerTopRecursivo(nodo.getDerecho(), resultado, n);
        
        if (resultado.size() < n) {
            resultado.add(nodo.getPublicacion());
            obtenerTopRecursivo(nodo.getIzquierdo(), resultado, n);
        }
    }

    // Actualizar relevancia de una publicación
    public boolean actualizarRelevancia(String idPublicacion) {
        Publicacion publicacion = buscar(idPublicacion);
        if (publicacion == null) {
            return false;
        }

        // Guardar la relevancia anterior
        float relevanciaAnterior = publicacion.getRelevancia();
        
        // Recalcular relevancia
        float nuevaRelevancia = publicacion.getRelevancia(); // El método ya recalcula
        
        // Si la relevancia cambió significativamente, reinsertar
        if (Math.abs(nuevaRelevancia - relevanciaAnterior) > 0.1) {
            eliminar(idPublicacion);
            insertar(publicacion);
        }
        
        return true;
    }

    // Getters
    public NodoPublicacion getRaiz() {
        return raiz;
    }

    public int getTamaño() {
        return tamaño;
    }

    public boolean estaVacio() {
        return raiz == null;
    }

    // Método para obtener altura del árbol
    public int getAltura() {
        return calcularAltura(raiz);
    }

    private int calcularAltura(NodoPublicacion nodo) {
        if (nodo == null) {
            return 0;
        }
        return 1 + Math.max(calcularAltura(nodo.getIzquierdo()), calcularAltura(nodo.getDerecho()));
    }

    // Recorridos adicionales
    public List<Publicacion> recorridoPreOrden() {
        List<Publicacion> resultado = new ArrayList<>();
        preOrdenRecursivo(raiz, resultado);
        return resultado;
    }

    private void preOrdenRecursivo(NodoPublicacion nodo, List<Publicacion> resultado) {
        if (nodo != null) {
            resultado.add(nodo.getPublicacion());
            preOrdenRecursivo(nodo.getIzquierdo(), resultado);
            preOrdenRecursivo(nodo.getDerecho(), resultado);
        }
    }

    public List<Publicacion> recorridoPostOrden() {
        List<Publicacion> resultado = new ArrayList<>();
        postOrdenRecursivo(raiz, resultado);
        return resultado;
    }

    private void postOrdenRecursivo(NodoPublicacion nodo, List<Publicacion> resultado) {
        if (nodo != null) {
            postOrdenRecursivo(nodo.getIzquierdo(), resultado);
            postOrdenRecursivo(nodo.getDerecho(), resultado);
            resultado.add(nodo.getPublicacion());
        }
    }
}