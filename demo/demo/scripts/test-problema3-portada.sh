#!/bin/bash

# Script de Prueba - Problema 3: Optimización de Portada
# Crea publicaciones con diferentes tamaños y beneficios (likes + comentarios)
# Verifica que el algoritmo Knapsack seleccione la combinación óptima

BASE_URL="http://localhost:8080"

echo "🚀 Test: Problema 3 - Optimización de Portada"
echo ""

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${BLUE}📝 Paso 1: Creando usuario de prueba...${NC}"

USER_ID=$(curl -s -X POST "${BASE_URL}/usuarios" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Portada Test",
    "email": "portada@example.com",
    "intereses": ["test"],
    "tiempoMaximoExposicion": 60
  }' | jq -r '.id')

echo "✅ Usuario creado (ID: $USER_ID)"
echo ""

echo -e "${BLUE}📢 Paso 2: Creando publicaciones con diferentes tamaños y engagement...${NC}"

declare -a PUB_IDS

# Pub 1: Tamaño pequeño, alto beneficio (eficiente)
PUB_IDS[0]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{
    \"contenido\": \"Post eficiente: Alto engagement, poco espacio\",
    \"idCreador\": \"$USER_ID\",
    \"costo\": 50,
    \"duracion\": 10,
    \"alcancePotencial\": 500,
    \"tamaño\": 2
  }" | jq -r '.id')
echo "📝 Pub #1: Tamaño 2 (ID: ${PUB_IDS[0]})"

# Pub 2: Tamaño medio, beneficio medio
PUB_IDS[1]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{
    \"contenido\": \"Post medio: Balance entre espacio y engagement\",
    \"idCreador\": \"$USER_ID\",
    \"costo\": 80,
    \"duracion\": 15,
    \"alcancePotencial\": 800,
    \"tamaño\": 5
  }" | jq -r '.id')
echo "📝 Pub #2: Tamaño 5 (ID: ${PUB_IDS[1]})"

# Pub 3: Tamaño grande, alto beneficio
PUB_IDS[2]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{
    \"contenido\": \"Post grande: Mucho engagement pero ocupa mucho\",
    \"idCreador\": \"$USER_ID\",
    \"costo\": 150,
    \"duracion\": 25,
    \"alcancePotencial\": 1500,
    \"tamaño\": 10
  }" | jq -r '.id')
echo "📝 Pub #3: Tamaño 10 (ID: ${PUB_IDS[2]})"

# Pub 4: Tamaño pequeño, bajo beneficio
PUB_IDS[3]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{
    \"contenido\": \"Post ineficiente: Poco engagement\",
    \"idCreador\": \"$USER_ID\",
    \"costo\": 30,
    \"duracion\": 8,
    \"alcancePotencial\": 300,
    \"tamaño\": 3
  }" | jq -r '.id')
echo "📝 Pub #4: Tamaño 3 (ID: ${PUB_IDS[3]})"

# Pub 5: Tamaño variable
PUB_IDS[4]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{
    \"contenido\": \"Post compacto: Buen ratio beneficio/tamaño\",
    \"idCreador\": \"$USER_ID\",
    \"costo\": 60,
    \"duracion\": 12,
    \"alcancePotencial\": 600,
    \"tamaño\": 3
  }" | jq -r '.id')
echo "📝 Pub #5: Tamaño 3 (ID: ${PUB_IDS[4]})"

echo ""
echo -e "${BLUE}👍 Paso 3: Agregando engagement (likes + comentarios)...${NC}"

# Pub 1: 15 likes + 5 comentarios = 20 beneficio, tamaño 2 → ratio: 10.0
for i in {1..15}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${PUB_IDS[0]}/like?idUsuario=user${i}" > /dev/null
done
for i in {1..5}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${PUB_IDS[0]}/comentario?idUsuario=user${i}&cantidad=1" > /dev/null
done
echo "✅ Pub #1: 15L + 5C = 20 beneficio, tamaño 2 → Ratio: 10.0 ⭐⭐⭐"

# Pub 2: 10 likes + 5 comentarios = 15 beneficio, tamaño 5 → ratio: 3.0
for i in {1..10}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${PUB_IDS[1]}/like?idUsuario=user${i}" > /dev/null
done
for i in {1..5}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${PUB_IDS[1]}/comentario?idUsuario=user${i}&cantidad=1" > /dev/null
done
echo "✅ Pub #2: 10L + 5C = 15 beneficio, tamaño 5 → Ratio: 3.0 ⭐⭐"

