# Casos de Uso

```plantuml
@startuml "casos-de-uso"
left to right direction
skinparam packageStyle rectangle

actor deportista
rectangle CycleTrainer {
  deportista -- (Iniciar trayecto)
  deportista -- (Agregar ubicación al Trayecto)
  deportista -- (Finalizar Trayecto)
  deportista -- (Consultar Trayecto)
  deportista -- (Consultar todos los Trayectos)
}

@enduml
```

---

* [CU001 - Iniciar Trayecto](CU001-iniciar-trayecto.md)
* [CU002 - Registrar ubicación durante un trayecto](CU002-registrar-ubicacion.md)
* [CU003 - Finalizar Trayecto](CU003-finalizar-trayecto.md)
* [CU004 - Consultar estadísticas del trayecto](CU004-consultar-trayecto.md)
* [CU005- Consultar resumen de todos los trayectos](CU005-consultar-resumen-trayectos.md)