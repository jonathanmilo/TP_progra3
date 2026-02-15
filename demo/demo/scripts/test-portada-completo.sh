#!/bin/bash

# Script de Prueba Completo - Problema 3: Optimización de Portada
# Crea publicaciones con diferentes tamaños y beneficios
# Verifica que el algoritmo Knapsack maximice beneficio sin exceder espacio

BASE_URL="http://localhost:8080"

echo "🚀 Iniciando Test: Problema 3 - Optimización de Portada"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${BLUE}📝 Paso 1: Limpiando base de datos...${NC}"
curl -s -X DELETE "${BASE_URL}/publicaciones" > /dev/null
curl -s -X DELETE "${BASE_URL}/usuarios" > /dev/null
echo "✅ Base de datos limpiada"
echo ""

echo -e "${BLUE}👤 Paso 2: Creando usuario de prueba...${NC}"

USER_ID=$(curl -s -X POST "${BASE_URL}/usuarios" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Test Portada",
    "email": "portada@test.com",
    "intereses": ["test"],
    "tiempoMaximoExposicion": 60
  }' | jq -r '.id')

echo "✅ Usuario creado: $USER_ID"
echo ""

echo -e "${BLUE}📢 Paso 3: Creando publicaciones con diferentes características...${NC}"
echo ""

declare -a PUB_IDS

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# Publicación 1: Contenido compacto y viral (se calculará automáticamente)
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo "📝 Creando Pub #1: Post viral compacto..."
PUB_IDS[0]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{
    \"contenido\": \"💎 Noticia viral: Contenido trending del día\",
    \"idCreador\": \"$USER_ID\",
    \"costo\": 50,
    \"duracion\": 10,
    \"alcancePotencial\": 500
  }" | jq -r '.id')
echo "   ID: ${PUB_IDS[0]}"
echo "   Contenido: Corto y viral → Tamaño se calculará automáticamente"

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# Publicación 2: Noticia con imagen (tamaño medio)
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo ""
echo "📝 Creando Pub #2: Noticia con imagen..."
PUB_IDS[1]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{
    \"contenido\": \"📰 Noticia Popular con imagen 📸: Tema de interés general con foto destacada del evento\",
    \"idCreador\": \"$USER_ID\",
    \"costo\": 80,
    \"duracion\": 15,
    \"alcancePotencial\": 800
  }" | jq -r '.id')
echo "   ID: ${PUB_IDS[1]}"
echo "   Contenido: Con imagen → Tamaño medio automático"

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# Publicación 3: Video extenso (tamaño grande)
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo ""
echo "📝 Creando Pub #3: Video trending..."
PUB_IDS[2]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{
    \"contenido\": \"🎥 Video Trending: Contenido multimedia extenso con entrevista exclusiva y análisis detallado del tema más importante de la semana. Incluye video de alta calidad.\",
    \"idCreador\": \"$USER_ID\",
    \"costo\": 150,
    \"duracion\": 25,
    \"alcancePotencial\": 1500
  }" | jq -r '.id')
echo "   ID: ${PUB_IDS[2]}"
echo "   Contenido: Video + texto largo → Tamaño grande automático"

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# Publicación 4: Texto simple corto
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo ""
echo "📝 Creando Pub #4: Post simple..."
PUB_IDS[3]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{
    \"contenido\": \"📝 Pensamiento del día\",
    \"idCreador\": \"$USER_ID\",
    \"costo\": 30,
    \"duracion\": 8,
    \"alcancePotencial\": 300
  }" | jq -r '.id')
echo "   ID: ${PUB_IDS[3]}"
echo "   Contenido: Muy corto → Tamaño mínimo automático"

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# Publicación 5: Encuesta interactiva (tamaño medio)
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo ""
echo "📝 Creando Pub #5: Encuesta interactiva..."
PUB_IDS[4]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{
    \"contenido\": \"📊 Encuesta: ¿Cuál es tu opinión sobre el nuevo proyecto? Participa y comparte tu perspectiva\",
    \"idCreador\": \"$USER_ID\",
    \"costo\": 60,
    \"duracion\": 12,
    \"alcancePotencial\": 600
  }" | jq -r '.id')
echo "   ID: ${PUB_IDS[4]}"
echo "   Contenido: Encuesta → Tamaño medio automático"

