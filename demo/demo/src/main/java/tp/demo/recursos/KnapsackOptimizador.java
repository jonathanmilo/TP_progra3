package tp.demo.recursos;

import tp.demo.Publicaciones.Entidad.Publicacion;
import tp.demo.Usuarios.Entidad.Usuario;

import java.util.ArrayList;
import java.util.List;

/**
 * Optimizador que resuelve el Problema de la Mochila
 * para la asignación de publicidad.
 */
public class KnapsackOptimizador {

    /**
     * Clase para estructurar el resultado de la optimización por usuario.
     */
    public static class ResultadoAsignacion {
        private String usuarioNombre;
        private List<Publicacion> anunciosAsignados;
        private int alcanceTotal;
        private int tiempoTotalUsado;
        private int costoEconomicoTotal;

        public ResultadoAsignacion(String usuarioNombre) {
            this.usuarioNombre = usuarioNombre;
            this.anunciosAsignados = new ArrayList<>();
            this.alcanceTotal = 0;
            this.tiempoTotalUsado = 0;
            this.costoEconomicoTotal = 0;
        }

        // Getters para la presentación de resultados
        public String getUsuarioNombre() { return usuarioNombre; }
        public List<Publicacion> getAnunciosAsignados() { return anunciosAsignados; }
        public int getAlcanceTotal() { return alcanceTotal; }
        public int getTiempoTotalUsado() { return tiempoTotalUsado; }
        public int getCostoEconomicoTotal() { return costoEconomicoTotal; }
    }

    /**
     * Optimiza la selección de anuncios para un usuario basado en su tiempo máximo.
     * * @param usuario El usuario con su restricción de tiempo (presupuesto de exposición).
     * @param catalogo La lista de publicaciones disponibles que actúan como anuncios.
     * @return Objeto con los anuncios que maximizan el alcance (relevancia).
     */
    public static ResultadoAsignacion optimizarParaUsuario(Usuario usuario, List<Publicacion> catalogo) {
        int n = catalogo.size();
        int capacidadTiempo = usuario.getTiempoMaximoExposicion();

        // Matriz de Programación Dinámica: dp[i][w]
        // i: anuncios considerados, w: capacidad de tiempo actual
        int[][] dp = new int[n + 1][capacidadTiempo + 1];

        for (int i = 1; i <= n; i++) {
            Publicacion anuncio = catalogo.get(i - 1);
            int duracion = anuncio.getDuracion();
            int beneficio = (int) anuncio.getRelevancia(); // El alcance es la relevancia calculada

            for (int w = 0; w <= capacidadTiempo; w++) {
                if (duracion <= w) {
                    dp[i][w] = Math.max(beneficio + dp[i - 1][w - duracion], dp[i - 1][w]);
                } else {
                    dp[i][w] = dp[i - 1][w];
                }
            }
        }

        ResultadoAsignacion resultado = new ResultadoAsignacion(usuario.getNombre());
        int valorRestante = dp[n][capacidadTiempo];
        int tiempoRestante = capacidadTiempo;

        for (int i = n; i > 0 && valorRestante > 0; i--) {
            // Si el valor en la celda es distinto al de la fila superior, el anuncio i fue incluido
            if (valorRestante != dp[i - 1][tiempoRestante]) {
                Publicacion seleccionado = catalogo.get(i - 1);
                resultado.anunciosAsignados.add(seleccionado);

                resultado.alcanceTotal += (int) seleccionado.getRelevancia();
                resultado.tiempoTotalUsado += seleccionado.getDuracion();
                resultado.costoEconomicoTotal += seleccionado.getCosto();

                valorRestante -= (int) seleccionado.getRelevancia();
                tiempoRestante -= seleccionado.getDuracion();
            }
        }

        return resultado;
    }
}