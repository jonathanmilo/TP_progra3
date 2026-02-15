package tp.demo.examples;

import tp.demo.model.Publicacion;
import tp.demo.model.ResultadoPortada;
import tp.demo.model.TipoPublicacion;
import tp.demo.utils.KnapsackOptimizador;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * EJEMPLO EJECUTABLE LOCAL - PROBLEMA 3
 * Optimización de Portada usando Knapsack 0/1 con Programación Dinámica
 *
 * No requiere MongoDB ni servidor HTTP.
 * Ejecutar: Click derecho → Run 'EjemploProblema3.main()'
 */
public class EjemploProblema3 {

    public static void main(String[] args) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("   🏠 PROBLEMA 3: Optimización de Portada");
        System.out.println("   Algoritmo: Knapsack 0/1 con Programación Dinámica");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println();

        // ═══════════════════════════════════════════════════════════════
        // PASO 1: Crear publicaciones con diferentes características
        // ═══════════════════════════════════════════════════════════════

        List<Publicacion> publicaciones = new ArrayList<>();
        Date ahora = new Date();

        // VIDEO VIRAL (tamaño 4, alto engagement)
        publicaciones.add(crearPublicacion(
            "🔥 Tutorial IA Completo 2026",
            TipoPublicacion.VIDEO,
            100, 50, ahora
        ));

        // VIDEO POPULAR (tamaño 4, medio-alto engagement)
        publicaciones.add(crearPublicacion(
            "📹 Demo de Proyecto Fullstack",
            TipoPublicacion.VIDEO,
            60, 30, ahora
        ));

        // IMAGEN POPULAR (tamaño 2, alto engagement)
        publicaciones.add(crearPublicacion(
            "📸 Setup de Programación Profesional",
            TipoPublicacion.IMAGEN,
            80, 40, ahora
        ));

        // IMAGEN MEDIA (tamaño 2, medio engagement)
        publicaciones.add(crearPublicacion(
            "🖼️ Infografía Tech Stack 2026",
            TipoPublicacion.IMAGEN,
            50, 20, ahora
        ));

        // TEXTO TRENDING (tamaño 1, alto engagement)
        publicaciones.add(crearPublicacion(
            "💡 10 Tips para Entrevistas Técnicas",
            TipoPublicacion.TEXTO,
            70, 30, ahora
        ));

        // TEXTO POPULAR (tamaño 1, medio engagement)
        publicaciones.add(crearPublicacion(
            "📝 Guía Rápida de Python",
            TipoPublicacion.TEXTO,
            40, 15, ahora
        ));

        // ENCUESTA POPULAR (tamaño 2, medio engagement)
        publicaciones.add(crearPublicacion(
            "📊 ¿Qué framework prefieres?",
            TipoPublicacion.ENCUESTA,
            45, 20, ahora
        ));

        // TEXTO NORMAL (tamaño 1, bajo engagement)
        publicaciones.add(crearPublicacion(
            "📄 Introducción a Git",
            TipoPublicacion.TEXTO,
            20, 5, ahora
        ));

        System.out.println("📢 Publicaciones candidatas: " + publicaciones.size());
        System.out.println();
        System.out.println("┌─────┬──────────────────────────────┬────────┬──────────┬──────────┬────────────┐");
        System.out.println("│ #   │ Contenido                    │ Tipo   │ Tamaño   │ Likes    │ Comentarios│");
        System.out.println("├─────┼──────────────────────────────┼────────┼──────────┼──────────┼────────────┤");

        for (int i = 0; i < publicaciones.size(); i++) {
            Publicacion p = publicaciones.get(i);
            System.out.printf("│ %-3d │ %-28s │ %-6s │    %d     │   %3d    │     %3d    │%n",
                i + 1,
                truncar(p.getContenido(), 28),
                p.tipo.toString().substring(0, Math.min(6, p.tipo.toString().length())),
                p.getTamaño(),
                p.getLikes(),
                p.getComentarios()
            );
        }
        System.out.println("└─────┴──────────────────────────────┴────────┴──────────┴──────────┴────────────┘");
        System.out.println();