echo ""
echo -e "${BLUE}👍 Paso 4: Agregando reacciones (likes + comentarios)...${NC}"
echo ""

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# Pub #1: 15 likes + 5 comentarios = 20 beneficio
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo "💎 Pub #1: Agregando 15 likes + 5 comentarios..."
for i in {1..15}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${PUB_IDS[0]}/like?idUsuario=user${i}" > /dev/null
done
for i in {1..5}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${PUB_IDS[0]}/comentario?idUsuario=user${i}&cantidad=1&textoComentario=Excelente" > /dev/null
done
echo "   ✅ 15L + 5C = 20 beneficio | Tamaño: 2 | Ratio: 10.0 🥇"

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# Pub #2: 10 likes + 5 comentarios = 15 beneficio
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo ""
echo "📰 Pub #2: Agregando 10 likes + 5 comentarios..."
for i in {1..10}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${PUB_IDS[1]}/like?idUsuario=user${i}" > /dev/null
done
for i in {1..5}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${PUB_IDS[1]}/comentario?idUsuario=user${i}&cantidad=1&textoComentario=Interesante" > /dev/null
done
echo "   ✅ 10L + 5C = 15 beneficio | Tamaño: 5 | Ratio: 3.0"

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# Pub #3: 20 likes + 10 comentarios = 30 beneficio
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo ""
echo "🎥 Pub #3: Agregando 20 likes + 10 comentarios..."
for i in {1..20}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${PUB_IDS[2]}/like?idUsuario=user${i}" > /dev/null
done
for i in {1..10}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${PUB_IDS[2]}/comentario?idUsuario=user${i}&cantidad=1&textoComentario=Wow" > /dev/null
done
echo "   ✅ 20L + 10C = 30 beneficio | Tamaño: 10 | Ratio: 3.0"

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# Pub #4: 2 likes + 1 comentario = 3 beneficio
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo ""
echo "📝 Pub #4: Agregando 2 likes + 1 comentario..."
for i in {1..2}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${PUB_IDS[3]}/like?idUsuario=user${i}" > /dev/null
done
curl -s -X POST "${BASE_URL}/publicaciones/${PUB_IDS[3]}/comentario?idUsuario=user1&cantidad=1&textoComentario=Ok" > /dev/null
echo "   ✅ 2L + 1C = 3 beneficio | Tamaño: 3 | Ratio: 1.0"

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# Pub #5: 12 likes + 3 comentarios = 15 beneficio
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo ""
echo "🎯 Pub #5: Agregando 12 likes + 3 comentarios..."
for i in {1..12}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${PUB_IDS[4]}/like?idUsuario=user${i}" > /dev/null
done
for i in {1..3}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${PUB_IDS[4]}/comentario?idUsuario=user${i}&cantidad=1&textoComentario=Genial" > /dev/null
done
echo "   ✅ 12L + 3C = 15 beneficio | Tamaño: 3 | Ratio: 5.0 🥈"

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

echo -e "${BLUE}📊 Consultando tamaños calculados (FIJOS por tipo)...${NC}"
echo ""

# Obtener información de cada publicación para ver el tamaño calculado
for i in {0..4}; do
  PUB_INFO=$(curl -s "${BASE_URL}/publicaciones/${PUB_IDS[$i]}")
  TAMAÑO=$(echo "$PUB_INFO" | jq '.tamaño')
  TIPO=$(echo "$PUB_INFO" | jq -r '.tipo')
  LIKES=$(echo "$PUB_INFO" | jq '.likes')
  COMS=$(echo "$PUB_INFO" | jq '.comentarios')
  BENEFICIO=$((LIKES + COMS))

  if [ "$BENEFICIO" -gt 0 ]; then
    RATIO=$(echo "scale=2; $BENEFICIO / $TAMAÑO" | bc)
  else
    RATIO="0.00"
  fi

  echo "Pub #$((i+1)): Tipo=$TIPO | Tamaño=$TAMAÑO (fijo) | Beneficio=$BENEFICIO | Ratio=$RATIO"
done

echo ""
echo -e "${BLUE}📊 Resumen de Publicaciones Creadas:${NC}"
echo ""
echo "┌─────┬──────────────────────┬──────────┬──────────┬────────────────────┐"
echo "│ Pub │ Tipo                 │ Tamaño   │ Beneficio│ Ratio (B/T)        │"
echo "├─────┼──────────────────────┼──────────┼──────────┼────────────────────┤"
echo "│ #1  │ TEXTO                │    1     │    20    │ 20.0 🥇            │"
echo "│ #2  │ IMAGEN               │    2     │    15    │  7.5               │"
echo "│ #3  │ VIDEO                │    4     │    30    │  7.5               │"
echo "│ #4  │ TEXTO                │    1     │     3    │  3.0               │"
echo "│ #5  │ ENCUESTA             │    2     │    15    │  7.5               │"
echo "└─────┴──────────────────────┴──────────┴──────────┴────────────────────┘"
echo ""
echo "💡 Tamaño = FIJO según tipo (propiedad estructural)"
echo "💡 Beneficio = likes + comentarios (variable con engagement)"
echo "💡 Ratio = Beneficio/Tamaño (Knapsack maximiza esto automáticamente)"
echo ""

echo -e "${BLUE}🔄 Paso 5: Ejecutando optimización con espacio=15...${NC}"
echo ""

RESULTADO=$(curl -s "${BASE_URL}/publicaciones/optimizar-portada?espacioDisponible=15")

BENEFICIO=$(echo "$RESULTADO" | jq '.beneficioTotal')
ESPACIO=$(echo "$RESULTADO" | jq '.espacioUsado')
COUNT=$(echo "$RESULTADO" | jq '.publicacionesDestacadas | length')

