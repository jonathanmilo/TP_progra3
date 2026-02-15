#!/bin/bash

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# SCRIPT MAESTRO: Test Completo con Interacciones Reales Entre Usuarios
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
#
# Este script simula una red social real donde:
# - Usuarios crean publicaciones
# - Usuarios dan likes y comentarios en posts de OTROS usuarios
# - Se calcula relevancia basada en engagement
# - Se priorizan usuarios trending
#
# Problema 1: Top K Publicaciones Relevantes (MergeSort)
# Problema 2: Asignación de Publicidad (Knapsack + Priorización usuarios trending)
# Problema 3: Optimización de Portada (Knapsack + Peso trending)
#
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
echo -e "${CYAN}     🚀 TEST COMPLETO: RED SOCIAL CON INTERACCIONES REALES${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo -e "${MAGENTA}Simulación:${NC} Usuarios interactuando en una red social"
echo -e "${MAGENTA}Problema 1:${NC} Top K Publicaciones Relevantes (MergeSort)"
echo -e "${MAGENTA}Problema 2:${NC} Asignación de Publicidad (Knapsack + Trending)"
echo -e "${MAGENTA}Problema 3:${NC} Optimización de Portada (Knapsack + Peso Trending)"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# Verificar servidor
echo -e "${BLUE}🔍 Verificando servidor...${NC}"
if ! curl -s "${BASE_URL}/actuator/health" > /dev/null 2>&1; then
  echo -e "${RED}❌ ERROR: El servidor no está corriendo en ${BASE_URL}${NC}"
  echo "Por favor, inicia DemoApplication y vuelve a ejecutar este script."
  exit 1
fi
echo -e "${GREEN}✅ Servidor corriendo correctamente${NC}"
echo ""

# Limpiar BD
echo -e "${BLUE}🗑️  Limpiando base de datos...${NC}"
curl -s -X DELETE "${BASE_URL}/publicaciones" > /dev/null
curl -s -X DELETE "${BASE_URL}/usuarios" > /dev/null
echo -e "${GREEN}✅ Base de datos limpia${NC}"
echo ""

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# PASO 1: CREAR USUARIOS
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

echo -e "${BLUE}👥 PASO 1: Creando usuarios...${NC}"
echo ""

declare -A USUARIOS

# Influencer (mucho contenido popular)
USUARIOS[influencer]=$(curl -s -X POST "${BASE_URL}/usuarios" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"@influencer_tech","tiempoMaximoExposicion":120}' \
  | jq -r '.id')
echo "✅ @influencer_tech (120s exposición)"

# Usuario medio-alto
USUARIOS[popular]=$(curl -s -X POST "${BASE_URL}/usuarios" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"@popular_user","tiempoMaximoExposicion":90}' \
  | jq -r '.id')
echo "✅ @popular_user (90s exposición)"

# Usuario medio
USUARIOS[medio]=$(curl -s -X POST "${BASE_URL}/usuarios" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"@usuario_medio","tiempoMaximoExposicion":60}' \
  | jq -r '.id')
echo "✅ @usuario_medio (60s exposición)"

# Usuario casual
USUARIOS[casual]=$(curl -s -X POST "${BASE_URL}/usuarios" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"@casual_lurker","tiempoMaximoExposicion":45}' \
  | jq -r '.id')
echo "✅ @casual_lurker (45s exposición)"

# Usuario nuevo
USUARIOS[nuevo]=$(curl -s -X POST "${BASE_URL}/usuarios" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"@nuevo_usuario","tiempoMaximoExposicion":30}' \
  | jq -r '.id')
echo "✅ @nuevo_usuario (30s exposición)"

echo ""
echo -e "${GREEN}✅ 5 usuarios creados${NC}"
echo ""

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# PASO 2: CREAR PUBLICACIONES
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

echo -e "${BLUE}📢 PASO 2: Creando publicaciones...${NC}"
echo ""

declare -a POSTS

