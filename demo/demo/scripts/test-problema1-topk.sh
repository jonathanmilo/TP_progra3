#!/bin/bash

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# PROBLEMA 1: Top K Publicaciones Relevantes (MergeSort)
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

BASE_URL="http://localhost:8080"

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
CYAN='\033[0;36m'
NC='\033[0m'

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo -e "${CYAN}     📊 PROBLEMA 1: Top K Publicaciones Relevantes${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo -e "${MAGENTA}Algoritmo:${NC} Merge Sort + Divide y Conquista"
echo -e "${MAGENTA}Objetivo:${NC} Mantener las K publicaciones más relevantes"
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
echo -e "${BLUE}👥 Creando usuarios...${NC}"
declare -a USUARIOS_IDS

USUARIOS_IDS[0]=$(curl -s -X POST "${BASE_URL}/usuarios" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Usuario1","tiempoMaximoExposicion":120}' | jq -r '.id')

USUARIOS_IDS[1]=$(curl -s -X POST "${BASE_URL}/usuarios" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Usuario2","tiempoMaximoExposicion":90}' | jq -r '.id')

USUARIOS_IDS[2]=$(curl -s -X POST "${BASE_URL}/usuarios" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Usuario3","tiempoMaximoExposicion":60}' | jq -r '.id')

echo -e "${GREEN}✅ 3 usuarios creados${NC}"
echo ""

# Crear publicaciones con diferentes niveles de engagement
echo -e "${BLUE}📢 Creando 15 publicaciones con engagement variado...${NC}"
declare -a POSTS

# Publicaciones con ALTO engagement
for i in {1..3}; do
  POSTS[$i]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
    -H "Content-Type: application/json" \
    -d "{\"contenido\":\"Publicación VIRAL #${i}\",\"idCreador\":\"${USUARIOS_IDS[0]}\",\"tipo\":\"VIDEO\",\"costo\":100,\"duracion\":30,\"alcancePotencial\":1000}" \
    | jq -r '.id')
done

# Publicaciones con MEDIO engagement
for i in {4..7}; do
  POSTS[$i]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
    -H "Content-Type: application/json" \
    -d "{\"contenido\":\"Publicación Popular #${i}\",\"idCreador\":\"${USUARIOS_IDS[1]}\",\"tipo\":\"IMAGEN\",\"costo\":80,\"duracion\":20,\"alcancePotencial\":600}" \
    | jq -r '.id')
done

# Publicaciones con BAJO engagement
for i in {8..12}; do
  POSTS[$i]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
    -H "Content-Type: application/json" \
    -d "{\"contenido\":\"Publicación Normal #${i}\",\"idCreador\":\"${USUARIOS_IDS[2]}\",\"tipo\":\"TEXTO\",\"costo\":50,\"duracion\":10,\"alcancePotencial\":300}" \
    | jq -r '.id')
done

# Publicaciones con MUY BAJO engagement
for i in {13..15}; do
  POSTS[$i]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
    -H "Content-Type: application/json" \
    -d "{\"contenido\":\"Publicación Baja #${i}\",\"idCreador\":\"${USUARIOS_IDS[2]}\",\"tipo\":\"TEXTO\",\"costo\":30,\"duracion\":5,\"alcancePotencial\":100}" \
    | jq -r '.id')
done

echo -e "${GREEN}✅ 15 publicaciones creadas${NC}"
echo ""

# Agregar LIKES usando IDs reales de usuarios
echo -e "${BLUE}❤️  Agregando likes con usuarios reales...${NC}"

# VIRAL: 50 likes cada una
for post_idx in {1..3}; do
  for user_idx in {0..2}; do
    for i in {1..17}; do
      curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[$post_idx]}/like?idUsuario=${USUARIOS_IDS[$user_idx]}" > /dev/null 2>&1
    done
  done
done

# POPULAR: 25 likes cada una
for post_idx in {4..7}; do
  for user_idx in {0..2}; do
    for i in {1..8}; do
      curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[$post_idx]}/like?idUsuario=${USUARIOS_IDS[$user_idx]}" > /dev/null 2>&1
    done
  done
done

# NORMAL: 10 likes cada una
for post_idx in {8..12}; do
  for user_idx in {0..1}; do
    for i in {1..5}; do
      curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[$post_idx]}/like?idUsuario=${USUARIOS_IDS[$user_idx]}" > /dev/null 2>&1
    done
  done
done

# BAJO: 3 likes cada una
for post_idx in {13..15}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[$post_idx]}/like?idUsuario=${USUARIOS_IDS[0]}" > /dev/null 2>&1
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[$post_idx]}/like?idUsuario=${USUARIOS_IDS[1]}" > /dev/null 2>&1
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[$post_idx]}/like?idUsuario=${USUARIOS_IDS[2]}" > /dev/null 2>&1
done

echo -e "${GREEN}✅ Likes agregados${NC}"
echo ""

# Agregar COMENTARIOS
echo -e "${BLUE}💬 Agregando comentarios...${NC}"

# VIRAL: 20 comentarios cada una
for post_idx in {1..3}; do
  for user_idx in {0..2}; do
    curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[$post_idx]}/comentario?idUsuario=${USUARIOS_IDS[$user_idx]}&cantidad=7" > /dev/null 2>&1
  done
done

# POPULAR: 8 comentarios cada una
for post_idx in {4..7}; do
  for user_idx in {0..1}; do
    curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[$post_idx]}/comentario?idUsuario=${USUARIOS_IDS[$user_idx]}&cantidad=4" > /dev/null 2>&1
  done
done

# NORMAL: 3 comentarios cada una
for post_idx in {8..12}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[$post_idx]}/comentario?idUsuario=${USUARIOS_IDS[0]}&cantidad=3" > /dev/null 2>&1
done

echo -e "${GREEN}✅ Comentarios agregados${NC}"
echo ""

# Calcular relevancia
echo -e "${BLUE}🔄 Calculando relevancia...${NC}"
curl -s -X POST "${BASE_URL}/publicaciones/relevantes/calcular" > /dev/null
echo -e "${GREEN}✅ Relevancia calculada${NC}"
echo ""

# Obtener Top K
echo -e "${BLUE}🏆 Obteniendo Top K=10 publicaciones...${NC}"
RELEVANTES=$(curl -s "${BASE_URL}/publicaciones/relevantes")
echo ""

echo -e "${CYAN}📊 Top 10 Publicaciones Más Relevantes:${NC}"
echo ""
echo "$RELEVANTES" | jq -r '.[] | "  \(.relevancia | tostring | .[0:5]) - \(.contenido)"'
echo ""

echo -e "${GREEN}✅ PROBLEMA 1 COMPLETADO${NC}"
echo ""
echo -e "${CYAN}🌐 Ver en navegador:${NC}"
echo "   ${BASE_URL}/view/relevantes"
echo ""

