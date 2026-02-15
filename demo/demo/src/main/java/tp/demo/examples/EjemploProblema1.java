package tp.demo.examples;

import tp.demo.model.Publicacion;
import tp.demo.model.TipoPublicacion;
import tp.demo.utils.MergeSort;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * EJEMPLO EJECUTABLE LOCAL - PROBLEMA 1
 * Top K Publicaciones Relevantes usando MergeSort
 *
 * No requiere MongoDB ni servidor HTTP.
 * Ejecutar: Click derecho → Run 'EjemploProblema1.main()'
 */
public class EjemploProblema1 {

    public static void main(String[] args) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("   📊 PROBLEMA 1: Top K Publicaciones Relevantes");
        System.out.println("   Algoritmo: Merge Sort + Divide y Conquista");
        System.out.println("   Complejidad: O(n log n)");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println();

        // ═══════════════════════════════════════════════════════════════
        // PASO 1: Crear publicaciones con diferentes niveles de engagement
        // ═══════════════════════════════════════════════════════════════

        List<Publicacion> publicaciones = new ArrayList<>();
        Date ahora = new Date();
        Date hace3Dias = new Date(ahora.getTime() - 3 * 24 * 60 * 60 * 1000);
        Date hace7Dias = new Date(ahora.getTime() - 7 * 24 * 60 * 60 * 1000);
        Date hace14Dias = new Date(ahora.getTime() - 14 * 24 * 60 * 60 * 1000);

        // Publicaciones VIRALES (recientes, mucho engagement)
        publicaciones.add(crearPublicacion("Tutorial IA 2026 🔥", 100, 50, ahora));
        publicaciones.add(crearPublicacion("Tips para entrevistas técnicas", 80, 40, hace3Dias));
        publicaciones.add(crearPublicacion("Setup de programación completo", 90, 35, hace3Dias));

        // Publicaciones POPULARES (medio engagement)
        publicaciones.add(crearPublicacion("Proyectos en GitHub", 50, 25, hace7Dias));
        publicaciones.add(crearPublicacion("Reseña de React 2026", 45, 20, hace7Dias));
        publicaciones.add(crearPublicacion("Guía de Python para principiantes", 40, 15, hace7Dias));

        // Publicaciones NORMALES (bajo engagement)
        publicaciones.add(crearPublicacion("Aprendiendo Git", 20, 10, hace14Dias));
        publicaciones.add(crearPublicacion("Mi primer proyecto web", 15, 8, hace14Dias));
        publicaciones.add(crearPublicacion("Configuración de VS Code", 10, 5, hace14Dias));

        // Publicaciones ANTIGUAS (engagement decaído)
        publicaciones.add(crearPublicacion("Post antiguo 1", 30, 10, hace14Dias));

        System.out.println("📢 Publicaciones creadas: " + publicaciones.size());
        System.out.println();

        // ═══════════════════════════════════════════════════════════════
        // PASO 2: Calcular relevancia de cada publicación
        // ═══════════════════════════════════════════════════════════════

        System.out.println("📊 Publicaciones ANTES de ordenar:");
        System.out.println("─────────────────────────────────────────────────────────");
        for (int i = 0; i < publicaciones.size(); i++) {
            Publicacion p = publicaciones.get(i);
            int relevancia = p.getRelevancia();
            System.out.printf("  %2d. [Rel:%3d] %s (Likes:%d, Com:%d)%n",
                i + 1, relevancia, p.getContenido(), p.getLikes(), p.getComentarios());
        }
        System.out.println();

        // ═══════════════════════════════════════════════════════════════
        // PASO 3: Ordenar con MergeSort
        // ═══════════════════════════════════════════════════════════════

        System.out.println("🔄 Ejecutando MergeSort...");
        MergeSort mergeSort = new MergeSort();

        long inicio = System.nanoTime();
        List<Publicacion> ordenadas = mergeSort.MergeSortByRelevancia(
            publicaciones, 0, publicaciones.size() - 1
        );
        long fin = System.nanoTime();

        System.out.println("✅ Ordenamiento completado en " + (fin - inicio) / 1000 + " μs");
        System.out.println();

        // ═══════════════════════════════════════════════════════════════
        // PASO 4: Obtener Top K
        // ═══════════════════════════════════════════════════════════════

        int K = 5;
        System.out.println("🏆 TOP " + K + " Publicaciones Más Relevantes:");
        System.out.println("─────────────────────────────────────────────────────────");

        for (int i = 0; i < Math.min(K, ordenadas.size()); i++) {
            Publicacion p = ordenadas.get(i);
            int relevancia = p.getRelevancia();
            System.out.printf("  #%d. [Relevancia: %3d] %s%n",
                i + 1, relevancia, p.getContenido());
            System.out.printf("       👍 %d likes | 💬 %d comentarios%n",
                p.getLikes(), p.getComentarios());
            System.out.println();
        }

        // ═══════════════════════════════════════════════════════════════
        // PASO 5: Análisis de complejidad
        // ═══════════════════════════════════════════════════════════════

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📈 Análisis de Complejidad:");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  • Algoritmo: Merge Sort (Divide y Conquista)");
        System.out.println("✅ El algoritmo garantiza ordenamiento estable y predecible.");
        System.out.println();
    }

    /**
     * Método auxiliar para crear publicaciones con engagement
     */
    private static Publicacion crearPublicacion(String contenido, int likes, int comentarios, Date fecha) {
        Publicacion p = new Publicacion();
        p.setContenido(contenido);
        p.setIdCreador("usuario_test");
        p.setFechaCreacion(fecha);
        p.setTipo(TipoPublicacion.TEXTO);

        // Agregar likes (simulando múltiples usuarios)
        for (int i = 0; i < likes; i++) {
            p.agregarLike("user_" + i);
        }

        // Agregar comentarios (simulando múltiples usuarios)
        for (int i = 0; i < comentarios; i++) {
            p.agregarComentarios("user_comment_" + i, 1);
        }

        return p;
    }
}

