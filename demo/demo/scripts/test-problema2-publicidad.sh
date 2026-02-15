#!/bin/bash

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# PROBLEMA 2: Asignación de Publicidad (Knapsack DP)
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
echo -e "${CYAN}     💰 PROBLEMA 2: Asignación de Publicidad${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo -e "${MAGENTA}Algoritmo:${NC} Knapsack 0/1 con Programación Dinámica"
echo -e "${MAGENTA}Objetivo:${NC} Maximizar alcance sin exceder tiempo y presupuesto"
echo -e "${MAGENTA}Priorización:${NC} Usuarios trending primero"
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

# Crear usuarios con diferentes tiempos de exposición
echo -e "${BLUE}👥 PASO 1: Creando usuarios...${NC}"
echo ""

declare -A USUARIOS

USUARIOS[influencer]=$(curl -s -X POST "${BASE_URL}/usuarios" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"@influencer_tech","tiempoMaximoExposicion":120}' \
  | jq -r '.id')
echo "✅ @influencer_tech (120s exposición)"

USUARIOS[popular]=$(curl -s -X POST "${BASE_URL}/usuarios" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"@popular_user","tiempoMaximoExposicion":90}' \
  | jq -r '.id')
echo "✅ @popular_user (90s exposición)"

USUARIOS[medio]=$(curl -s -X POST "${BASE_URL}/usuarios" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"@usuario_medio","tiempoMaximoExposicion":60}' \
  | jq -r '.id')
echo "✅ @usuario_medio (60s exposición)"

USUARIOS[casual]=$(curl -s -X POST "${BASE_URL}/usuarios" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"@casual_user","tiempoMaximoExposicion":30}' \
  | jq -r '.id')
echo "✅ @casual_user (30s exposición)"

echo ""
echo -e "${GREEN}✅ 4 usuarios creados${NC}"
echo ""

# Crear publicaciones del INFLUENCER (contenido viral)
echo -e "${BLUE}📢 PASO 2: Creando publicaciones...${NC}"
echo ""

declare -a POSTS

echo -e "${YELLOW}Publicaciones de @influencer_tech (trending):${NC}"
POSTS[0]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{\"contenido\":\"Tutorial IA 2026\",\"idCreador\":\"${USUARIOS[influencer]}\",\"costo\":200,\"duracion\":30,\"alcancePotencial\":5000,\"tipo\":\"VIDEO\"}" \
  | jq -r '.id')

POSTS[1]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{\"contenido\":\"Setup completo\",\"idCreador\":\"${USUARIOS[influencer]}\",\"costo\":150,\"duracion\":25,\"alcancePotencial\":3500,\"tipo\":\"IMAGEN\"}" \
  | jq -r '.id')

POSTS[2]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{\"contenido\":\"Tips entrevistas\",\"idCreador\":\"${USUARIOS[influencer]}\",\"costo\":180,\"duracion\":20,\"alcancePotencial\":4000,\"tipo\":\"TEXTO\"}" \
  | jq -r '.id')

echo "  ✅ 3 publicaciones creadas"

echo -e "${YELLOW}Publicaciones de @popular_user:${NC}"
POSTS[3]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{\"contenido\":\"Proyectos GitHub\",\"idCreador\":\"${USUARIOS[popular]}\",\"costo\":100,\"duracion\":15,\"alcancePotencial\":2000,\"tipo\":\"TEXTO\"}" \
  | jq -r '.id')

POSTS[4]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{\"contenido\":\"Reseña React\",\"idCreador\":\"${USUARIOS[popular]}\",\"costo\":120,\"duracion\":18,\"alcancePotencial\":2500,\"tipo\":\"TEXTO\"}" \
  | jq -r '.id')

echo "  ✅ 2 publicaciones creadas"

echo -e "${YELLOW}Publicaciones de @usuario_medio:${NC}"
POSTS[5]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{\"contenido\":\"Aprendiendo Python\",\"idCreador\":\"${USUARIOS[medio]}\",\"costo\":80,\"duracion\":12,\"alcancePotencial\":1000,\"tipo\":\"TEXTO\"}" \
  | jq -r '.id')

echo "  ✅ 1 publicación creada"

echo -e "${YELLOW}Anuncios de empresa:${NC}"
POSTS[6]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d '{"contenido":"Oferta Smartphones","idCreador":"empresa123","costo":200,"duracion":30,"alcancePotencial":8000,"tipo":"VIDEO"}' \
  | jq -r '.id')

POSTS[7]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d '{"contenido":"Viajes Cancún","idCreador":"empresa123","costo":250,"duracion":40,"alcancePotencial":10000,"tipo":"VIDEO"}' \
  | jq -r '.id')

POSTS[8]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d '{"contenido":"Ropa Primavera","idCreador":"empresa123","costo":150,"duracion":25,"alcancePotencial":6000,"tipo":"IMAGEN"}' \
  | jq -r '.id')

POSTS[9]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d '{"contenido":"Restaurante","idCreador":"empresa123","costo":80,"duracion":15,"alcancePotencial":3000,"tipo":"TEXTO"}' \
  | jq -r '.id')

