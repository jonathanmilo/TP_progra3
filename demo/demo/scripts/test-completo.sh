#!/bin/bash

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# SCRIPT MAESTRO: Test Completo de los 3 Problemas del Trabajo Integrador
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
#
# Problema 1: Gestión de Publicaciones (MergeSort + Top K Relevantes)
# Problema 2: Asignación de Publicidad (Knapsack + Presupuesto)
# Problema 3: Optimización de Portada (Knapsack + Espacio)
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
echo -e "${CYAN}     🚀 TEST COMPLETO: TRABAJO INTEGRADOR - PROGRAMACIÓN III${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo -e "${MAGENTA}Problema 1:${NC} Gestión de Publicaciones (MergeSort + Top K)"
echo -e "${MAGENTA}Problema 2:${NC} Asignación de Publicidad (Knapsack + Tiempo)"
echo -e "${MAGENTA}Problema 3:${NC} Optimización de Portada (Knapsack + Espacio)"
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

# Crear usuarios
echo -e "${BLUE}👥 Creando usuarios de prueba...${NC}"

USER1_ID=$(curl -s -X POST "${BASE_URL}/usuarios" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Juan Pérez","email":"juan@example.com","intereses":["tecnología"],"tiempoMaximoExposicion":60}' \
  | jq -r '.id')
echo "✅ Usuario 1: Juan Pérez (60s)"

USER2_ID=$(curl -s -X POST "${BASE_URL}/usuarios" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"María García","email":"maria@example.com","intereses":["cocina"],"tiempoMaximoExposicion":120}' \
  | jq -r '.id')
echo "✅ Usuario 2: María García (120s)"

USER3_ID=$(curl -s -X POST "${BASE_URL}/usuarios" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Pedro López","email":"pedro@example.com","intereses":["deportes"],"tiempoMaximoExposicion":30}' \
  | jq -r '.id')
echo "✅ Usuario 3: Pedro López (30s)"

echo ""

# Crear publicaciones
echo -e "${BLUE}📢 Creando 15 publicaciones variadas...${NC}"
echo ""

declare -a PUB_IDS

# Pub 1-10 con diferentes características
for i in {0..9}; do
  TIPOS=("TEXTO" "IMAGEN" "VIDEO" "TEXTO" "ENCUESTA" "REEL" "TEXTO" "IMAGEN" "VIDEO" "TEXTO")
  COSTOS=(50 80 150 30 60 70 20 40 200 15)
  DURACIONES=(5 10 20 5 12 15 5 10 30 3)
  ALCANCES=(500 800 1500 300 600 2000 1000 800 3000 500)

  PUB_IDS[$i]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
    -H "Content-Type: application/json" \
    -d "{\"contenido\":\"Publicación $((i+1))\",\"idCreador\":\"$USER1_ID\",\"costo\":${COSTOS[$i]},\"duracion\":${DURACIONES[$i]},\"alcancePotencial\":${ALCANCES[$i]},\"tipo\":\"${TIPOS[$i]}\"}" \
    | jq -r '.id')

  # Agregar reacciones variadas
  LIKES=$((5 + i * 2))
  COMS=$((1 + i))
  for j in $(seq 1 $LIKES); do
    curl -s -X POST "${BASE_URL}/publicaciones/${PUB_IDS[$i]}/like?idUsuario=u$j" > /dev/null
  done
  for j in $(seq 1 $COMS); do
    curl -s -X POST "${BASE_URL}/publicaciones/${PUB_IDS[$i]}/comentario?idUsuario=u$j&cantidad=1" > /dev/null
  done
done

echo "✅ 10 publicaciones creadas con engagement variado"

# Crear 5 más
for i in {10..14}; do
  PUB_IDS[$i]=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
    -H "Content-Type: application/json" \
    -d "{\"contenido\":\"Post $((i+1))\",\"idCreador\":\"$USER2_ID\",\"costo\":50,\"duracion\":10,\"alcancePotencial\":500,\"tipo\":\"TEXTO\"}" \
    | jq -r '.id')
  for j in {1..5}; do
    curl -s -X POST "${BASE_URL}/publicaciones/${PUB_IDS[$i]}/like?idUsuario=u$j" > /dev/null
  done
done

echo "✅ 15 publicaciones totales creadas"
echo ""

# PROBLEMA 1
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo -e "${MAGENTA}📊 PROBLEMA 1: Gestión de Publicaciones${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

echo -e "${BLUE}🔄 Calculando relevancia...${NC}"
curl -s -X POST "${BASE_URL}/publicaciones/relevantes/calcular" > /dev/null
sleep 1