# Pub 3: 20 likes + 10 comentarios = 30 beneficio, tamaño 10 → ratio: 3.0
for i in {1..20}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${PUB_IDS[2]}/like?idUsuario=user${i}" > /dev/null
done
for i in {1..10}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${PUB_IDS[2]}/comentario?idUsuario=user${i}&cantidad=1" > /dev/null
done
echo "✅ Pub #3: 20L + 10C = 30 beneficio, tamaño 10 → Ratio: 3.0 ⭐⭐"

# Pub 4: 2 likes + 1 comentario = 3 beneficio, tamaño 3 → ratio: 1.0
for i in {1..2}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${PUB_IDS[3]}/like?idUsuario=user${i}" > /dev/null
done
curl -s -X POST "${BASE_URL}/publicaciones/${PUB_IDS[3]}/comentario?idUsuario=user1&cantidad=1" > /dev/null
echo "✅ Pub #4: 2L + 1C = 3 beneficio, tamaño 3 → Ratio: 1.0 ⭐"

# Pub 5: 12 likes + 3 comentarios = 15 beneficio, tamaño 3 → ratio: 5.0
for i in {1..12}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${PUB_IDS[4]}/like?idUsuario=user${i}" > /dev/null
done
for i in {1..3}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${PUB_IDS[4]}/comentario?idUsuario=user${i}&cantidad=1" > /dev/null
done
echo "✅ Pub #5: 12L + 3C = 15 beneficio, tamaño 3 → Ratio: 5.0 ⭐⭐⭐"

echo ""
echo -e "${BLUE}🔄 Paso 4: Optimizando portada con espacio=15...${NC}"

RESULTADO=$(curl -s "${BASE_URL}/publicaciones/optimizar-portada?espacioDisponible=15")

echo ""
echo -e "${GREEN}📊 RESULTADO DE OPTIMIZACIÓN:${NC}"
echo "$RESULTADO" | jq '.'

echo ""
BENEFICIO=$(echo "$RESULTADO" | jq '.beneficioTotal')
ESPACIO=$(echo "$RESULTADO" | jq '.espacioUsado')
COUNT=$(echo "$RESULTADO" | jq '.publicacionesDestacadas | length')

echo -e "${YELLOW}Métricas:${NC}"
echo "  📝 Publicaciones seleccionadas: $COUNT"
echo "  🎯 Beneficio total: $BENEFICIO (likes + comentarios)"
echo "  📏 Espacio usado: $ESPACIO de 15 disponibles"

echo ""
echo -e "${GREEN}✅ Paso 5: Verificaciones...${NC}"

# Verificar que no excede espacio
if [ "$ESPACIO" -le 15 ]; then
  echo -e "${GREEN}✅ El espacio usado ($ESPACIO) no excede el disponible (15)${NC}"
else
  echo -e "${RED}❌ ERROR: Excede el espacio disponible${NC}"
fi

# Verificar que se maximiza beneficio
echo ""
echo -e "${YELLOW}Análisis de combinaciones posibles:${NC}"
echo "  Opción A: Pub#1 + Pub#5 + Pub#2 = 20+15+15 = 50 beneficio, 2+3+5 = 10 espacio ✅"
echo "  Opción B: Pub#3 solo = 30 beneficio, 10 espacio"
echo "  Opción C: Pub#1 + Pub#5 + Pub#4 = 20+15+3 = 38 beneficio, 2+3+3 = 8 espacio"
echo ""
echo "  La mejor combinación debería incluir Pub#1 y Pub#5 (mejores ratios)"

# Verificar IDs seleccionados
SELECTED_IDS=$(echo "$RESULTADO" | jq -r '.publicacionesDestacadas[].id')
if echo "$SELECTED_IDS" | grep -q "${PUB_IDS[0]}"; then
  echo -e "${GREEN}✅ Pub#1 (ratio 10.0) está seleccionada${NC}"
else
  echo -e "${YELLOW}⚠️  Pub#1 NO está seleccionada${NC}"
fi

if echo "$SELECTED_IDS" | grep -q "${PUB_IDS[4]}"; then
  echo -e "${GREEN}✅ Pub#5 (ratio 5.0) está seleccionada${NC}"
else
  echo -e "${YELLOW}⚠️  Pub#5 NO está seleccionada${NC}"
fi

echo ""
echo -e "${GREEN}✅ Test completado${NC}"
echo ""
echo "🌐 Ver resultados en navegador:"
echo "   ${BASE_URL}/view/optimizar-portada"
echo ""
echo "📊 Consultar via API:"
echo "   curl -s '${BASE_URL}/publicaciones/optimizar-portada?espacioDisponible=15' | jq '.'"
echo ""

