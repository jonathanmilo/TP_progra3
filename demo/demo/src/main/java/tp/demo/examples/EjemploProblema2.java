package tp.demo.examples;

import tp.demo.model.Publicacion;
import tp.demo.model.ResultadoAsignacion;
import tp.demo.model.TipoPublicacion;
import tp.demo.model.Usuario;
import tp.demo.utils.KnapsackOptimizador;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * EJEMPLO EJECUTABLE LOCAL - PROBLEMA 2
 * Asignación de Publicidad usando Knapsack 0/1 con Programación Dinámica
 *
 * No requiere MongoDB ni servidor HTTP.
 * Ejecutar: Click derecho → Run 'EjemploProblema2.main()'
 */
public class EjemploProblema2 {

    public static void main(String[] args) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("   💰 PROBLEMA 2: Asignación de Publicidad");
        System.out.println("   Algoritmo: Knapsack 0/1 con Programación Dinámica");
        System.out.println("   Complejidad: O(u log u + u × n × C)");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println();

        // ═══════════════════════════════════════════════════════════════
        // PASO 1: Crear usuarios con diferentes características
        // ═══════════════════════════════════════════════════════════════

        List<Usuario> usuarios = new ArrayList<>();

        Usuario influencer = new Usuario();
        influencer.setNombre("@influencer_tech");
        influencer.setTiempoMaximoExposicion(120); // 2 minutos
        influencer.setRelevanciaEnPosteos(250);     // Alta relevancia (trending)
        usuarios.add(influencer);

        Usuario popular = new Usuario();
        popular.setNombre("@popular_user");
        popular.setTiempoMaximoExposicion(90);      // 1.5 minutos
        popular.setRelevanciaEnPosteos(120);        // Media relevancia
        usuarios.add(popular);

        Usuario medio = new Usuario();
        medio.setNombre("@usuario_medio");
        medio.setTiempoMaximoExposicion(60);        // 1 minuto
        medio.setRelevanciaEnPosteos(50);           // Baja relevancia
        usuarios.add(medio);

        Usuario casual = new Usuario();
        casual.setNombre("@casual_user");
        casual.setTiempoMaximoExposicion(30);       // 30 segundos
        casual.setRelevanciaEnPosteos(10);          // Muy baja relevancia
        usuarios.add(casual);

        System.out.println("👥 Usuarios creados: " + usuarios.size());
        for (Usuario u : usuarios) {
            System.out.printf("  • %s (Tiempo: %ds, Relevancia: %d)%n",
                u.getNombre(), u.getTiempoMaximoExposicion(), u.getRelevanciaEnPosteos());
        }
        System.out.println();

        // ═══════════════════════════════════════════════════════════════
        // PASO 2: Crear catálogo de anuncios
        // ═══════════════════════════════════════════════════════════════

        List<Publicacion> anuncios = new ArrayList<>();

        anuncios.add(crearAnuncio("Oferta Smartphones 📱", 200, 30, 8000));
        anuncios.add(crearAnuncio("Viajes a Cancún ✈️", 250, 40, 10000));
        anuncios.add(crearAnuncio("Ropa Primavera 👕", 150, 25, 6000));
        anuncios.add(crearAnuncio("Restaurante 🍽️", 80, 15, 3000));
        anuncios.add(crearAnuncio("Concierto 🎸", 180, 35, 7000));
        anuncios.add(crearAnuncio("App Nueva 📲", 100, 20, 4000));

        System.out.println("📢 Anuncios disponibles: " + anuncios.size());
        for (Publicacion a : anuncios) {
            System.out.printf("  • %s ($%d, %ds, alcance: %d personas)%n",
                a.getContenido(), a.getCosto(), a.getDuracion(), a.getAlcancePotencial());
        }
        System.out.println();

        // ═══════════════════════════════════════════════════════════════
        // PASO 3: Ordenar usuarios por relevancia (trending primero)
        // ═══════════════════════════════════════════════════════════════

