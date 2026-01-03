**Sofka Fintech – Prueba Técnica Full Stack**

Aplicación full stack para registrar y consultar transacciones, calculando comisión por política de negocio y visualizando un tablero con listado y gráfico por descripción.

**Tecnologías**
**Backend**

Java 21
Spring Boot (WebFlux)
R2DBC (Oracle)
Validación Jakarta (@Valid)
Server-Sent Events (SSE) para actualizaciones en tiempo real
Maven
Oracle Free (Docker)

**Frontend**

Angular 19 (standalone)
PrimeNG (UI: tabla, modal, botones, etc.)
Highcharts (gráfico por descripción)
RxJS

**Arquitectura**
**Backend (Spring Boot WebFlux)**

Se implementa una separación por capas orientada a Clean Architecture / Hexagonal:

**1) Capa API (entrada)**

Paquete: com.sofka.fintech.api
ControladorTransaccion: expone endpoints HTTP:
POST /api/transacciones registra una transacción
GET /api/transacciones lista transacciones
GET /api/transacciones/stream stream SSE de transacciones registradas

**Manejo de errores**
Paquete: com.sofka.fintech.api.errores
ManejadorGlobalErrores: centraliza errores de validación, negocio y errores generales.

**2) Capa Aplicación (casos de uso)**

Paquete: com.sofka.fintech.aplicacion
Interfaces de casos de uso:
CasoUsoRegistrarTransaccion
CasoUsoListarTransacciones

Paquete: com.sofka.fintech.aplicacion.implementacion
Implementaciones:
ServicioRegistrarTransaccionImpl: valida monto, calcula comisión, persiste y publica evento.
ServicioListarTransaccionesImpl: consulta repositorio y mapea a DTO de salida.

**3) Capa Dominio (reglas y modelos)**

Paquete: com.sofka.fintech.dominio
Transaccion: entidad de dominio.
PoliticaComision: contrato de regla de comisión.
PoliticaComisionPorUmbralImpl: regla:
2% si monto <= 10.000
5% si monto > 10.000

ExcepcionNegocio: error controlado de negocio.

**4) Puertos de salida (interfaces hacia infraestructura)**

Paquete: com.sofka.fintech.puertos.salida
RepositorioTransaccion: persistencia (guardar/listar).
PublicadorTransacciones: emisión de eventos para SSE (flujo() y publicar(...)).
La implementación concreta del repositorio y publicador se conecta a Oracle R2DBC y mantiene un flujo reactivo para tiempo real.

**Frontend (Angular 19 Standalone)**

Arquitectura basada en componentes standalone, con servicios para acceso a API:

**Estructura funcional
ServicioTransaccionesService: cliente HTTP para:**
registrar transacción
listar transacciones
escuchar SSE (tiempo real)

**TableroTransaccionesComponent:**

muestra tabla PrimeNG (listado)
botón “Registrar transacción” abre modal
mantiene lista en memoria y se actualiza con SSE

**FormularioTransaccionComponent:**
formulario reactivo con validación
registra y cierra modal según resultado

**GraficoDescripcionComponent (Highcharts):**
gráfico combinado:
columnas: monto total por descripción
línea: promedio por descripción
se recalcula cuando cambia la lista de transacciones
Errores en frontend

Interceptor HTTP para manejo uniforme de errores (mensajes y estados)

**Requisitos de ejecución local**

Docker Desktop
Java 21
Maven 3.9+
Node.js 20+ (recomendado)
Angular CLI 19 (npm i -g @angular/cli@19)
**Levantar Oracle**
En la ruta ..\BD, se encuentra el docker compose para levantar la base de datos oracle free, ejecutar:

<img width="243" height="229" alt="image" src="https://github.com/user-attachments/assets/c313fd72-2780-4ac9-b891-0b51378d89e7" />


**docker compose up -d**
**Datos de conexión**

* Host: localhost
* Puerto: 1521
* Servicio/PDB: FREEPDB1
* Usuario: SOFKA
* Contraseña: admin123

Ejecutar scripts contenido en la carpeta ..\BD (script.sql), el cual contiene el CREATE de la tabla constuida.

**Backend: ejecutar en local**
1. Configurar application.yml (ejemplo):
<img width="437" height="450" alt="image" src="https://github.com/user-attachments/assets/209b47b7-7177-466f-8f79-5bc97b8a5ee1" />

2. En la ruta del ms ..\sofka-fintech-ms Compilar y ejecutar:
   mvn clean test
   mvn spring-boot:run
   <img width="1111" height="434" alt="image" src="https://github.com/user-attachments/assets/1d44085b-8f67-4f5b-a8a6-5ddb5d562115" />

**Backend disponible en:**
http://localhost:8085
**Endpoints:**
POST http://localhost:8085/api/transacciones
GET http://localhost:8085/api/transacciones
GET http://localhost:8085/api/transacciones/stream (SSE)

**Frontend: ejecutar en local**
1. En la ruta ..\sofka-fintech-front ejecutar:
   npm install
   ng serve -o
   
**Frontend disponible en:**
http://localhost:4200


**Demo aplicación:**
<img width="1895" height="985" alt="image" src="https://github.com/user-attachments/assets/0aa0f416-f0ea-48fa-a695-c97a67abe0b8" />
Registrar Tranbsacción:
<img width="1905" height="988" alt="image" src="https://github.com/user-attachments/assets/b41f9794-e586-4e90-af9b-489272f31c58" />
Datos en timepo real:
https://github.com/user-attachments/assets/e7876480-3a5f-4a9d-949d-589aa459abc5

Gracias.














