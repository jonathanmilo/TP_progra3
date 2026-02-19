package tp.demo.utils;

import tp.demo.model.Publicacion;
import tp.demo.model.PublicacionRelevante;

import java.util.List;

public class MergeSort {
    
    /**
     * Ordenar por relevancia usando MergeSort
     */
    public List<PublicacionRelevante> MergeSortRelevantes(List<PublicacionRelevante> lista, int inicio, int fin) {
        if (inicio < fin) {
            int medio = (inicio + fin) / 2;
            
            // Ordenar primera mitad
            MergeSortRelevantes(lista, inicio, medio);
            
            // Ordenar segunda mitad
            MergeSortRelevantes(lista, medio + 1, fin);
            
            // Combinar mitades ordenadas
            mergeRelevantes(lista, inicio, medio, fin);
        }
        return lista;
    }
    private void mergeRelevantes(List<PublicacionRelevante> lista, int inicio, int medio, int fin) {
        // Crear arreglos temporales
        int n1 = medio - inicio + 1;
        int n2 = fin - medio;
        
        PublicacionRelevante[] izquierda = new PublicacionRelevante[n1];
        PublicacionRelevante[] derecha = new PublicacionRelevante[n2];
        
        // Copiar datos a arreglos temporales
        for (int i = 0; i < n1; i++) {
            izquierda[i] = lista.get(inicio + i);
        }
        for (int j = 0; j < n2; j++) {
            derecha[j] = lista.get(medio + 1 + j);
        }
        
        // Combinar arreglos temporales
        int i = 0, j = 0;
        int k = inicio;
        
        while (i < n1 && j < n2) {
            if (izquierda[i].getRelevancia() >= derecha[j].getRelevancia()) {
                lista.set(k, izquierda[i]);
                i++;
            } else {
                lista.set(k, derecha[j]);
                j++;
            }
            k++;
        }
        
        // Copiar elementos restantes de izquierda
        while (i < n1) {
            lista.set(k, izquierda[i]);
            i++;
            k++;
        }
        
        // Copiar elementos restantes de derecha
        while (j < n2) {
            lista.set(k, derecha[j]);
            j++;
            k++;
        }
    }

    public List<Publicacion> MergeSortByRelevancia(List<Publicacion> lista, int inicio, int fin) {
        if (inicio < fin) {
            int medio = (inicio + fin) / 2;
            
            // Ordenar primera mitad
            MergeSortByRelevancia(lista, inicio, medio);
            
            // Ordenar segunda mitad
            MergeSortByRelevancia(lista, medio + 1, fin);
            
            // Combinar mitades ordenadas
            merge(lista, inicio, medio, fin);
        }
        return lista;
    }
    
    private void merge(List<Publicacion> lista, int inicio, int medio, int fin) {
        // Crear arreglos temporales
        int n1 = medio - inicio + 1;
        int n2 = fin - medio;
        
        Publicacion[] izquierda = new Publicacion[n1];
        Publicacion[] derecha = new Publicacion[n2];
        
        // Copiar datos a arreglos temporales
        for (int i = 0; i < n1; i++) {
            izquierda[i] = lista.get(inicio + i);
        }
        for (int j = 0; j < n2; j++) {
            derecha[j] = lista.get(medio + 1 + j);
        }
        
        // Combinar arreglos temporales
        int i = 0, j = 0;
        int k = inicio;
        
        while (i < n1 && j < n2) {
            if (izquierda[i].getRelevancia() >= derecha[j].getRelevancia()) {
                lista.set(k, izquierda[i]);
                i++;
            } else {
                lista.set(k, derecha[j]);
                j++;
            }
            k++;
        }
        
        // Copiar elementos restantes de izquierda
        while (i < n1) {
            lista.set(k, izquierda[i]);
            i++;
            k++;
        }
        
        // Copiar elementos restantes de derecha
        while (j < n2) {
            lista.set(k, derecha[j]);
            j++;
            k++;
        }
    }
}