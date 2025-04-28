# Diagrama de clases -- Diseño

```uml
@startuml

class RestTrayectos {
  POST /trayectos/iniciar
  POST /trayectos/ubicaciones
  POST /trayectos/terminar
  GET /trayectos/{id}
  GET /trayectos?{fechaini}&{fechafin}
}

class ServicioTrayectos {
  iniciarTrayecto(long: Double, lat: Double) : UUID
  registrarUbicacionTrayecto(id: UUID, long: Double, lat: Double): void
  finalizarTrayecto(id: UUID, long: Double, lat: Double): void
  consultarTrayecto(id: UUID): Trayecto
  consultarTrayectosPorFecha(inicio: Date, final: Date): List<Trayecto>
}

RestTrayectos ..d..> ServicioTrayectos

class RepositorioTrayectos { }
class RepositorioUbicaciones { }

class Trayecto { }
class Ubicacion { }

Trayecto "1" *-d-> "*" Ubicacion

ServicioTrayectos ..r..> RepositorioTrayectos
ServicioTrayectos ..r..> RepositorioUbicaciones

ServicioTrayectos ..r..> Trayecto
ServicioTrayectos ..r..> Ubicacion

RepositorioTrayectos   ..r..> Trayecto
RepositorioUbicaciones ..r..> Ubicacion

RepositorioTrayectos -[hidden]d- RepositorioUbicaciones
Trayecto             -[hidden]d- Ubicacion

@enduml
```

---


Clases:
* **RestTrayectos:** Controlador que atiende las peticiones REST de la aplicación.
* **ServicioTrayectos:** Servicio que implementa los casos de uso de la aplicación.
* **RepositorioTrayectos:** Acceso a los datos de los Trayectos
* **RepositorioUbicaciones:** Acceso a los datos de las Ubicaciones