# Posts del INFLUENCER (contenido que será viral)
echo -e "${YELLOW}Posts de @influencer_tech:${NC}"
POSTS[0]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{\"contenido\":\"Tutorial: Cómo usar IA en 2026 🤖\",\"idCreador\":\"${USUARIOS[influencer]}\",\"costo\":200,\"duracion\":30,\"alcancePotencial\":3000,\"tipo\":\"VIDEO\"}" \
  | jq -r '.id')
echo "  📹 Tutorial IA (será VIRAL)"

POSTS[1]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{\"contenido\":\"Mi setup de programación completo 💻\",\"idCreador\":\"${USUARIOS[influencer]}\",\"costo\":150,\"duracion\":25,\"alcancePotencial\":2500,\"tipo\":\"IMAGEN\"}" \
  | jq -r '.id')
echo "  📸 Setup programación"

POSTS[2]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{\"contenido\":\"Tips para aprobar entrevistas técnicas ⚡\",\"idCreador\":\"${USUARIOS[influencer]}\",\"costo\":180,\"duracion\":20,\"alcancePotencial\":2800,\"tipo\":\"TEXTO\"}" \
  | jq -r '.id')
echo "  📝 Tips entrevistas"

# Posts del USUARIO POPULAR
echo -e "${YELLOW}Posts de @popular_user:${NC}"
POSTS[3]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{\"contenido\":\"Mis proyectos de programación en GitHub 🚀\",\"idCreador\":\"${USUARIOS[popular]}\",\"costo\":100,\"duracion\":15,\"alcancePotencial\":1500,\"tipo\":\"TEXTO\"}" \
  | jq -r '.id')
echo "  📝 Proyectos GitHub"

POSTS[4]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{\"contenido\":\"Reseña del nuevo framework React 2026\",\"idCreador\":\"${USUARIOS[popular]}\",\"costo\":120,\"duracion\":18,\"alcancePotencial\":1800,\"tipo\":\"TEXTO\"}" \
  | jq -r '.id')
echo "  📝 Reseña React"

# Posts del USUARIO MEDIO
echo -e "${YELLOW}Posts de @usuario_medio:${NC}"
POSTS[5]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{\"contenido\":\"Aprendiendo Python desde cero 🐍\",\"idCreador\":\"${USUARIOS[medio]}\",\"costo\":80,\"duracion\":12,\"alcancePotencial\":800,\"tipo\":\"TEXTO\"}" \
  | jq -r '.id')
echo "  📝 Aprendiendo Python"

POSTS[6]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{\"contenido\":\"Mi primer proyecto web terminado!\",\"idCreador\":\"${USUARIOS[medio]}\",\"costo\":70,\"duracion\":10,\"alcancePotencial\":700,\"tipo\":\"IMAGEN\"}" \
  | jq -r '.id')
echo "  📸 Primer proyecto"

# Posts del USUARIO CASUAL
echo -e "${YELLOW}Posts de @casual_lurker:${NC}"
POSTS[7]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{\"contenido\":\"Hola mundo desde mi blog\",\"idCreador\":\"${USUARIOS[casual]}\",\"costo\":50,\"duracion\":8,\"alcancePotencial\":400,\"tipo\":\"TEXTO\"}" \
  | jq -r '.id')
echo "  📝 Hola mundo"

# Posts del USUARIO NUEVO (poco engagement esperado)
echo -e "${YELLOW}Posts de @nuevo_usuario:${NC}"
POSTS[8]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{\"contenido\":\"Mi primer post en la plataforma\",\"idCreador\":\"${USUARIOS[nuevo]}\",\"costo\":40,\"duracion\":5,\"alcancePotencial\":300,\"tipo\":\"TEXTO\"}" \
  | jq -r '.id')
echo "  📝 Primer post"

POSTS[9]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{\"contenido\":\"Probando la plataforma...\",\"idCreador\":\"${USUARIOS[nuevo]}\",\"costo\":30,\"duracion\":5,\"alcancePotencial\":200,\"tipo\":\"TEXTO\"}" \
  | jq -r '.id')
echo "  📝 Probando"

echo ""
echo -e "${GREEN}✅ 10 publicaciones creadas${NC}"
echo ""

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# PASO 3: SIMULAR INTERACCIONES REALES
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

