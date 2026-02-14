#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
ADMIN_EMAIL="${ADMIN_EMAIL:-admin@local}"

LOGIN_URL="${BASE_URL}/api/1/auth/login"
CREATE_URL="${BASE_URL}/api/1/car"

# 1) Login -> pega accessToken
ADMIN_PASSWORD="${ADMIN_PASSWORD:-admin123}"

echo "-> Login: ${LOGIN_URL}"
login_resp="$(
  curl -sS -X POST "${LOGIN_URL}" \
    -H "Content-Type: application/json" \
    -H "Accept: application/json" \
    -d "{\"email\":\"${ADMIN_EMAIL}\",\"password\":\"${ADMIN_PASSWORD}\"}" \
    -w "\n%{http_code}"
)"

login_body="$(echo "$login_resp" | sed '$d')"
login_code="$(echo "$login_resp" | tail -n 1)"

if [[ "$login_code" != "200" ]]; then
  echo "❌ Login falhou (HTTP $login_code)"
  echo "Response:"
  echo "$login_body"
  exit 1
fi

TOKEN="$(
  printf '%s' "$login_body" \
  | python3 -c 'import sys,json; print(json.load(sys.stdin)["accessToken"])'
)"


echo "✅ Token obtido."
AUTH="Bearer ${TOKEN}"

# 2) 30 carros (payload compatível com CarCreateRequest)
CARS=(
'{"brand":"Toyota","model":"Corolla","year":2022,"color":"White","price":145000.00}'
'{"brand":"Honda","model":"Civic","year":2021,"color":"Black","price":139900.00}'
'{"brand":"BMW","model":"320i","year":2023,"color":"Blue","price":289900.00}'
'{"brand":"Audi","model":"A3","year":2022,"color":"Gray","price":259900.00}'
'{"brand":"Mercedes","model":"C180","year":2021,"color":"Silver","price":279000.00}'
'{"brand":"Volkswagen","model":"Jetta","year":2020,"color":"White","price":129900.00}'
'{"brand":"Hyundai","model":"Elantra","year":2022,"color":"Red","price":124000.00}'
'{"brand":"Kia","model":"Cerato","year":2021,"color":"Black","price":119000.00}'
'{"brand":"Chevrolet","model":"Cruze","year":2019,"color":"White","price":98000.00}'
'{"brand":"Nissan","model":"Sentra","year":2023,"color":"Gray","price":148500.00}'

'{"brand":"Toyota","model":"Camry","year":2022,"color":"Black","price":189900.00}'
'{"brand":"Honda","model":"Accord","year":2021,"color":"White","price":182000.00}'
'{"brand":"BMW","model":"X1","year":2023,"color":"Blue","price":309900.00}'
'{"brand":"Audi","model":"Q3","year":2022,"color":"Gray","price":299000.00}'
'{"brand":"Mercedes","model":"GLA200","year":2021,"color":"Silver","price":315000.00}'
'{"brand":"Volkswagen","model":"Tiguan","year":2020,"color":"White","price":165000.00}'
'{"brand":"Hyundai","model":"Tucson","year":2022,"color":"Black","price":179000.00}'
'{"brand":"Kia","model":"Sportage","year":2023,"color":"Green","price":189000.00}'
'{"brand":"Chevrolet","model":"Equinox","year":2021,"color":"Blue","price":172000.00}'
'{"brand":"Nissan","model":"Kicks","year":2022,"color":"Red","price":118000.00}'

'{"brand":"Fiat","model":"Pulse","year":2023,"color":"White","price":99000.00}'
'{"brand":"Jeep","model":"Compass","year":2022,"color":"Black","price":189000.00}'
'{"brand":"Renault","model":"Duster","year":2021,"color":"Gray","price":105000.00}'
'{"brand":"Peugeot","model":"3008","year":2022,"color":"Blue","price":199000.00}'
'{"brand":"Citroen","model":"C4 Cactus","year":2021,"color":"White","price":112000.00}'
'{"brand":"Ford","model":"Territory","year":2023,"color":"Silver","price":215000.00}'
'{"brand":"Mitsubishi","model":"Outlander","year":2022,"color":"Black","price":225000.00}'
'{"brand":"Subaru","model":"Forester","year":2021,"color":"Green","price":219000.00}'
'{"brand":"Volvo","model":"XC40","year":2023,"color":"White","price":269000.00}'
'{"brand":"Land Rover","model":"Discovery Sport","year":2022,"color":"Gray","price":349000.00}'
)

echo
echo "-> Criando carros em: ${CREATE_URL}"
ok=0
fail=0
i=1

for car in "${CARS[@]}"; do
  resp="$(
    curl -sS -X POST "${CREATE_URL}" \
      -H "Content-Type: application/json" \
      -H "Accept: application/json" \
      -H "Authorization: ${AUTH}" \
      -d "${car}" \
      -w "\n%{http_code}"
  )"

  body="$(echo "$resp" | sed '$d')"
  code="$(echo "$resp" | tail -n 1)"

  if [[ "$code" == "201" || "$code" == "200" ]]; then
    echo "[$i/30] ✅ OK (HTTP $code)"
    ok=$((ok+1))
  else
    echo "[$i/30] ❌ FAIL (HTTP $code) payload=$car"
    echo "Response: $body"
    fail=$((fail+1))
  fi
  i=$((i+1))
done

echo
echo "Final: OK=$ok FAIL=$fail"
