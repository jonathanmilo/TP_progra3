package tp.demo.utils;

import tp.demo.model.Publicacion;
import tp.demo.model.ResultadoAsignacion;
import tp.demo.model.ResultadoPortada;
import tp.demo.model.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Optimizador que resuelve el Problema de la Mochila (Knapsack)
 * usando Programación Dinámica.
 *
 * Implementación genérica y reutilizable sin duplicación de código.
 */
public class KnapsackOptimizador {

    /**
     * Método genérico de Knapsack con Programación Dinámica.
     * Resuelve el problema de la mochila maximizando beneficio sin exceder capacidad.
     *
     * @param items Lista de items (publicaciones)
     * @param capacidad Capacidad máxima (tiempo o espacio)
     * @param obtenerPeso Función para obtener el peso de cada item
     * @param obtenerBeneficio Función para obtener el beneficio de cada item
     * @return Lista de índices de items seleccionados
     */
    private static List<Integer> mochila(
            List<Publicacion> items,
            int capacidad,
            Function<Publicacion, Integer> obtenerPeso,
            Function<Publicacion, Integer> obtenerBeneficio) {

        int n = items.size();
        if (n == 0 || capacidad <= 0) {
            return new ArrayList<>();
        }

        // Matriz de Programación Dinámica: dp[i][w]
        // i: items considerados, w: capacidad actual
        int[][] dp = new int[n + 1][capacidad + 1];

        // Llenar la matriz DP
        for (int i = 1; i <= n; i++) {
            Publicacion item = items.get(i - 1);
            int peso = obtenerPeso.apply(item);
            int beneficio = obtenerBeneficio.apply(item);

            for (int w = 0; w <= capacidad; w++) {
                if (peso <= w) {
                    // Máximo entre incluir o no incluir el item
                    dp[i][w] = Math.max(beneficio + dp[i - 1][w - peso], dp[i - 1][w]);
                } else {
                    // No cabe, heredar valor anterior
                    dp[i][w] = dp[i - 1][w];
                }
            }
        }

        // Reconstruir la solución (backtracking)
        List<Integer> indicesSeleccionados = new ArrayList<>();
        int w = capacidad;

        for (int i = n; i > 0; i--) {
            if (w > 0 && dp[i][w] != dp[i - 1][w]) {
                Publicacion item = items.get(i - 1);
                indicesSeleccionados.add(i - 1);
                w -= obtenerPeso.apply(item);  // Reducir capacidad restante
            }
        }

        return indicesSeleccionados;
    }

    /**
     * PROBLEMA 2: Asignación de Publicidad
     *
     * Optimiza la selección de anuncios para un usuario basado en su tiempo máximo.
     * Maximiza el alcance potencial sin exceder el tiempo de exposición del usuario.
     *
     * @param usuario El usuario con su restricción de tiempo
     * @param catalogo Lista de publicaciones que actúan como anuncios
     * @return Resultado con los anuncios asignados
     */
    public static ResultadoAsignacion optimizarParaUsuario(Usuario usuario, List<Publicacion> catalogo) {
        // Usar el método genérico de Knapsack
        List<Integer> indicesSeleccionados = mochila(
            catalogo,
            usuario.getTiempoMaximoExposicion(),
            Publicacion::getDuracion,              // Peso = duración
            Publicacion::getAlcancePotencial        // Beneficio = alcance potencial
        );

        // Construir resultado
        ResultadoAsignacion resultado = new ResultadoAsignacion(usuario.getNombre());
        for (int idx : indicesSeleccionados) {
            resultado.addAnuncio(catalogo.get(idx));
        }

        return resultado;
    }

    /**
     * PROBLEMA 3: Optimización de Portada
     *
     * Selecciona publicaciones para la portada maximizando beneficio (likes + comentarios)
     * sin exceder el espacio disponible.
     *
     * @param publicaciones Lista de publicaciones candidatas
     * @param espacioDisponible Espacio máximo en la portada
     * @return Resultado con publicaciones destacadas
     */
    public static ResultadoPortada optimizarPortada(List<Publicacion> publicaciones, int espacioDisponible) {
        ResultadoPortada resultado = new ResultadoPortada(espacioDisponible);
        int n = publicaciones.size();
        
        // Crear matriz de programación dinámica
        int[][] dp = new int[n + 1][espacioDisponible + 1];
        
        // Llenar la matriz
        for (int i = 1; i <= n; i++) {
            Publicacion pub = publicaciones.get(i - 1);
            int tamaño = pub.getTamaño();
            int beneficio = pub.getLikes() + pub.getComentarios();
            
            for (int w = 0; w <= espacioDisponible; w++) {
                if (tamaño <= w) {
                    dp[i][w] = Math.max(
                        dp[i - 1][w],
                        dp[i - 1][w - tamaño] + beneficio
                    );
                } else {
                    dp[i][w] = dp[i - 1][w];
                }
            }
        }
        
        // Reconstruir la solución
        int w = espacioDisponible;
        for (int i = n; i > 0; i--) {
            if (dp[i][w] != dp[i - 1][w]) {
                Publicacion pub = publicaciones.get(i - 1);
                resultado.addPublicacion(pub);
                w -= pub.getTamaño();
            }
        }
        
        return resultado;
    }
}