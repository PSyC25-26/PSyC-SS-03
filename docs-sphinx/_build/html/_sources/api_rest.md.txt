# API REST Reference

## Endpoints Summary

### Carceles

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/carcel/crear` | Create new prison |
| `GET` | `/carcel` | List all prisons |
| `GET` | `/carcel/{id}` | Get prison by ID |
| `DELETE` | `/carcel/eliminar/{id}` | Delete prison |
| `GET` | `/carcel/ocupacion` | Prisoner count per prison |
| `GET` | `/carcel/{id}/ocupacion` | Occupancy of a specific prison |
| `GET` | `/carcel/estadisticas-completas` | id, name, capacity and occupancy |

### Presos

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/preso/crear` | Create new prisoner |
| `GET` | `/preso/todos` | List all prisoners |
| `GET` | `/preso/{id}` | Get prisoner by ID |
| `DELETE` | `/preso/eliminar/{id}` | Delete prisoner |
| `POST` | `/preso/trasladar/{id}/{nombreCarcel}` | Transfer prisoner to another prison |
| `GET` | `/preso/filtrar/delito/{delito}` | Filter prisoners by crime |
| `POST` | `/preso/modificar-condena/{id}/{nuevaCondena}` | Modify sentence in years |

### Cuentas

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/cuentas/crear` | Create new account |
| `DELETE` | `/cuentas/eliminar/{id}` | Delete account |
| `DELETE` | `/cuentas/eliminar/policia/{id}` | Delete POLICIA account |
| `GET` | `/cuentas/policias` | List POLICIA accounts |
| `POST` | `/cuentas/login/policia` | Authenticate user |

## Request / Response Examples

### Create a prison

**Request:**
```http
POST /carcel/crear
Content-Type: application/json

{
  "nombre": "Centro Penitenciario Basauri",
  "localidad": "Bizkaia",
  "capacidad": 250
}
```

**Response `201 Created`:**
```json
{
  "idCarcel": 1,
  "nombre": "Centro Penitenciario Basauri",
  "localidad": "Bizkaia",
  "capacidad": 250
}
```

### Login

**Request:**
```http
POST /cuentas/login/policia
Content-Type: application/json

{
  "username": "admin",
  "password": "admin"
}
```

**Response `200 OK`:**
```json
{
  "idCuentas": 1,
  "username": "admin",
  "tipoCuenta": "POLICIA"
}
```

**Response `401 Unauthorized`:**
```json
"Credenciales incorrectas"
```

### Transfer a prisoner

```http
POST /preso/trasladar/3/Centro%20Penitenciario%20Basauri
```

**Response `200 OK`:**
```json
"Traslado realizado correctamente a Centro Penitenciario Basauri"
```

## Available Crimes (enum `Delito`)

```
HOMICIDIO  SECUESTRO  ROBO  AGRESION_SEXUAL  PEDOFILIA
ESTAFA  TRAF_DROGAS  TRAF_PERSONAS  TERRORISMO
PIROMANIA  BLANQUEO_DINERO  FALSIFICACION_DOC
```

## Account Types (enum `TipoCuenta`)

```
POLICIA       -> full access
FAMILIA       -> limited consultation
GUBERNAMENTAL -> statistics and reports
```
