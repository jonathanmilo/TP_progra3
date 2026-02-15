#!/bin/bash

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# PROBLEMA 3: Optimización de Portada (Knapsack DP)
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

BASE_URL="http://localhost:8080"

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
CYAN='\033[0;36m'
MAGENTA='\033[0;35m'
NC='\033[0m'

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo -e "${CYAN}     🏠 PROBLEMA 3: Optimización de Portada${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo -e "${MAGENTA}Algoritmo:${NC} Knapsack 0/1 con Programación Dinámica"
echo -e "${MAGENTA}Objetivo:${NC} Maximizar engagement sin exceder espacio disponible"
echo -e "${MAGENTA}Beneficio:${NC} likes + comentarios por publicación"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# Verificar servidor
echo -e "${BLUE}🔍 Verificando servidor...${NC}"
if ! curl -s "${BASE_URL}/actuator/health" > /dev/null 2>&1; then
  echo -e "${RED}❌ ERROR: El servidor no está corriendo en ${BASE_URL}${NC}"
  exit 1
fi
echo -e "${GREEN}✅ Servidor corriendo${NC}"
echo ""

# Limpiar BD
echo -e "${BLUE}🗑️  Limpiando base de datos...${NC}"
curl -s -X DELETE "${BASE_URL}/publicaciones" > /dev/null
curl -s -X DELETE "${BASE_URL}/usuarios" > /dev/null
echo -e "${GREEN}✅ Base de datos limpia${NC}"
echo ""

# Crear usuarios
echo -e "${BLUE}👥 PASO 1: Creando usuarios...${NC}"

declare -a USUARIOS_IDS

USUARIOS_IDS[0]=$(curl -s -X POST "${BASE_URL}/usuarios" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"@creator1","tiempoMaximoExposicion":120}' | jq -r '.id')

USUARIOS_IDS[1]=$(curl -s -X POST "${BASE_URL}/usuarios" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"@creator2","tiempoMaximoExposicion":90}' | jq -r '.id')

USUARIOS_IDS[2]=$(curl -s -X POST "${BASE_URL}/usuarios" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"@creator3","tiempoMaximoExposicion":60}' | jq -r '.id')

echo -e "${GREEN}✅ 3 usuarios creados${NC}"
echo ""

# Crear publicaciones con diferentes tamaños y engagement
echo -e "${BLUE}📢 PASO 2: Creando publicaciones con engagement variado...${NC}"
echo ""

declare -a POSTS

# VIDEO VIRAL (tamaño=4, alto engagement)
POSTS[0]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{\"contenido\":\"🔥 Tutorial IA Completo 2026\",\"idCreador\":\"${USUARIOS_IDS[0]}\",\"tipo\":\"VIDEO\",\"costo\":200,\"duracion\":30,\"alcancePotencial\":5000}" \
  | jq -r '.id')

# VIDEO POPULAR (tamaño=4, medio-alto engagement)
POSTS[1]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{\"contenido\":\"📹 Demo de Proyecto\",\"idCreador\":\"${USUARIOS_IDS[1]}\",\"tipo\":\"VIDEO\",\"costo\":150,\"duracion\":25,\"alcancePotencial\":3000}" \
  | jq -r '.id')

# IMAGEN POPULAR (tamaño=2, alto engagement)
POSTS[2]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{\"contenido\":\"📸 Setup de Programación\",\"idCreador\":\"${USUARIOS_IDS[0]}\",\"tipo\":\"IMAGEN\",\"costo\":120,\"duracion\":20,\"alcancePotencial\":2500}" \
  | jq -r '.id')

# IMAGEN MEDIA (tamaño=2, medio engagement)
POSTS[3]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{\"contenido\":\"🖼️ Infografía Tech\",\"idCreador\":\"${USUARIOS_IDS[1]}\",\"tipo\":\"IMAGEN\",\"costo\":100,\"duracion\":15,\"alcancePotencial\":2000}" \
  | jq -r '.id')