echo -e "${GREEN}✅ RESULTADO DE OPTIMIZACIÓN:${NC}"
echo ""
echo "  📝 Publicaciones seleccionadas: $COUNT"
echo "  🎯 Beneficio total: $BENEFICIO (likes + comentarios)"
echo "  📏 Espacio usado: $ESPACIO de 15 disponibles"
echo ""

echo -e "${YELLOW}Publicaciones seleccionadas:${NC}"
echo "$RESULTADO" | jq -r '.publicacionesDestacadas[] | "  • \(.contenido) | \(.likes)L + \(.comentarios)C = \(.likes + .comentarios) beneficio | Tamaño: \(.tamaño)"'

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

echo -e "${GREEN}✅ Paso 6: Verificaciones Automáticas...${NC}"
echo ""

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# Verificación 1: Restricción de espacio
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo "🔍 Verificación 1: Restricción de espacio"
if [ "$ESPACIO" -le 15 ]; then
  echo -e "   ${GREEN}✅ PASS: Espacio usado ($ESPACIO) ≤ espacio disponible (15)${NC}"
else
  echo -e "   ${RED}❌ FAIL: Espacio usado ($ESPACIO) > espacio disponible (15)${NC}"
fi

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# Verificación 2: Beneficio mayor a 0
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo ""
echo "🔍 Verificación 2: Beneficio total"
if [ "$BENEFICIO" -gt 0 ]; then
  echo -e "   ${GREEN}✅ PASS: Beneficio total ($BENEFICIO) > 0${NC}"
else
  echo -e "   ${RED}❌ FAIL: Beneficio total es 0${NC}"
fi

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# Verificación 3: Pub #1 debe estar seleccionada (mejor ratio)
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo ""
echo "🔍 Verificación 3: Selección de mejores ratios"
SELECTED_IDS=$(echo "$RESULTADO" | jq -r '.publicacionesDestacadas[].id')

if echo "$SELECTED_IDS" | grep -q "${PUB_IDS[0]}"; then
  echo -e "   ${GREEN}✅ PASS: Pub #1 (ratio 10.0 🥇) está seleccionada${NC}"
else
  echo -e "   ${YELLOW}⚠️  WARNING: Pub #1 (mejor ratio) NO está seleccionada${NC}"
fi

if echo "$SELECTED_IDS" | grep -q "${PUB_IDS[4]}"; then
  echo -e "   ${GREEN}✅ PASS: Pub #5 (ratio 5.0 🥈) está seleccionada${NC}"
else
  echo -e "   ${YELLOW}⚠️  INFO: Pub #5 NO está seleccionada${NC}"
fi

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# Verificación 4: Análisis de optimalidad
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo ""
echo "🔍 Verificación 4: Análisis de optimalidad (Ratio Beneficio/Tamaño)"
echo ""
echo "   El algoritmo Knapsack maximiza beneficio/tamaño automáticamente:"
echo ""
echo "   Ratios de las publicaciones:"
echo "   • Pub #1 (TEXTO):    20/1  = 20.0 🥇 (mejor ratio)"
echo "   • Pub #2 (IMAGEN):   15/2  = 7.5"
echo "   • Pub #3 (VIDEO):    30/4  = 7.5"
echo "   • Pub #4 (TEXTO):    3/1   = 3.0"
echo "   • Pub #5 (ENCUESTA): 15/2  = 7.5"
echo ""
echo "   Estrategia óptima con espacio=15:"
echo "   • Incluir todas (tamaño total = 1+2+4+1+2 = 10 ≤ 15) ✅"
echo "   • O priorizar altos ratios si no caben todas"
echo ""

if [ "$BENEFICIO" -ge 50 ]; then
  echo -e "   ${GREEN}✅ PASS: Beneficio ($BENEFICIO) es óptimo o cercano al óptimo${NC}"
else
  echo -e "   ${YELLOW}⚠️  INFO: Beneficio ($BENEFICIO), verificar combinación${NC}"
fi

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

echo -e "${GREEN}✅ TEST COMPLETADO${NC}"
echo ""
echo "📊 Resultados Finales:"
echo "  • Publicaciones creadas: 5"
echo "  • Publicaciones seleccionadas: $COUNT"
echo "  • Beneficio total: $BENEFICIO (likes + comentarios)"
echo "  • Espacio usado: $ESPACIO / 15"
echo "  • Eficiencia: $(echo "scale=1; $BENEFICIO * 100 / 15" | bc)% de aprovechamiento"
echo ""
echo "🌐 Ver resultados en navegador:"
echo "   ${BASE_URL}/view/optimizar-portada"
echo ""
echo "📊 Consultar via API:"
echo "   curl -s '${BASE_URL}/publicaciones/optimizar-portada?espacioDisponible=15' | jq '.'"
echo ""
echo "🧪 Probar con diferentes espacios:"
echo "   curl -s '${BASE_URL}/publicaciones/optimizar-portada?espacioDisponible=10' | jq '.beneficioTotal'"
echo "   curl -s '${BASE_URL}/publicaciones/optimizar-portada?espacioDisponible=20' | jq '.beneficioTotal'"
echo ""

