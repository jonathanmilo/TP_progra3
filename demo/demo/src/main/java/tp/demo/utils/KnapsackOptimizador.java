package tp.demo.utils;

import tp.demo.model.Publicacion;
import tp.demo.model.Usuario;

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

        // IMPORTANTE: alcanceTotal NO es cantidad de usuarios únicos
        // Es la SUMA de alcances potenciales (impresiones) de todos los anuncios
        // Ejemplo: Si un anuncio alcanza 800 personas y otro 1500,
        //          alcanceTotal = 2300 impresiones potenciales (pueden repetirse personas)
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
        
        public void addAnuncio(Publicacion anuncio) {
            this.anunciosAsignados.add(anuncio);
            this.alcanceTotal += anuncio.getAlcancePotencial(); // ← USAR ALCANCE POTENCIAL
            this.tiempoTotalUsado += anuncio.getDuracion();
            this.costoEconomicoTotal += anuncio.getCosto();
        }
    }

    /**
     * Optimiza la selección de anuncios para un usuario basado en su tiempo máximo.
     * PROBLEMA 2: Asignación de Publicidad
     *
     * @param usuario El usuario con su restricción de tiempo (presupuesto de exposición).
     * @param catalogo La lista de publicaciones disponibles que actúan como anuncios.
     * @return Objeto con los anuncios que maximizan el alcance potencial.
     */
    public static ResultadoAsignacion optimizarParaUsuario(Usuario usuario, List<Publicacion> catalogo) {
        int n = catalogo.size();
        int capacidadTiempo = usuario.getTiempoMaximoExposicion();

        // Matriz de Programación Dinámica: dp[i][w]
        // i: anuncios considerados, w: capacidad de tiempo actual
        int[][] dp = new int[n + 1][capacidadTiempo + 1];

        // Llenar la matriz DP
        for (int i = 1; i <= n; i++) {
            Publicacion anuncio = catalogo.get(i - 1);
            int duracion = anuncio.getDuracion();
            int beneficio = anuncio.getAlcancePotencial(); // ← USAR ALCANCE POTENCIAL

            for (int w = 0; w <= capacidadTiempo; w++) {
                if (duracion <= w) {
                    dp[i][w] = Math.max(beneficio + dp[i - 1][w - duracion], dp[i - 1][w]);
                } else {
                    dp[i][w] = dp[i - 1][w];
                }
            }
        }

        // Reconstruir la solución (backtracking)
        ResultadoAsignacion resultado = new ResultadoAsignacion(usuario.getNombre());
        int valorRestante = dp[n][capacidadTiempo];
        int tiempoRestante = capacidadTiempo;

        for (int i = n; i > 0 && valorRestante > 0; i--) {
            // Si el valor en la celda es distinto al de la fila superior, el anuncio i fue incluido
            if (valorRestante != dp[i - 1][tiempoRestante]) {
                Publicacion seleccionado = catalogo.get(i - 1);
                resultado.addAnuncio(seleccionado);

                valorRestante -= seleccionado.getAlcancePotencial(); // ← USAR ALCANCE POTENCIAL
                tiempoRestante -= seleccionado.getDuracion();
            }
        }

        return resultado;
    }

    /**
     * Clase para resultado de optimización de portada
     * PROBLEMA 3: Optimización de Interacciones en la Portada
     */
    public static class ResultadoPortada {
        private List<Publicacion> publicacionesDestacadas;
        private int beneficioTotal; // Suma de (likes + comentarios)
        private int espacioUsado;

        public ResultadoPortada() {
            this.publicacionesDestacadas = new ArrayList<>();
            this.beneficioTotal = 0;
            this.espacioUsado = 0;
        }

        public void addPublicacion(Publicacion pub) {
            this.publicacionesDestacadas.add(pub);
            this.beneficioTotal += (pub.getLikes() + pub.getComentarios());
            this.espacioUsado += pub.getTamaño();
        }

        public List<Publicacion> getPublicacionesDestacadas() { return publicacionesDestacadas; }
        public int getBeneficioTotal() { return beneficioTotal; }
        public int getEspacioUsado() { return espacioUsado; }
    }

    /**
     * Optimiza la selección de publicaciones para la portada.
     * PROBLEMA 3: Optimización de Interacciones en la Portada
     *
     * Objetivo: Maximizar beneficio (likes + comentarios) sin exceder espacio disponible
     *
     * @param publicaciones Lista de todas las publicaciones candidatas
     * @param espacioDisponible Espacio máximo en la portada
     * @return Resultado con publicaciones seleccionadas que maximizan el beneficio
     */
    public static ResultadoPortada optimizarPortada(List<Publicacion> publicaciones, int espacioDisponible) {
        int n = publicaciones.size();

        if (n == 0 || espacioDisponible <= 0) {
            return new ResultadoPortada();
        }

        // Matriz de Programación Dinámica: dp[i][w]
        // i: publicaciones consideradas, w: espacio disponible actual
        int[][] dp = new int[n + 1][espacioDisponible + 1];

        // Llenar la matriz DP
        for (int i = 1; i <= n; i++) {
            Publicacion pub = publicaciones.get(i - 1);
            int tamaño = pub.getTamaño();
            int beneficio = pub.getLikes() + pub.getComentarios(); // Beneficio = interacciones

            for (int w = 0; w <= espacioDisponible; w++) {
                if (tamaño <= w) {
                    // Tomar el máximo entre incluir o no incluir la publicación
                    dp[i][w] = Math.max(beneficio + dp[i - 1][w - tamaño], dp[i - 1][w]);
                } else {
                    // No cabe, heredar el valor anterior
                    dp[i][w] = dp[i - 1][w];
                }
            }
        }

        // Reconstruir la solución (backtracking)
        ResultadoPortada resultado = new ResultadoPortada();
        int valorRestante = dp[n][espacioDisponible];
        int espacioRestante = espacioDisponible;

        for (int i = n; i > 0 && valorRestante > 0; i--) {
            // Si el valor cambió, la publicación i fue incluida
            if (valorRestante != dp[i - 1][espacioRestante]) {
                Publicacion seleccionada = publicaciones.get(i - 1);
                resultado.addPublicacion(seleccionada);

                valorRestante -= (seleccionada.getLikes() + seleccionada.getComentarios());
                espacioRestante -= seleccionada.getTamaño();
            }
        }

        return resultado;
    }
}