        System.out.println("🔄 Ordenando usuarios por relevancia (trending primero)...");
        usuarios.sort(Comparator.comparingInt(Usuario::getRelevanciaEnPosteos).reversed());
        System.out.println("✅ Usuarios ordenados");
        System.out.println();

        // ═══════════════════════════════════════════════════════════════
        // PASO 4: Ejecutar Knapsack para cada usuario
        // ═══════════════════════════════════════════════════════════════

        int presupuestoTotal = 600;
        int presupuestoRestante = presupuestoTotal;
        List<ResultadoAsignacion> resultados = new ArrayList<>();

        System.out.println("💰 Presupuesto total: $" + presupuestoTotal);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println();

        long inicioTotal = System.nanoTime();

        for (Usuario usuario : usuarios) {
            System.out.println("👤 Usuario: " + usuario.getNombre());
            System.out.println("   Relevancia: " + usuario.getRelevanciaEnPosteos());
            System.out.println("   Tiempo disponible: " + usuario.getTiempoMaximoExposicion() + "s");

            // Ejecutar Knapsack para este usuario
            long inicio = System.nanoTime();
            ResultadoAsignacion resultado = KnapsackOptimizador.optimizarParaUsuario(usuario, anuncios);
            long fin = System.nanoTime();

            int costoUsuario = resultado.getCostoEconomicoTotal();

            if (costoUsuario <= presupuestoRestante && costoUsuario > 0) {
                resultados.add(resultado);
                presupuestoRestante -= costoUsuario;

                System.out.println("   ✅ Anuncios asignados: " + resultado.getAnunciosAsignados().size());
                System.out.println("   💰 Costo: $" + costoUsuario);
                System.out.println("   📊 Alcance: " + resultado.getAlcanceTotal() + " personas");
                System.out.println("   ⏱️ Tiempo usado: " + resultado.getTiempoTotalUsado() + "s");
                System.out.println("   🕐 Tiempo de cálculo: " + (fin - inicio) / 1000 + " μs");
                System.out.println("   💵 Presupuesto restante: $" + presupuestoRestante);
            } else {
                System.out.println("   ❌ Sin presupuesto suficiente (necesita $" + costoUsuario + ")");
            }
            System.out.println();
        }

        long finTotal = System.nanoTime();

        // ═══════════════════════════════════════════════════════════════
        // PASO 5: Resumen de resultados
        // ═══════════════════════════════════════════════════════════════

        int alcanceTotal = resultados.stream().mapToInt(ResultadoAsignacion::getAlcanceTotal).sum();
        int costoTotal = presupuestoTotal - presupuestoRestante;
        int usuariosAlcanzados = resultados.size();

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 RESUMEN FINAL:");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  • Usuarios alcanzados: " + usuariosAlcanzados + " de " + usuarios.size());
        System.out.println("  • Alcance total: " + alcanceTotal + " personas");
        System.out.println("  • Costo total: $" + costoTotal + " / $" + presupuestoTotal);
        System.out.println("  • Eficiencia presupuestaria: " + (costoTotal * 100 / presupuestoTotal) + "%");
        System.out.println("  • Tiempo total de cálculo: " + (finTotal - inicioTotal) / 1000 + " μs");
        System.out.println();

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("✅ Estrategia: Priorizar usuarios trending asi generan mas impacto en las publicaciones");
        System.out.println("✅ Optimalidad: Garantizada por usuario individual");
        System.out.println();
    }

    /**
     * Método auxiliar para crear anuncios
     */
    private static Publicacion crearAnuncio(String contenido, int costo, int duracion, int alcance) {
        Publicacion p = new Publicacion();
        p.setContenido(contenido);
        p.setIdCreador("empresa");
        p.setFechaCreacion(new Date());
        p.setCosto(costo);
        p.setDuracion(duracion);
        p.setAlcancePotencial(alcance);
        p.setTipo(TipoPublicacion.VIDEO);
        return p;
    }
}

