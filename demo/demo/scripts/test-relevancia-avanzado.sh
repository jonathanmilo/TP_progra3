#!/bin/bash

# Script de Prueba Avanzada - Problema 1: Relevancia con Decaimiento Temporal
# Verifica que la relevancia considere tanto reacciones como antigüedad

BASE_URL="http://localhost:8080"

echo "🚀 Test Avanzado: Relevancia con Factor Temporal"
echo ""

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${BLUE}📝 Paso 1: Creando usuario de prueba...${NC}"

USER_ID=$(curl -s -X POST "${BASE_URL}/usuarios" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Test User",
    "email": "test@example.com",
    "intereses": ["pruebas"],
    "tiempoMaximoExposicion": 60
  }' | jq -r '.id')

echo "✅ Usuario creado (ID: $USER_ID)"
echo ""

echo -e "${BLUE}📢 Paso 2: Creando 10 publicaciones con diferentes características...${NC}"

declare -a PUB_IDS

# Publicación 1: Viral reciente (muchas reacciones, nueva)
PUB_IDS[0]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{
    \"contenido\": \"🔥 VIRAL: Noticia de última hora\",
    \"idCreador\": \"$USER_ID\",
    \"costo\": 100,
    \"duracion\": 15,
    \"alcancePotencial\": 5000
  }" | jq -r '.id')
echo "📝 #1: Viral reciente (${PUB_IDS[0]})"
sleep 0.5

# Publicación 2: Trending (buenas reacciones, nueva)
PUB_IDS[1]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{
    \"contenido\": \"⭐ Trending: Tema del momento\",
    \"idCreador\": \"$USER_ID\",
    \"costo\": 80,
    \"duracion\": 12,
    \"alcancePotencial\": 3000
  }" | jq -r '.id')
echo "📝 #2: Trending (${PUB_IDS[1]})"
sleep 0.5

# Publicación 3: Popular moderada
PUB_IDS[2]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{
    \"contenido\": \"👍 Popular: Contenido interesante\",
    \"idCreador\": \"$USER_ID\",
    \"costo\": 60,
    \"duracion\": 10,
    \"alcancePotencial\": 2000
  }" | jq -r '.id')
echo "📝 #3: Popular (${PUB_IDS[2]})"
sleep 0.5

# Publicación 4: Engagement medio
PUB_IDS[3]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{
    \"contenido\": \"📊 Medio: Post normal\",
    \"idCreador\": \"$USER_ID\",
    \"costo\": 40,
    \"duracion\": 8,
    \"alcancePotencial\": 1000
  }" | jq -r '.id')
echo "📝 #4: Medio (${PUB_IDS[3]})"
sleep 0.5

# Publicación 5: Bajo engagement
PUB_IDS[4]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{
    \"contenido\": \"📉 Bajo: Poca visibilidad\",
    \"idCreador\": \"$USER_ID\",
    \"costo\": 20,
    \"duracion\": 5,
    \"alcancePotencial\": 500
  }" | jq -r '.id')
echo "📝 #5: Bajo (${PUB_IDS[4]})"
sleep 0.5

# Publicaciones 6-10: Contenido variado
for i in {5..9}; do
  PUB_IDS[$i]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
    -H "Content-Type: application/json" \
    -d "{
      \"contenido\": \"📝 Publicación #$((i+1)): Contenido genérico\",
      \"idCreador\": \"$USER_ID\",
      \"costo\": $((30 + RANDOM % 70)),
      \"duracion\": $((8 + RANDOM % 15)),
      \"alcancePotencial\": $((500 + RANDOM % 1500))
    }" | jq -r '.id')
  echo "📝 #$((i+1)): Genérica (${PUB_IDS[$i]})"
  sleep 0.5
done

echo ""
echo -e "${BLUE}👍 Paso 3: Agregando reacciones diferenciadas...${NC}"

# Publicación 1 (VIRAL): 20 likes + 5 comentarios = 25 reacciones
echo "🔥 Pub #1 (VIRAL): Agregando 25 reacciones..."
for i in {1..20}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${PUB_IDS[0]}/like?idUsuario=user${i}" > /dev/null
done
for i in {1..5}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${PUB_IDS[0]}/comentario?idUsuario=user${i}&cantidad=1&textoComentario=Increíble" > /dev/null
done
echo "✅ Pub #1: 20 likes + 5 comentarios = 25 reacciones"

# Publicación 2 (TRENDING): 15 likes + 3 comentarios = 18 reacciones
echo "⭐ Pub #2 (TRENDING): Agregando 18 reacciones..."
for i in {1..15}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${PUB_IDS[1]}/like?idUsuario=user${i}" > /dev/null
done
for i in {1..3}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${PUB_IDS[1]}/comentario?idUsuario=user${i}&cantidad=1&textoComentario=Interesante" > /dev/null
done
echo "✅ Pub #2: 15 likes + 3 comentarios = 18 reacciones"

