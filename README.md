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
Para utilizar este proyecto debes tener Docker Desktop, Maven, Java 25 y MySQL instalado en tu ordenador. Debes tener Docker runneando.
Para consultar la bd en MySQL debes abrir una conexión a:
- Port: 3307
- Username: myuser
- Password: secret

## Uso de tests de rendimiento e integración
- Para ejecutar el comando mvn clean install -U se debe primero ejecutar la ventana principal en pararelo e insertar como mínimo una cárcel y un preso.

