package com.bicicletas.trayectos.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trayecto {

    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    UUID id;

    @Temporal(TemporalType.TIMESTAMP)
    LocalDateTime fechaHoraInicio;

    @Temporal(TemporalType.TIMESTAMP)
    LocalDateTime fechaHoraFin;

    Long duracion;

    boolean enProceso = false;

    @OneToMany(mappedBy = "trayecto", cascade = CascadeType.ALL)
    List<Ubicacion> ubicaciones = new ArrayList<>();

    // Calcula la distancia total recorrida usando la fórmula de Haversine
    public double calcularDistanciaTotal() {
        double distanciaTotal = 0;
        
        for (int i = 1; i < ubicaciones.size(); i++) {
            Ubicacion anterior = ubicaciones.get(i-1);
            Ubicacion actual = ubicaciones.get(i);
            distanciaTotal += calcularDistancia(
                anterior.getLatitud(), anterior.getLongitud(),
                actual.getLatitud(), actual.getLongitud()
            );
        }
        
        return distanciaTotal;
    }

    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radio de la Tierra en kilómetros

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c; // Distancia en kilómetros
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Trayecto:\n");
        sb.append("  Fecha/hora inicio: ").append(fechaHoraInicio).append("\n");
        if (ubicaciones.size() > 0) {
            Ubicacion inicial = ubicaciones.get(0);
            sb.append("  Ubicación inicial: (").append(inicial.getLongitud())
              .append(", ").append(inicial.getLatitud()).append(")\n");
        }
        sb.append("  Ubicaciones registradas:\n");
        for (Ubicacion u : ubicaciones) {
            sb.append("    ").append(u.getFechaHora())
              .append(": (").append(u.getLongitud())
              .append(", ").append(u.getLatitud()).append(")\n");
        }
        if (!enProceso && fechaHoraFin != null) {
            sb.append("  Fecha/hora fin: ").append(fechaHoraFin).append("\n");
            sb.append("  Duración: ").append(duracion).append(" segundos\n");
            sb.append("  Distancia total: ").append(String.format("%.2f", calcularDistanciaTotal())).append(" km\n");
        }
        return sb.toString();
    }
}
