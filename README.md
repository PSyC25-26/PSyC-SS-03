# PSyC-SS-03
## Tabla de contenidos
- [Descripción](#Descripción)
- [Componentes_clave](#Componentes_clave)
- [Tecnologías](#Tecnologías)
- [Requisitos_previos](#Requisitos_previos)
- [How_to-Primera_ejecución](#How_to-Primera_ejecución)
- [Para_usar_la_aplicación](#Para_usar_la_aplicación)
- [Para_ejecutar_los_test](#Para_ejecutar_los_test)

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

## How_to-Primera_ejecución
1. Clone el repositorio
git clone https://github.com/PSyC25-26/PSyC-SS-03.git PSyC-SS-03

2. Habra Docker Desktop

3. Ejecute la aplicación mediante Spring Boot Dashboard --> "con.example.JailQ.JailQApplication
De esta manera se crearán los contenedores pertinentes y se hará conexión con la BD

5. Debe aparecer automaticamente la ventana principal de la aplicación: "JailQ - Sistema de Gestión Penitenciaria"

## Para_usar_la_aplicación
1. Ejecute la aplicación. Debe aparecer la ventana principal ("JailQ - Sistema de Gestión Penitenciaria")
2. Cree una cuenta de usuario:
- Acceda a "Gestión de cuentas".
- Inserte sus datos y pulse "Añadir cuenta"
- Pulse "Volver al Menú Principal"
4. Pulse el boton "iniciar sesión". Inserte su Username y Password.
5. Al iniciar sesión, vemos que ahora podemos entrar a las secciones de "Gestión de Cárceles" y "Gestión de Presos"
### Gestión de Cárceles
Mediante esta interfaz podemos crear cárceles y ver las estadisticas sobre cárceles.
#### Añadir una carcel
Para añadir una carcel rellene los datos sobre la carcel y pulse "Añadir Cárcel"
#### Consultar estadísticas
- Para consultar las estádisticas pulse "Ver Estadísticas"
- Se visualizará una nueva ventana "JailQ - Ocupación detallada"
- Seleccione la cárcel de la que se desea ver las estadísticas. Las estadísticas se visualizarán en la ventana
### Gestión de Presos
Mediante esta interfaz podemos crear un nuevo preso, listar presos, modificar presos o trasladar presos.
#### Crear nuevo preso
- Pulse "Crear nuevo preso"
- Verá la ventana "JailQ- Gestión de Presos"
- Rellene los datos pertinentes y pulse "Registrar Preso". Se registrará el preso en la base de datos.
#### Listar/modificar/trasladar preso
- Pulse "Listado/modificar/eliminar"
- Se visualizará una nueva ventana "JailQ - Listado de Presos". En esta ventana se ve una lista de los presos en la base de datos.
##### Para eliminar un preso
Seleccione un preso de la lista y pulse "Eliminar seleccionado"
##### Para actualizar la lista
Pulse "Actualizar Lista"
##### Para trasladar un preso
Seleccione un preso de la lista y pulse "Trasladar Preso". A continuación se mostrará una ventana en la que se podrá seleccionar la cárcel a la que se trasladará el preso.
### Salir de la aplicación
Al finalizar, en el menú principal, pulse "cerrar sesión". Esto le devolverá a al menu inicial.

## Para_ejecutar_los_test
Se proporciona el comando para ejecutar los test:
mvn test

Para ejecutar los test se debe primero ejecutar la ventana principal en pararelo e insertar como mínimo una cárcel y un preso. Esto también debe hacerse a la hora de ejecutar cualquier comando que ejecute los test (como mvn clean install -U).

