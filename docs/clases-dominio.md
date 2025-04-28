# Diagrama de clases -- Dominio


```uml
@startuml clases-dominio

class Trayecto {
  + UUID id  
  + Date horaInicio
  + Date horaFin
  + Long duracion
  + Boolean enProceso
  }

  class Ubicacion {
  + UUID id
  + Date hora
  + Long longitud
  + Long latitud 
  }

  Trayecto *-- "0..*" Ubicacion : ubicaciones	


@enduml
```

---

Clases:
* **Trayecto:** Cada uno de los trayectos de entrenamiento del usuario.
* **Ubicacion:** Secuencia de ubicaciones (con longitud y latitud) que se obtienen durante un trayecto específico.