# TEXTO TRENDING (tamaño=1, alto engagement)
POSTS[4]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{\"contenido\":\"💡 Tips para entrevistas técnicas\",\"idCreador\":\"${USUARIOS_IDS[0]}\",\"tipo\":\"TEXTO\",\"costo\":80,\"duracion\":10,\"alcancePotencial\":1500}" \
  | jq -r '.id')

# TEXTO POPULAR (tamaño=1, medio engagement)
POSTS[5]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{\"contenido\":\"📝 Guía de Python\",\"idCreador\":\"${USUARIOS_IDS[1]}\",\"tipo\":\"TEXTO\",\"costo\":70,\"duracion\":8,\"alcancePotencial\":1200}" \
  | jq -r '.id')

# ENCUESTA POPULAR (tamaño=2, medio engagement)
POSTS[6]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{\"contenido\":\"📊 ¿Qué lenguaje prefieres?\",\"idCreador\":\"${USUARIOS_IDS[2]}\",\"tipo\":\"ENCUESTA\",\"costo\":90,\"duracion\":12,\"alcancePotencial\":1800}" \
  | jq -r '.id')

# TEXTO NORMAL (tamaño=1, bajo engagement)
POSTS[7]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{\"contenido\":\"📄 Artículo sobre Git\",\"idCreador\":\"${USUARIOS_IDS[2]}\",\"tipo\":\"TEXTO\",\"costo\":60,\"duracion\":7,\"alcancePotencial\":800}" \
  | jq -r '.id')

echo -e "${GREEN}✅ 8 publicaciones creadas con diferentes tamaños${NC}"
echo ""

# Agregar LIKES Y COMENTARIOS para generar engagement
echo -e "${BLUE}❤️  PASO 3: Agregando engagement (likes + comentarios)...${NC}"
echo ""

# Post 0 (VIDEO VIRAL): 100 likes, 50 comentarios = 150 engagement
echo -e "${YELLOW}Video Viral: agregando 150 reacciones...${NC}"
for user_id in "${USUARIOS_IDS[@]}"; do
  for i in {1..33}; do
    curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[0]}/like?idUsuario=${user_id}" > /dev/null 2>&1
  done
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[0]}/comentario?idUsuario=${user_id}&cantidad=17" > /dev/null 2>&1
done

# Post 1 (VIDEO POPULAR): 60 likes, 30 comentarios = 90 engagement
echo -e "${YELLOW}Video Popular: agregando 90 reacciones...${NC}"
for user_id in "${USUARIOS_IDS[@]}"; do
  for i in {1..20}; do
    curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[1]}/like?idUsuario=${user_id}" > /dev/null 2>&1
  done
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[1]}/comentario?idUsuario=${user_id}&cantidad=10" > /dev/null 2>&1
done

# Post 2 (IMAGEN POPULAR): 80 likes, 40 comentarios = 120 engagement
echo -e "${YELLOW}Imagen Popular: agregando 120 reacciones...${NC}"
for user_id in "${USUARIOS_IDS[@]}"; do
  for i in {1..27}; do
    curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[2]}/like?idUsuario=${user_id}" > /dev/null 2>&1
  done
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[2]}/comentario?idUsuario=${user_id}&cantidad=13" > /dev/null 2>&1
done

# Post 3 (IMAGEN MEDIA): 50 likes, 20 comentarios = 70 engagement
echo -e "${YELLOW}Imagen Media: agregando 70 reacciones...${NC}"
for user_id in "${USUARIOS_IDS[@]}"; do
  for i in {1..17}; do
    curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[3]}/like?idUsuario=${user_id}" > /dev/null 2>&1
  done
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[3]}/comentario?idUsuario=${user_id}&cantidad=7" > /dev/null 2>&1
done