        // ═══════════════════════════════════════════════════════════════
        // PASO 2: Calcular beneficio (engagement) de cada publicación
        // ═══════════════════════════════════════════════════════════════

        // Calcular suma total de tamaños
        int sumaTotalTamanos = 0;
        int sumaTotalEngagement = 0;
        for (Publicacion p : publicaciones) {
            sumaTotalTamanos += p.getTamaño();
            sumaTotalEngagement += p.getLikes() + p.getComentarios();
        }

        System.out.println("📊 Análisis de Eficiencia (Beneficio/Tamaño):");
        System.out.println("─────────────────────────────────────────────────────────────────");
        for (Publicacion p : publicaciones) {
            int beneficio = p.getLikes() + p.getComentarios();
            double ratio = (double) beneficio / p.getTamaño();
            System.out.printf("  • %s: %d / %d = %.1f%n",
                truncar(p.getContenido(), 35),
                beneficio,
                p.getTamaño(),
                ratio
            );
        }
        System.out.println();
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📦 RESUMEN TOTAL:");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.printf("  • Total publicaciones: %d%n", publicaciones.size());
        System.out.printf("  • Suma de TODOS los tamaños: %d unidades%n", sumaTotalTamanos);
        System.out.printf("  • Suma de TODO el engagement: %d%n", sumaTotalEngagement);
        System.out.println();
        System.out.println("⚠️  Si el límite de portada < " + sumaTotalTamanos + ", NO cabrán todas las publicaciones");
        System.out.println();

        // ═══════════════════════════════════════════════════════════════
        // PASO 3: Ejecutar Knapsack con diferentes espacios
        // ═══════════════════════════════════════════════════════════════

        // ⚙️ CONFIGURACIÓN: Límites de portada a probar
        // Modifica estos valores para probar con diferentes límites de espacio
        //
        // ┌─────────────────────────────────────────────────────────────┐
        // │ LÍMITE DE PORTADA = Espacio máximo disponible en la portada │
        // │                                                               │
        // │ Ejemplos:                                                     │
        // │  • 10 unidades = Portada pequeña                             │
        // │  • 15 unidades = Portada mediana                             │
        // │  • 20 unidades = Portada grande                              │
        // │  • 17 unidades = Justo para TODAS las publicaciones          │
        // └─────────────────────────────────────────────────────────────┘
        //
        int[] espacios = {10, 15, 20};  // ← LÍMITES DE PORTADA: 10, 15 y 20 unidades