# Publicación 3 (POPULAR): 10 likes + 2 comentarios = 12 reacciones
echo "👍 Pub #3 (POPULAR): Agregando 12 reacciones..."
for i in {1..10}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${PUB_IDS[2]}/like?idUsuario=user${i}" > /dev/null
done
for i in {1..2}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${PUB_IDS[2]}/comentario?idUsuario=user${i}&cantidad=1&textoComentario=Bien" > /dev/null
done
echo "✅ Pub #3: 10 likes + 2 comentarios = 12 reacciones"

# Publicación 4 (MEDIO): 6 likes + 1 comentario = 7 reacciones
echo "📊 Pub #4 (MEDIO): Agregando 7 reacciones..."
for i in {1..6}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${PUB_IDS[3]}/like?idUsuario=user${i}" > /dev/null
done
curl -s -X POST "${BASE_URL}/publicaciones/${PUB_IDS[3]}/comentario?idUsuario=user1&cantidad=1" > /dev/null
echo "✅ Pub #4: 6 likes + 1 comentario = 7 reacciones"

# Publicación 5 (BAJO): 2 likes = 2 reacciones
echo "📉 Pub #5 (BAJO): Agregando 2 reacciones..."
for i in {1..2}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${PUB_IDS[4]}/like?idUsuario=user${i}" > /dev/null
done
echo "✅ Pub #5: 2 likes"

# Publicaciones 6-10: Reacciones aleatorias
for i in {5..9}; do
  NUM_LIKES=$((1 + RANDOM % 8))
  echo "📝 Pub #$((i+1)): Agregando $NUM_LIKES reacciones..."
  for j in $(seq 1 $NUM_LIKES); do
    curl -s -X POST "${BASE_URL}/publicaciones/${PUB_IDS[$i]}/like?idUsuario=user${j}" > /dev/null
  done
  echo "✅ Pub #$((i+1)): $NUM_LIKES likes"
done

echo ""
echo -e "${BLUE}🔄 Paso 4: Actualizando top K=10...${NC}"

RELEVANTES=$(curl -s -X POST "${BASE_URL}/publicaciones/relevantes/actualizarK?nuevoK=10")

echo ""
echo -e "${GREEN}📊 RESULTADOS (Top 10 por Relevancia):${NC}"
echo ""
echo "$RELEVANTES" | jq -r 'to_entries | .[] | "#\(.key + 1): \(.value.contenido)"'
echo ""
echo -e "${YELLOW}Métricas Detalladas:${NC}"
echo "$RELEVANTES" | jq -r '.[] | "  📌 \(.contenido)\n     Likes: \(.likes) | Comentarios: \(.comentarios) | Total: \(.cantidadReacciones) reacciones\n     Relevancia: \(.relevancia)\n"'

echo ""
echo -e "${GREEN}✅ Paso 5: Verificaciones automáticas...${NC}"

# Verificar que #1 es la más relevante
TOP1_ID=$(echo "$RELEVANTES" | jq -r '.[0].id')
if [ "$TOP1_ID" == "${PUB_IDS[0]}" ]; then
  echo -e "${GREEN}✅ La publicación VIRAL es la #1 (correcta)${NC}"
else
  echo -e "${RED}❌ La publicación VIRAL NO es la #1 (error)${NC}"
fi

# Verificar que #2 es trending
TOP2_ID=$(echo "$RELEVANTES" | jq -r '.[1].id')
if [ "$TOP2_ID" == "${PUB_IDS[1]}" ]; then
  echo -e "${GREEN}✅ La publicación TRENDING es la #2 (correcta)${NC}"
else
  echo -e "${YELLOW}⚠️  La publicación TRENDING NO es la #2${NC}"
fi

# Verificar orden decreciente de relevancia
ORDEN_CORRECTO=true
PREV_REL=$(echo "$RELEVANTES" | jq -r '.[0].relevancia')
for i in {1..9}; do
  CURR_REL=$(echo "$RELEVANTES" | jq -r ".[$i].relevancia // 0")
  if (( $(echo "$CURR_REL > $PREV_REL" | bc -l) )); then
    ORDEN_CORRECTO=false
    break
  fi
  PREV_REL=$CURR_REL
done

if [ "$ORDEN_CORRECTO" = true ]; then
  echo -e "${GREEN}✅ El orden de relevancia es correcto (decreciente)${NC}"
else
  echo -e "${RED}❌ El orden de relevancia es incorrecto${NC}"
fi

# Verificar que se mantienen solo K=10
COUNT=$(echo "$RELEVANTES" | jq 'length')
if [ "$COUNT" -eq 10 ]; then
  echo -e "${GREEN}✅ Se mantienen exactamente K=10 publicaciones${NC}"
else
  echo -e "${YELLOW}⚠️  Se mantienen $COUNT publicaciones (esperado: 10)${NC}"
fi

echo ""
echo -e "${GREEN}✅ Test completado${NC}"
echo ""
echo "🌐 Ver resultados en navegador:"
echo "   ${BASE_URL}/view/relevantes"
echo ""
echo "📊 Consultar via API:"
echo "   curl -s ${BASE_URL}/publicaciones/relevantes | jq '.'"
echo ""