echo "  ✅ 4 anuncios creados"

echo ""
echo -e "${GREEN}✅ 10 publicaciones totales${NC}"
echo ""

# Agregar engagement a publicaciones de usuarios (para calcular relevancia)
echo -e "${BLUE}❤️  PASO 3: Agregando engagement a publicaciones de usuarios...${NC}"

# Array de usuarios para hacer likes
USUARIOS_ARRAY=("${USUARIOS[influencer]}" "${USUARIOS[popular]}" "${USUARIOS[medio]}" "${USUARIOS[casual]}")

# Publicación 0 (influencer): 50 likes, 20 comentarios
for user_id in "${USUARIOS_ARRAY[@]}"; do
  for i in {1..12}; do
    curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[0]}/like?idUsuario=${user_id}" > /dev/null 2>&1
  done
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[0]}/comentario?idUsuario=${user_id}&cantidad=5" > /dev/null 2>&1
done

# Publicación 1 (influencer): 35 likes, 12 comentarios
for user_id in "${USUARIOS_ARRAY[@]}"; do
  for i in {1..9}; do
    curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[1]}/like?idUsuario=${user_id}" > /dev/null 2>&1
  done
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[1]}/comentario?idUsuario=${user_id}&cantidad=3" > /dev/null 2>&1
done

# Publicación 2 (influencer): 40 likes, 15 comentarios
for user_id in "${USUARIOS_ARRAY[@]}"; do
  for i in {1..10}; do
    curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[2]}/like?idUsuario=${user_id}" > /dev/null 2>&1
  done
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[2]}/comentario?idUsuario=${user_id}&cantidad=4" > /dev/null 2>&1
done

# Publicación 3 (popular): 25 likes, 8 comentarios
for user_id in "${USUARIOS_ARRAY[@]}"; do
  for i in {1..6}; do
    curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[3]}/like?idUsuario=${user_id}" > /dev/null 2>&1
  done
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[3]}/comentario?idUsuario=${user_id}&cantidad=2" > /dev/null 2>&1
done

# Publicación 4 (popular): 28 likes, 10 comentarios
for user_id in "${USUARIOS_ARRAY[@]}"; do
  for i in {1..7}; do
    curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[4]}/like?idUsuario=${user_id}" > /dev/null 2>&1
  done
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[4]}/comentario?idUsuario=${user_id}&cantidad=2" > /dev/null 2>&1
done

# Publicación 5 (medio): 10 likes, 3 comentarios
for user_id in "${USUARIOS_ARRAY[@]}"; do
  for i in {1..2}; do
    curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[5]}/like?idUsuario=${user_id}" > /dev/null 2>&1
  done
done
curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[5]}/comentario?idUsuario=${USUARIOS[influencer]}&cantidad=3" > /dev/null 2>&1

echo -e "${GREEN}✅ Engagement agregado${NC}"
echo ""

# Ejecutar optimización de publicidad
echo -e "${BLUE}🎯 PASO 4: Ejecutando optimización de publicidad...${NC}"
echo ""

PRESUPUESTO=800
echo -e "${YELLOW}Presupuesto: \$${PRESUPUESTO}${NC}"
echo ""

RESULTADO=$(curl -s "${BASE_URL}/publicaciones/optimizar-publicidad?presupuestoEmpresa=${PRESUPUESTO}")

echo -e "${CYAN}📊 Resultados:${NC}"
echo ""

# Procesar y mostrar resultados
echo "$RESULTADO" | jq -r '.[] | select(.costoEconomicoTotal > 0) | "  ✅ \(.usuarioNombre)\n     • Anuncios: \(.anunciosAsignados | length)\n     • Alcance: \(.alcanceTotal) personas\n     • Costo: $\(.costoEconomicoTotal)\n     • Tiempo: \(.tiempoTotalUsado)s\n"'

# Calcular totales
ALCANCE_TOTAL=$(echo "$RESULTADO" | jq '[.[] | .alcanceTotal] | add')
COSTO_TOTAL=$(echo "$RESULTADO" | jq '[.[] | .costoEconomicoTotal] | add')
USUARIOS_ALCANZADOS=$(echo "$RESULTADO" | jq '[.[] | select(.costoEconomicoTotal > 0)] | length')

echo ""
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}✅ PROBLEMA 2 COMPLETADO${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo -e "${CYAN}📈 Resumen:${NC}"
echo "  • Usuarios alcanzados: ${USUARIOS_ALCANZADOS}"
echo "  • Alcance total: ${ALCANCE_TOTAL} personas"
echo "  • Costo total: \$${COSTO_TOTAL} / \$${PRESUPUESTO}"
echo "  • Eficiencia: $(echo "scale=1; $COSTO_TOTAL * 100 / $PRESUPUESTO" | bc)%"
echo ""
echo -e "${CYAN}🌐 Ver en navegador:${NC}"
echo "   ${BASE_URL}/view/optimizar-publicidad?presupuesto=${PRESUPUESTO}"
echo ""
echo -e "${YELLOW}💡 Observación:${NC} Usuarios con mayor relevancia reciben anuncios primero"
echo ""

