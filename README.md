# PSyC-SS-03
## Tabla de contenidos
- [Descripción](#Descripción)
- [Componentes_clave](#Componentes_clave)
- [Tecnologías](#Tecnologías)
- [Requisitos_previos](#Requisitos_previos)

## Descripción
Software de gestión para cárceles, que proporciona información sobre cárceles y presos a distintos agentes (policía, familias,...), además de permitir filtrar presos y trasladarlos entre cárceles.

## Componentes_clave
- `src\main\java\com\example` (Carpeta de código principal)
  - `Dao/` Interfaces para interacción con la base de datos
  - `Entidades/` Definición de entidades
  - `Facade/` Controladores RESTful para manejar las peticiones
  - `GUI/` Interfaces visuales del proyecto
  - `Service/` Capa de lógica de negocio
- `src\test\java\com\example\JailQ`(Carpeta de test)
    - `Service/` Test unitarios
    - `Integration/` Test de integración
    - `Performance/` Test de rendimiento
- **Javadoc**: La documentación de la API es generada mediante el uso de Javadoc.

## Tecnologías
Java 21 + Spring Boot 4.0.4
MySQL — base de datos
Mockito, JUnit5 y JaCoCo

## Requisitos_previos
Java 21 o superior y Maven instalado.
Para utilizar este proyecto debes tener Docker Desktop y MySQL instalado en tu ordenador. Debes tener Docker runneando.
Para consultar la bd en MySQL debes abrir una conexión a:
- Connection name: JailQ Docker
- Hostname: 127.0.0.1
- Port: el que indique Docker
- Username: myuser
- Password: secret