# Post 4 (TEXTO TRENDING): 70 likes, 30 comentarios = 100 engagement
echo -e "${YELLOW}Texto Trending: agregando 100 reacciones...${NC}"
for user_id in "${USUARIOS_IDS[@]}"; do
  for i in {1..23}; do
    curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[4]}/like?idUsuario=${user_id}" > /dev/null 2>&1
  done
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[4]}/comentario?idUsuario=${user_id}&cantidad=10" > /dev/null 2>&1
done

# Post 5 (TEXTO POPULAR): 40 likes, 15 comentarios = 55 engagement
echo -e "${YELLOW}Texto Popular: agregando 55 reacciones...${NC}"
for user_id in "${USUARIOS_IDS[@]}"; do
  for i in {1..13}; do
    curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[5]}/like?idUsuario=${user_id}" > /dev/null 2>&1
  done
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[5]}/comentario?idUsuario=${user_id}&cantidad=5" > /dev/null 2>&1
done

# Post 6 (ENCUESTA): 45 likes, 20 comentarios = 65 engagement
echo -e "${YELLOW}Encuesta: agregando 65 reacciones...${NC}"
for user_id in "${USUARIOS_IDS[@]}"; do
  for i in {1..15}; do
    curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[6]}/like?idUsuario=${user_id}" > /dev/null 2>&1
  done
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[6]}/comentario?idUsuario=${user_id}&cantidad=7" > /dev/null 2>&1
done

# Post 7 (TEXTO NORMAL): 20 likes, 5 comentarios = 25 engagement
echo -e "${YELLOW}Texto Normal: agregando 25 reacciones...${NC}"
for user_id in "${USUARIOS_IDS[@]}"; do
  for i in {1..7}; do
    curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[7]}/like?idUsuario=${user_id}" > /dev/null 2>&1
  done
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[7]}/comentario?idUsuario=${user_id}&cantidad=2" > /dev/null 2>&1
done

echo ""
echo -e "${GREEN}✅ Engagement agregado a todas las publicaciones${NC}"
echo ""

# Ejecutar optimización de portada
echo -e "${BLUE}🎯 PASO 4: Ejecutando optimización de portada...${NC}"
echo ""

ESPACIO=15
echo -e "${YELLOW}Espacio disponible: ${ESPACIO} unidades${NC}"
echo -e "${YELLOW}Tamaños: VIDEO=4, IMAGEN=2, ENCUESTA=2, TEXTO=1${NC}"
echo ""

RESULTADO=$(curl -s "${BASE_URL}/publicaciones/optimizar-portada?espacioDisponible=${ESPACIO}")

echo -e "${CYAN}📊 Portada Optimizada:${NC}"
echo ""

# Mostrar publicaciones seleccionadas
echo "$RESULTADO" | jq -r '.publicacionesDestacadas[] | "  ✅ \(.contenido)\n     • Tipo: \(.tipo) (tamaño=\(.tamaño))\n     • Engagement: \(.likes) likes + \(.comentarios) comentarios = \(.likes + .comentarios)\n"'

# Calcular métricas
BENEFICIO=$(echo "$RESULTADO" | jq '.beneficioTotal')
ESPACIO_USADO=$(echo "$RESULTADO" | jq '.espacioUsado')
CANTIDAD=$(echo "$RESULTADO" | jq '.publicacionesDestacadas | length')

echo ""
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}✅ PROBLEMA 3 COMPLETADO${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo -e "${CYAN}📈 Resumen:${NC}"
echo "  • Publicaciones en portada: ${CANTIDAD}"
echo "  • Beneficio total (engagement): ${BENEFICIO}"
echo "  • Espacio usado: ${ESPACIO_USADO} / ${ESPACIO} unidades"
echo "  • Eficiencia espacial: $(echo "scale=1; $ESPACIO_USADO * 100 / $ESPACIO" | bc)%"
echo ""
echo -e "${CYAN}🌐 Ver en navegador:${NC}"
echo "   ${BASE_URL}/view/optimizar-portada?espacioDisponible=${ESPACIO}"
echo ""
echo -e "${YELLOW}💡 Observación:${NC} El algoritmo maximiza engagement sin exceder espacio"
echo ""

