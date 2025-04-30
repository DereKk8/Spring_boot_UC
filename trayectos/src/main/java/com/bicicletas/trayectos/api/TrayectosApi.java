package com.bicicletas.trayectos.api;

import org.springframework.web.bind.annotation.RestController;

import com.bicicletas.trayectos.dataAccess.TrayectosRepository;
import com.bicicletas.trayectos.logica.TrayectosService;
import com.bicicletas.trayectos.modelo.Trayecto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;



@RestController
public class TrayectosApi {

    @Autowired
    TrayectosService trayectosService;

    @Autowired
    TrayectosRepository trayectosRepository;

    @GetMapping("/hola")
     public String hola() {
        return "Hola mundo";
     }

     @GetMapping("/trayectos")
     public List<Trayecto> getTrayectos() {
        return trayectosRepository.findAll();
     }

     @PostMapping("/trayectos")
     public UUID iniciarTrayecto(iniciarTrayectoDTO ubicacion) throws Exception {
        return trayectosService.iniciarTrayecto(ubicacion.getLongitud(), ubicacion.getLatitud());
     }  



    


}

@Data
@NoArgsConstructor
class iniciarTrayectoDTO {
    Double longitud;
    Double latitud;
}