        for (int espacio : espacios) {
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("🎯 OPTIMIZACIÓN DE PORTADA");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println();
            System.out.println("╔═══════════════════════════════════════════════════════╗");
            System.out.printf("║  📏 LÍMITE DE PORTADA: %d unidades                    ║%n", espacio);
            System.out.println("╚═══════════════════════════════════════════════════════╝");
            System.out.println();
            System.out.printf("📦 Publicaciones disponibles: %d (suma total de tamaños: %d unidades)%n",
                publicaciones.size(), sumaTotalTamanos);
            System.out.println();

            if (sumaTotalTamanos <= espacio) {
                System.out.println("✅ Resultado: Todas las publicaciones CABEN en la portada");
                System.out.printf("   (Se usarán %d de %d unidades disponibles = %.1f%%)%n",
                    sumaTotalTamanos, espacio, (sumaTotalTamanos * 100.0 / espacio));
            } else {
                System.out.printf("⚠️  Resultado: NO caben todas las publicaciones%n");
                System.out.printf("   Necesitarías %d unidades pero solo tienes %d (faltan %d)%n",
                    sumaTotalTamanos, espacio, sumaTotalTamanos - espacio);
                System.out.println("   → El algoritmo seleccionará las que maximicen engagement total");
            }
            System.out.println();

            // Ejecutar algoritmo Knapsack con el límite actual
            // 'espacio' = LÍMITE DE PORTADA (capacidad máxima disponible)
            long inicio = System.nanoTime();
            ResultadoPortada resultado = KnapsackOptimizador.optimizarPortada(publicaciones, espacio);
            long fin = System.nanoTime();

            System.out.println("✅ Publicaciones seleccionadas: " + resultado.getPublicacionesDestacadas().size());
            System.out.println();

            int numeroPublicacion = 1;
            int sumaEspacioReal = 0;
            for (Publicacion p : resultado.getPublicacionesDestacadas()) {
                int engagement = p.getLikes() + p.getComentarios();
                sumaEspacioReal += p.getTamaño();
                System.out.printf("  %d. %s%n", numeroPublicacion++, p.getContenido());
                System.out.printf("     Tipo: %-8s | Tamaño: %d | Engagement: %d (👍%d + 💬%d)%n",
                    p.tipo, p.getTamaño(), engagement, p.getLikes(), p.getComentarios());
                System.out.println();
            }

            // Verificar qué publicaciones NO fueron seleccionadas
            System.out.println("❌ Publicaciones NO seleccionadas:");
            for (int i = 0; i < publicaciones.size(); i++) {
                Publicacion p = publicaciones.get(i);
                boolean seleccionada = false;
                for (Publicacion sel : resultado.getPublicacionesDestacadas()) {
                    if (sel.getContenido().equals(p.getContenido())) {
                        seleccionada = true;
                        break;
                    }
                }
                if (!seleccionada) {
                    int engagement = p.getLikes() + p.getComentarios();
                    System.out.printf("  • %s (Tamaño: %d, Engagement: %d)%n",
                        truncar(p.getContenido(), 30), p.getTamaño(), engagement);
                }
            }
            System.out.println();

            System.out.println("🔍 Verificación:");
            System.out.println("  • Suma real de tamaños: " + sumaEspacioReal);
            System.out.println("  • Espacio restante: " + (espacio - sumaEspacioReal));

            double eficiencia = (resultado.getEspacioUsado() * 100.0) / espacio;

            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("📈 RESUMEN DE RESULTADOS:");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("  • LÍMITE DE PORTADA: " + espacio + " unidades");
            System.out.println("  • Espacio usado: " + resultado.getEspacioUsado() + " unidades");
            System.out.println("  • Espacio restante: " + (espacio - sumaEspacioReal) + " unidades");
            System.out.println("  • Eficiencia espacial: " + String.format("%.1f", eficiencia) + "%");
            System.out.println();
            System.out.println("  • Beneficio total (engagement): " + resultado.getBeneficioTotal());
            System.out.println("  • Tiempo de cálculo: " + (fin - inicio) / 1000 + " μs");
            System.out.println();
        }

        // ═══════════════════════════════════════════════════════════════
        // PASO 4: Análisis de complejidad
        // ═══════════════════════════════════════════════════════════════

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("    - E = espacio disponible (variable: 10, 15, 20)");
        System.out.println("✅ Respeta restricción de espacio estrictamente");
        // ═══════════════════════════════════════════════════════════════
        // PASO 5: Comparación con estrategia greedy
        // ═══════════════════════════════════════════════════════════════

    }



    /**
     * Método auxiliar para crear publicaciones
     */
    private static Publicacion crearPublicacion(String contenido,
                                                TipoPublicacion tipo,
                                                int likes,
                                                int comentarios,
                                                Date fecha) {
        Publicacion p = new Publicacion();
        p.setContenido(contenido);
        p.setIdCreador("usuario_test");
        p.setFechaCreacion(fecha);
        p.setTipo(tipo);

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

    /**
     * Método auxiliar para truncar strings
     */
    private static String truncar(String str, int longitud) {
        if (str.length() <= longitud) {
            return str;
        }
        return str.substring(0, longitud - 3) + "...";
    }
}