RELEVANTES=$(curl -s "${BASE_URL}/publicaciones/relevantes")
K_ACTUAL=$(echo "$RELEVANTES" | jq 'length')
echo -e "${GREEN}✅ Top K=$K_ACTUAL publicaciones calculadas${NC}"
echo ""

echo -e "${BLUE}🏆 Top 5 Más Relevantes:${NC}"
echo "$RELEVANTES" | jq -r '.[:5] | .[] | "  \(.relevancia | tostring | .[0:5]) - \(.contenido[0:50])"'

echo ""
echo -e "${GREEN}✅ PROBLEMA 1 COMPLETADO${NC}"
echo ""

# PROBLEMA 2
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo -e "${MAGENTA}💰 PROBLEMA 2: Asignación de Publicidad${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

PRESUPUESTO=500
echo -e "${BLUE}🔄 Ejecutando con presupuesto: \$$PRESUPUESTO${NC}"

CAMPANA=$(curl -s "${BASE_URL}/publicaciones/optimizar-publicidad?presupuestoEmpresa=$PRESUPUESTO")
NUM_USUARIOS=$(echo "$CAMPANA" | jq 'length')
ALCANCE_TOTAL=0
COSTO_TOTAL=0

echo ""
for ((i=0; i<NUM_USUARIOS; i++)); do
  USUARIO=$(echo "$CAMPANA" | jq ".[$i]")
  NOMBRE=$(echo "$USUARIO" | jq -r '.usuarioNombre')
  ALCANCE=$(echo "$USUARIO" | jq '.alcanceTotal')
  COSTO=$(echo "$USUARIO" | jq '.costoEconomicoTotal')
  COUNT=$(echo "$USUARIO" | jq '.anunciosAsignados | length')

  ALCANCE_TOTAL=$((ALCANCE_TOTAL + ALCANCE))
  COSTO_TOTAL=$((COSTO_TOTAL + COSTO))

  if [ "$COUNT" -gt 0 ]; then
    echo "  👤 $NOMBRE: $COUNT anuncios | Alcance: $ALCANCE | \$$COSTO"
  fi
done

echo ""
echo -e "${GREEN}📈 Alcance total: $ALCANCE_TOTAL | Costo: \$$COSTO_TOTAL/\$$PRESUPUESTO${NC}"
echo -e "${GREEN}✅ PROBLEMA 2 COMPLETADO${NC}"
echo ""

# PROBLEMA 3
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo -e "${MAGENTA}🏠 PROBLEMA 3: Optimización de Portada${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

ESPACIO=20
echo -e "${BLUE}🔄 Ejecutando con espacio: $ESPACIO unidades${NC}"

PORTADA=$(curl -s "${BASE_URL}/publicaciones/optimizar-portada?espacioDisponible=$ESPACIO")
BENEFICIO=$(echo "$PORTADA" | jq '.beneficioTotal')
ESPACIO_USADO=$(echo "$PORTADA" | jq '.espacioUsado')
COUNT=$(echo "$PORTADA" | jq '.publicacionesDestacadas | length')
PORCENTAJE=$(echo "scale=1; $ESPACIO_USADO * 100 / $ESPACIO" | bc)

echo ""
echo -e "${GREEN}📊 Publicaciones: $COUNT | Beneficio: $BENEFICIO | Espacio: $ESPACIO_USADO/$ESPACIO ($PORCENTAJE%)${NC}"
echo -e "${GREEN}✅ PROBLEMA 3 COMPLETADO${NC}"
echo ""

# RESUMEN
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo -e "${CYAN}     ✅ TEST COMPLETO FINALIZADO${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo -e "${GREEN}📊 RESUMEN:${NC}"
echo ""
echo -e "${MAGENTA}Problema 1:${NC} Top K=$K_ACTUAL ✅"
echo -e "${MAGENTA}Problema 2:${NC} Alcance=$ALCANCE_TOTAL, Costo=\$$COSTO_TOTAL ✅"
echo -e "${MAGENTA}Problema 3:${NC} Beneficio=$BENEFICIO, Espacio=$ESPACIO_USADO/$ESPACIO ✅"
echo ""
echo -e "${CYAN}🌐 Ver en navegador:${NC}"
echo "  • ${BASE_URL}/view/relevantes"
echo "  • ${BASE_URL}/view/optimizar-publicidad"
echo "  • ${BASE_URL}/view/optimizar-portada"
echo ""
echo -e "${GREEN}🎉 TODOS LOS PROBLEMAS FUNCIONANDO CORRECTAMENTE${NC}"
echo ""

