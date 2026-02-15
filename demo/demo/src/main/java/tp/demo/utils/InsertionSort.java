package tp.demo.utils;

import tp.demo.model.Usuario;

import java.util.List;

public class InsertionSort {
    /**
     * Ordena usuarios por relevancia de forma DESCENDENTE segun su relevancia en sus posteos.
     */
    public static void insertionSortUsuariosDesc(List<Usuario> usuarios) {
        int n = usuarios.size();
        for (int i = 1; i < n; ++i) {
            Usuario llave = usuarios.get(i);
            int j = i - 1;

        /* Mueve los elementos que son MENORES que la llave
           una posición adelante para dejar el espacio (Orden Descendente) */
            while (j >= 0 && usuarios.get(j).getRelevanciaEnPosteos() < llave.getRelevanciaEnPosteos()) {
                usuarios.set(j + 1, usuarios.get(j));
                j = j - 1;
            }
            usuarios.set(j + 1, llave);
        }
    }
}
