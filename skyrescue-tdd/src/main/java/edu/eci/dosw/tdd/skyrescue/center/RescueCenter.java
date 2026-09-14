package edu.eci.dosw.tdd.skyrescue.center;

import edu.eci.dosw.tdd.skyrescue.drone.Drone;
import edu.eci.dosw.tdd.skyrescue.mission.Mission;
import edu.eci.dosw.tdd.skyrescue.mission.MissionStatus;
import edu.eci.dosw.tdd.skyrescue.operator.RescueOperator;

import java.nio.channels.SelectableChannel;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.ranges.DocumentRange;

/**
 * Coordinates drones, operators and emergency missions.
 */
public class RescueCenter {

    private final List<RescueOperator> operators;
    private final Map<String, Drone> drones;
    private final List<Mission> missions;

    public RescueCenter() {
        this.operators = new ArrayList<>();
        this.drones = new HashMap<>();
        this.missions = new ArrayList<>();
    }

    /**
     * Registers a drone in the rescue center.
     *
     * Rules:
     * - The drone cannot be null.
     * - The drone id cannot be null or blank.
     * - Two drones cannot have the same id.
     * - A valid drone is stored as available.
     *
     * @param drone drone to register.
     * @return true if it was registered; false otherwise.
     */
    public boolean addDrone(Drone drone) {
        // TODO Implement using TDD.
        return false;
    }

    /**
     * Assigns an emergency mission to an operator and an available drone.
     *
     * Rules:
     * - operatorId, droneId and location must be valid.
     * - The operator must exist.
     * - The drone must exist and be available.
     * - distanceKm must be greater than zero.
     * - distanceKm cannot exceed the drone maxRangeKm.
     * - The same operator cannot have two ACTIVE missions.
     * - On success, create an ACTIVE mission with the current date.
     * - On success, the selected drone becomes unavailable.
     * - The created mission must be stored in the center.
     *
     * Suggested error policy:
     * - Invalid/nonexistent data -> IllegalArgumentException.
     * - Valid resource but invalid state -> IllegalStateException.
     *
     * @param operatorId operator identifier.
     * @param droneId drone identifier.
     * @param location emergency location description.
     * @param distanceKm mission distance in kilometers.
     * @return created mission.
     */
    public Mission assignMission(
            String operatorId,
            String droneId,
            String location,
            int distanceKm) {
        RescueOperator selectedOperator =null;
        for (RescueOperator op : operators){
            if(op.getId().equals(operatorId)){
                selectedOperator = op;
                break;
            }
        }
        if(selectedOperator == null){
            throw new IllegalArgumentException("El operador no exite.");
        }
        if (!drones.containsKey(droneId)){
            throw new IllegalArgumentException("El dron no existe.");
        }
        Drone SelectedDrone =drones.get(droneId);
        if (!SelectedDrone.isAvailable()){
            throw new IllegalStateException("El dron no está disponible.");
        }
        if(distanceKm<=0|| distanceKm > SelectedDrone.getMaxRangeKm()){
            throw new IllegalArgumentException("La distancia es inválida o excede la autonomía del dron.");
        }
        for (Mission m : missions) {
            if (m.getOperator().getId().equals(operatorId) && m.getStatus() == MissionStatus.ACTIVE) {
                throw new IllegalStateException("El operador ya tiene una misión activa.");
            }
        }

        String generatedMissionId = "M-" + (missions.size() + 1);
        
        Mission newMission = new Mission(
                generatedMissionId,
                location,
                distanceKm,
                SelectedDrone,
                selectedOperator,
                LocalDateTime.now(), 
                MissionStatus.ACTIVE
        );
        SelectedDrone.setAvailable(false);
        missions.add(newMission);

        return newMission;
        
    }

    /**
     * Completes an active mission.
     *
     * Rules:
     * - missionId must be valid.
     * - The mission must exist.
     * - An already COMPLETED mission cannot be completed again.
     * - The mission status changes to COMPLETED.
     * - The end date is the current date/time.
     * - The drone assigned to the mission becomes available again.
     *
     * Suggested error policy:
     * - Invalid/nonexistent mission -> IllegalArgumentException.
     * - Mission already completed -> IllegalStateException.
     *
     * @param missionId mission identifier.
     * @return completed mission.
     */
    public Mission completeMission(String missionId) {
        if (missionId == null || missionId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de la misión no puede ser nulo o vacío.");
        }

        Mission foundMission = null;
        for (Mission m : missions) {
            if (m.getId().equals(missionId)) {
                foundMission = m;
                break;
            }
        }

        if (foundMission == null) {
            throw new IllegalArgumentException("La misión no existe.");
        }
        if (foundMission.getStatus() == MissionStatus.COMPLETED) {
            throw new IllegalStateException("La misión ya fue completada.");
        }
        foundMission.setStatus(MissionStatus.COMPLETED);
        foundMission.setEndDate(LocalDateTime.now());
        foundMission.getDrone().setAvailable(true);

        return foundMission;
    }

    public boolean addOperator(RescueOperator operator) {
        return operators.add(operator);
    }
}