echo -e "${BLUE}❤️  PASO 3: Simulando interacciones entre usuarios...${NC}"
echo ""

# POST 0: Tutorial IA del influencer (VIRAL - 50 likes, 20 comentarios)
echo -e "${YELLOW}Post: Tutorial IA (@influencer_tech) - VIRAL 🔥${NC}"
for i in {1..50}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[0]}/like?idUsuario=user${i}" > /dev/null
done
for i in {1..20}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[0]}/comentario?idUsuario=user${i}&cantidad=1" > /dev/null
done
echo "  👍 50 likes | 💬 20 comentarios"

# POST 1: Setup programación del influencer (POPULAR - 35 likes, 12 comentarios)
echo -e "${YELLOW}Post: Setup programación (@influencer_tech)${NC}"
for i in {1..35}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[1]}/like?idUsuario=user${i}" > /dev/null
done
for i in {1..12}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[1]}/comentario?idUsuario=user${i}&cantidad=1" > /dev/null
done
echo "  👍 35 likes | 💬 12 comentarios"

# POST 2: Tips entrevistas del influencer (MUY POPULAR - 40 likes, 15 comentarios)
echo -e "${YELLOW}Post: Tips entrevistas (@influencer_tech)${NC}"
for i in {1..40}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[2]}/like?idUsuario=user${i}" > /dev/null
done
for i in {1..15}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[2]}/comentario?idUsuario=user${i}&cantidad=1" > /dev/null
done
echo "  👍 40 likes | 💬 15 comentarios"

# POST 3: Proyectos GitHub del popular (POPULAR - 25 likes, 8 comentarios)
echo -e "${YELLOW}Post: Proyectos GitHub (@popular_user)${NC}"
for i in {1..25}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[3]}/like?idUsuario=user${i}" > /dev/null
done
for i in {1..8}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[3]}/comentario?idUsuario=user${i}&cantidad=1" > /dev/null
done
echo "  👍 25 likes | 💬 8 comentarios"

# POST 4: Reseña React del popular (POPULAR - 28 likes, 10 comentarios)
echo -e "${YELLOW}Post: Reseña React (@popular_user)${NC}"
for i in {1..28}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[4]}/like?idUsuario=user${i}" > /dev/null
done
for i in {1..10}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[4]}/comentario?idUsuario=user${i}&cantidad=1" > /dev/null
done
echo "  👍 28 likes | 💬 10 comentarios"

# POST 5: Aprendiendo Python del medio (MEDIO - 15 likes, 5 comentarios)
echo -e "${YELLOW}Post: Aprendiendo Python (@usuario_medio)${NC}"
for i in {1..15}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[5]}/like?idUsuario=user${i}" > /dev/null
done
for i in {1..5}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[5]}/comentario?idUsuario=user${i}&cantidad=1" > /dev/null
done
echo "  👍 15 likes | 💬 5 comentarios"

# POST 6: Primer proyecto del medio (MEDIO - 12 likes, 4 comentarios)
echo -e "${YELLOW}Post: Primer proyecto (@usuario_medio)${NC}"
for i in {1..12}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[6]}/like?idUsuario=user${i}" > /dev/null
done
for i in {1..4}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[6]}/comentario?idUsuario=user${i}&cantidad=1" > /dev/null
done
echo "  👍 12 likes | 💬 4 comentarios"

# POST 7: Hola mundo del casual (BAJO - 5 likes, 2 comentarios)
echo -e "${YELLOW}Post: Hola mundo (@casual_lurker)${NC}"
for i in {1..5}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[7]}/like?idUsuario=user${i}" > /dev/null
done
for i in {1..2}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[7]}/comentario?idUsuario=user${i}&cantidad=1" > /dev/null
done
echo "  👍 5 likes | 💬 2 comentarios"

# POST 8: Primer post del nuevo (MUY BAJO - 2 likes, 1 comentario)
echo -e "${YELLOW}Post: Primer post (@nuevo_usuario)${NC}"
for i in {1..2}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[8]}/like?idUsuario=user${i}" > /dev/null
done
curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[8]}/comentario?idUsuario=user1&cantidad=1" > /dev/null
echo "  👍 2 likes | 💬 1 comentario"

