# Trabajo Integrador - Programación III

## Integrantes
- **Julieta Puig Peralta**
- **Jonathan Mayan**

---

##  Tabla de Contenidos
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Cómo Ejecutar](#-cómo-ejecutar)
  - [Opción 1: Ejemplos Locales (Sin BD/Servidor)](#opción-1-ejemplos-locales-sin-bdservidor)
  - [Opción 2: Aplicación Completa con UI](#opción-2-aplicación-completa-con-ui)
- [Tecnologías](#-tecnologías)

---

##  Estructura del Proyecto

```
src/main/java/tp/demo/
├── DemoApplication.java          # ← Clase principal (main)
├── examples/                     # ← Ejemplos ejecutables locales
│   ├── EjemploProblema1.java    # Problema 1: Top K (MergeSort)
│   ├── EjemploProblema2.java    # Problema 2: Publicidad (Knapsack)
│   └── EjemploProblema3.java    # Problema 3: Portada (Knapsack)
├── controller/                   # REST API y vistas web
├── model/                        # Entidades (Usuario, Publicacion, etc.)
├── service/                      # Lógica de negocio
├── repository/                   # MongoDB repositories
└── utils/                        # Algoritmos
    ├── MergeSort.java           # Ordenamiento O(n log n)
    └── KnapsackOptimizador.java # Knapsack 0/1 con DP

src/main/resources/templates/view/
├── index.html                    # Página principal
├── relevantes.html               # Vista Problema 1
├── optimizar-publicidad.html    # Vista Problema 2
└── optimizar-portada.html        # Vista Problema 3
```

---

## Cómo Ejecutar

### **Opción 1: Ejemplos Locales (Sin BD/Servidor)**

 Ejecución rápida sin dependencias externas

#### Abrir en IntelliJ y ejecutar:

** Problema 1: Top K Publicaciones Relevantes**
```
src/main/java/tp/demo/examples/EjemploProblema1.java
```
- Click derecho → **Run 'EjemploProblema1.main()'**
- **Algoritmo:** MergeSort + Divide y Conquista
- **Complejidad:** O(n log n)
- Crea 10 publicaciones, ordena por relevancia, muestra Top 5

** Problema 2: Asignación de Publicidad**
```
src/main/java/tp/demo/examples/EjemploProblema2.java
```
- Click derecho → **Run 'EjemploProblema2.main()'**
- **Algoritmo:** Knapsack 0/1 con Programación Dinámica
- **Complejidad:** O(u log u + u × n × C)
- Crea 4 usuarios y 6 anuncios, optimiza asignación con presupuesto $600

** Problema 2 Opcional: Optimización de Portada**
```
src/main/java/tp/demo/examples/EjemploProblema3.java
```
- Click derecho → **Run 'EjemploProblema3.main()'**
- **Algoritmo:** Knapsack 0/1 con Programación Dinámica
- **Complejidad:** O(n × E)
- Crea 8 publicaciones, optimiza selección con 3 límites (10, 15, 20 unidades)

---

### **Opción 2: Aplicación Completa con UI**

Requiere MongoDB y Spring Boot

#### **Paso 1: Iniciar la aplicación**

**En IntelliJ:**
1. Abrir `src/main/java/tp/demo/DemoApplication.java`
2. Click derecho → **Run 'DemoApplication.main()'**
3. Esperar: `Started DemoApplication in X seconds`

**En Terminal:**
```bash
cd /Users/puki/Desktop/tp_progra3/demo/demo
./mvnw spring-boot:run
```

#### **Paso 2: Acceder a la interfaz**

 **Página Principal**  
http://localhost:8080

 **Swagger API (opcional)**  
http://localhost:8080/swagger-ui/index.html

#### **Paso 3: Ejecutar scripts de prueba (opcional)**

** Primero dar permisos de ejecución:**
```bash
chmod +x *.sh
```

**Luego ejecutar los scripts:**
```bash
# Crear datos de prueba
./test-problema1-topk.sh          # Top K Relevantes
./test-problema2-publicidad.sh    # Asignación de Publicidad
./test-problema3-portada.sh       # Optimización de Portada

# Limpiar base de datos
./limpiar-bd-completo.sh
```

#### **Paso 4: Ver resultados en el navegador**

| Vista | URL | Descripción |
|-------|-----|-------------|
| **Problema 1** | http://localhost:8080/view/relevantes?k=10 | Top K publicaciones más relevantes |
| **Problema 2** | http://localhost:8080/view/optimizar-publicidad?presupuesto=2000 | Asignación óptima de anuncios |
| **Problema 3** | http://localhost:8080/view/optimizar-portada?espacioDisponible=20 | Selección óptima para portada |
| **Usuarios** | http://localhost:8080/view/usuarios | Lista de usuarios |
| **Publicaciones** | http://localhost:8080/view/publicaciones | Lista de publicaciones |

---

## Tecnologías

- **Backend:** Spring Boot 3.2.2, Java 17
- **Base de Datos:** MongoDB
- **Frontend:** Thymeleaf, HTML, CSS
- **API Docs:** Swagger/OpenAPI 3
- **Algoritmos:** MergeSort (Divide y Conquista), Knapsack 0/1 (Programación Dinámica)
