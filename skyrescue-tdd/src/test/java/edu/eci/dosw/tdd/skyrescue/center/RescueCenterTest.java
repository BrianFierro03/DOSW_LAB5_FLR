package edu.eci.dosw.tdd.skyrescue.center;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.eci.dosw.tdd.skyrescue.drone.Drone;
import edu.eci.dosw.tdd.skyrescue.mission.Mission;
import edu.eci.dosw.tdd.skyrescue.mission.MissionStatus;
import edu.eci.dosw.tdd.skyrescue.operator.RescueOperator;

public class RescueCenterTest {
    private RescueCenter rescueCenter;
    private Drone drone;
    private Mission mission;
    private RescueOperator operator;
  
    @BeforeEach 
    public void setup(){
        rescueCenter = new RescueCenter(); 
        drone = new Drone("1030", "Mk", 30);
        operator = new RescueOperator("2025", "Rudencio");
    }

    // Operador y dron válidos, distancia permitida
    @Test 
    void ShouldCreateMissionCorrectInput(){
        rescueCenter.addDrone(drone);
        rescueCenter.addOperator(operator);
        
        mission = rescueCenter.assignMission("2025", "1030", "popayan", 20);
        
        assertNotNull(mission, "La misión debería ser creada"); 
        assertEquals(MissionStatus.ACTIVE, mission.getStatus(), "La misión debe estar activa");
        assertFalse(drone.isAvailable(), "El dron debe estar no disponible");
    }

    // Dron inexistente
    @Test 
    public void ShouldCreateAnErrorDroneDontExist(){
        rescueCenter.addOperator(operator);
        assertThrows(IllegalArgumentException.class, () -> {
            rescueCenter.assignMission("2025", "DONT_EXIST", "popayan", 20);
        });
    }
    
    // Dron ya ocupado
    @Test 
    public void ShouldUseADroneItHaveBusiness(){
        drone.setAvailable(false); 
        rescueCenter.addDrone(drone);
        rescueCenter.addOperator(operator);

        assertThrows(IllegalStateException.class, () -> {
            rescueCenter.assignMission("2025", "1030", "popayan", 20);
        });
    }

    // Distancia superior a la autonomía
    @Test
    public void ShouldThrowAnErrorDistanceExceedsMaxRange() {
        rescueCenter.addDrone(drone);
        rescueCenter.addOperator(operator);

        assertThrows(IllegalArgumentException.class, () -> {
            rescueCenter.assignMission("2025", "1030", "popayan", 50);
        });
    }

    // Operador inexistente
    @Test
    public void ShouldCreateAnErrorOperatorDontExist() {
        rescueCenter.addDrone(drone);

        assertThrows(IllegalArgumentException.class, () -> {
            rescueCenter.assignMission("ID_FALSO_777", "1030", "popayan", 20);
        });
    }

    // Operador con otra misión activa
    @Test
    public void ShouldThrowAnErrorOperatorAlreadyHasAnActiveMission() {
        rescueCenter.addOperator(operator);
        rescueCenter.addDrone(drone); 
        
        Drone segundoDrone = new Drone("1031", "Mk-II", 40);
        rescueCenter.addDrone(segundoDrone); 

        rescueCenter.assignMission("2025", "1030", "popayan", 20);

        assertThrows(IllegalStateException.class, () -> {
            rescueCenter.assignMission("2025", "1031", "cali", 20);
        });
    }
    
    //Cerrar una misión activa
    @Test
    public void CloseAnActiveMission(){
        rescueCenter.addDrone(drone);
        rescueCenter.addOperator(operator);
        Mission activeMission = rescueCenter.assignMission("2025", "1030", "popayan", 20);
        Mission completedMission = rescueCenter.completeMission(activeMission.getId());
        assertEquals(MissionStatus.COMPLETED, completedMission.getStatus(), "El estado debe ser COMPLETED");
        assertNotNull(completedMission.getEndDate(), "La fecha de cierre no debe ser nula");
        assertTrue(drone.isAvailable(), "El dron asociado debe volver a estar disponible");
    }
    //Mision no existe
    @Test 
    public void ShouldCreateAnErrorMissionDontExist(){
        assertThrows(IllegalArgumentException.class, () -> {
            rescueCenter.completeMission("ID_INVENTADO_999");
        }, "Debe lanzar IllegalArgumentException al intentar cerrar una misión que  no existe");

    }
    //Mision 2 a la vez 
    @Test
     public void ShouldCreateAnErrorCloseTwoTimesMission(){
        rescueCenter.addDrone(drone);
        rescueCenter.addOperator(operator);
        Mission activeMission = rescueCenter.assignMission("2025", "1030", "popayan", 20);
        rescueCenter.completeMission(activeMission.getId());
        assertThrows(IllegalStateException.class, () -> {
            rescueCenter.completeMission(activeMission.getId());
        }, "Debe lanzar IllegalStateException al intentar cerrar una misión ya completada");
     }
     // debe crear otra mision
     @Test 
     public void ShouldCreateAnotherMission(){
        rescueCenter.addDrone(drone);
        rescueCenter.addOperator(operator);
        Mission mission1 = rescueCenter.assignMission("2025", "1030", "popayan", 20);
        Drone drone2 = new Drone("1031", "Mk-II", 40);
        RescueOperator operator2 = new RescueOperator("2026", "Jacinta");
        rescueCenter.addDrone(drone2);
        rescueCenter.addOperator(operator2);
        Mission mission2 = rescueCenter.assignMission("2026", "1031", "cali", 25);
        rescueCenter.completeMission(mission1.getId());
        assertEquals(MissionStatus.ACTIVE, mission2.getStatus(), "La otra misión debe conservar su estado ACTIVE");
        assertFalse(drone2.isAvailable(), "El dron de la otra misión debe seguir ocupado");
    }
     



    //Registrar un Dron valido
    @Test 
    public void shouldRegisterValidDrone(){
        assertTrue(rescueCenter.addDrone(drone));
    }

    //Registrar null
    @Test 
    public void shouldNotRegisterANullDrone(){
        assertFalse(rescueCenter.addDrone(null));
    }

    //Registrar con id vacío
    @Test 
    public void shouldNotRegisterDroneWithBlankId(){
        Drone droneFalse = new Drone("","Mk",30);
        Drone droneFalse2 = new Drone(null,"Mk",30);
        assertFalse(rescueCenter.addDrone(droneFalse));
        assertFalse(rescueCenter.addDrone(droneFalse2));
    }

    //Registrar dos ids iguales
    @Test 
    public void shouldNotRegisterDronesWithSameNames(){
        Drone second = new Drone("1030", "Mk", 30);
        assertTrue(rescueCenter.addDrone(drone));
        assertFalse(rescueCenter.addDrone(second));
    }
}