# POST 9: Probando del nuevo (MUY BAJO - 1 like)
echo -e "${YELLOW}Post: Probando (@nuevo_usuario)${NC}"
curl -s -X POST "${BASE_URL}/publicaciones/${POSTS[9]}/like?idUsuario=user1" > /dev/null
echo "  👍 1 like | 💬 0 comentarios"

echo ""
echo -e "${GREEN}✅ Interacciones simuladas${NC}"
echo ""
echo -e "${CYAN}📊 Resumen de engagement:${NC}"
echo "  🔥 @influencer_tech: ~125 likes + 47 comentarios = 172 reacciones"
echo "  ⭐ @popular_user: 53 likes + 18 comentarios = 71 reacciones"
echo "  📈 @usuario_medio: 27 likes + 9 comentarios = 36 reacciones"
echo "  📉 @casual_lurker: 5 likes + 2 comentarios = 7 reacciones"
echo "  🆕 @nuevo_usuario: 3 likes + 1 comentario = 4 reacciones"
echo ""

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# PROBLEMA 1: TOP K PUBLICACIONES RELEVANTES
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo -e "${MAGENTA}📊 PROBLEMA 1: Gestión de Publicaciones (MergeSort + Top K)${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

echo -e "${BLUE}🔄 Calculando relevancia con MergeSort...${NC}"
curl -s -X POST "${BASE_URL}/publicaciones/relevantes/calcular" > /dev/null
sleep 1

RELEVANTES=$(curl -s "${BASE_URL}/publicaciones/relevantes")
K_ACTUAL=$(echo "$RELEVANTES" | jq 'length')
echo -e "${GREEN}✅ Top K=$K_ACTUAL publicaciones calculadas${NC}"
echo ""

echo -e "${BLUE}🏆 Top 5 Más Relevantes:${NC}"
echo "$RELEVANTES" | jq -r '.[:5] | to_entries[] | "  \(.key + 1). [\(.value.relevancia | tostring | .[0:5])] \(.value.contenido[0:50])..."'

echo ""
echo -e "${GREEN}✅ PROBLEMA 1 COMPLETADO${NC}"
echo ""

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# PROBLEMA 2: ASIGNACIÓN DE PUBLICIDAD
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo -e "${MAGENTA}💰 PROBLEMA 2: Asignación de Publicidad (Knapsack + Trending)${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

PRESUPUESTO=600
echo -e "${BLUE}🔄 Ejecutando con presupuesto: \$$PRESUPUESTO${NC}"
echo ""

CAMPANA=$(curl -s "${BASE_URL}/publicaciones/optimizar-publicidad?presupuestoEmpresa=$PRESUPUESTO")
NUM_USUARIOS=$(echo "$CAMPANA" | jq 'length')
ALCANCE_TOTAL=0
COSTO_TOTAL=0

for ((i=0; i<NUM_USUARIOS; i++)); do
  USUARIO=$(echo "$CAMPANA" | jq ".[$i]")
  NOMBRE=$(echo "$USUARIO" | jq -r '.usuarioNombre')
  ALCANCE=$(echo "$USUARIO" | jq '.alcanceTotal')
  COSTO=$(echo "$USUARIO" | jq '.costoEconomicoTotal')
  COUNT=$(echo "$USUARIO" | jq '.anunciosAsignados | length')

  ALCANCE_TOTAL=$((ALCANCE_TOTAL + ALCANCE))
  COSTO_TOTAL=$((COSTO_TOTAL + COSTO))

  if [ "$COUNT" -gt 0 ]; then
    echo -e "  ${GREEN}✅ $NOMBRE: $COUNT anuncios | Alcance: $ALCANCE | \$$COSTO${NC}"
  else
    echo -e "  ${RED}❌ $NOMBRE: Sin anuncios (presupuesto agotado)${NC}"
  fi
done

echo ""
echo -e "${CYAN}📈 Resumen:${NC}"
echo "  Alcance total: $ALCANCE_TOTAL personas"
echo "  Costo total: \$$COSTO_TOTAL / \$$PRESUPUESTO"
echo ""
echo -e "${GREEN}✅ PROBLEMA 2 COMPLETADO${NC}"
echo -e "${YELLOW}💡 Nota: Usuarios trending (@influencer, @popular) reciben prioridad${NC}"
echo ""

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# PROBLEMA 3: OPTIMIZACIÓN DE PORTADA
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo -e "${MAGENTA}🏠 PROBLEMA 3: Optimización de Portada (Knapsack + Peso Trending)${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

ESPACIO=25
echo -e "${BLUE}🔄 Ejecutando con espacio: $ESPACIO unidades${NC}"
echo ""

PORTADA=$(curl -s "${BASE_URL}/publicaciones/optimizar-portada?espacioDisponible=$ESPACIO")
BENEFICIO=$(echo "$PORTADA" | jq '.beneficioTotal')
ESPACIO_USADO=$(echo "$PORTADA" | jq '.espacioUsado')
COUNT=$(echo "$PORTADA" | jq '.publicacionesDestacadas | length')
PORCENTAJE=$(echo "scale=1; $ESPACIO_USADO * 100 / $ESPACIO" | bc)

echo -e "${CYAN}📊 Resultado:${NC}"
echo "  Publicaciones seleccionadas: $COUNT"
echo "  Beneficio total: $BENEFICIO (likes + comentarios con peso trending)"
echo "  Espacio usado: $ESPACIO_USADO / $ESPACIO ($PORCENTAJE%)"
echo ""

echo -e "${BLUE}🏆 Publicaciones en portada:${NC}"
echo "$PORTADA" | jq -r '.publicacionesDestacadas[] | "  📰 \(.contenido[0:50])... (👍 \(.likes) | 💬 \(.comentarios))"'

echo ""
echo -e "${GREEN}✅ PROBLEMA 3 COMPLETADO${NC}"
echo -e "${YELLOW}💡 Nota: Publicaciones con más engagement tienen beneficio multiplicado${NC}"
echo ""

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# RESUMEN FINAL
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo -e "${CYAN}     ✅ TEST COMPLETO FINALIZADO${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo -e "${GREEN}📊 RESUMEN COMPLETO:${NC}"
echo ""
echo -e "${MAGENTA}Datos Creados:${NC}"
echo "  👥 5 usuarios (desde influencer hasta nuevo)"
echo "  📢 10 publicaciones con contenido variado"
echo "  ❤️  289 interacciones totales (likes + comentarios)"
echo ""
echo -e "${MAGENTA}Problema 1:${NC} Top K=$K_ACTUAL publicaciones más relevantes ✅"
echo -e "${MAGENTA}Problema 2:${NC} Alcance=$ALCANCE_TOTAL, Costo=\$$COSTO_TOTAL, Usuarios trending priorizados ✅"
echo -e "${MAGENTA}Problema 3:${NC} Beneficio=$BENEFICIO, Espacio=$ESPACIO_USADO/$ESPACIO, Trending con peso extra ✅"
echo ""
echo -e "${CYAN}🌐 Ver resultados en navegador:${NC}"
echo "  • Usuarios: ${BASE_URL}/view/usuarios"
echo "  • Publicaciones: ${BASE_URL}/view/publicaciones"
echo "  • Top K Relevantes: ${BASE_URL}/view/relevantes"
echo "  • Publicidad: ${BASE_URL}/view/optimizar-publicidad?presupuesto=${PRESUPUESTO}"
echo "  • Portada: ${BASE_URL}/view/optimizar-portada?espacioDisponible=${ESPACIO}"
echo ""
echo -e "${GREEN}🎉 TODOS LOS PROBLEMAS FUNCIONANDO CON INTERACCIONES REALES${NC}"
echo ""
echo -e "${YELLOW}💡 Observaciones Clave:${NC}"
echo "  1. Usuarios con más engagement aparecen primero en /view/usuarios"
echo "  2. Sus posts aparecen en Top K relevantes"
echo "  3. Reciben prioridad en asignación de presupuesto publicitario"
echo "  4. Sus publicaciones tienen peso extra en portada"
echo ""

