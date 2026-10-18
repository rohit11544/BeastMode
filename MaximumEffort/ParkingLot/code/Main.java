import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Parking Lot — ONE FILE
 * Patterns: Facade + Strategy (pricing) + Factory (spots) + Observer (display)
 *
 *   javac Main.java && java Main
 */

// ========== DOMAIN ==========

enum VehicleType {
    MOTORCYCLE,
    CAR,
    TRUCK
}

enum SpotType {
    MOTORCYCLE,
    COMPACT,
    LARGE
}

class Vehicle {
    private final String plate;
    private final VehicleType type;

    Vehicle(String plate, VehicleType type) {
        this.plate = plate;
        this.type = type;
    }

    String getPlate() { return plate; }
    VehicleType getType() { return type; }
}

class ParkingSpot {
    private final String id;
    private final int floor;
    private final SpotType type;
    private Vehicle occupant;

    ParkingSpot(String id, int floor, SpotType type) {
        this.id = id;
        this.floor = floor;
        this.type = type;
    }

    String getId() { return id; }
    int getFloor() { return floor; }
    SpotType getType() { return type; }
    boolean isFree() { return occupant == null; }

    void park(Vehicle vehicle) { this.occupant = vehicle; }
    void leave() { this.occupant = null; }
}

class Ticket {
    private final String id;
    private final Vehicle vehicle;
    private final ParkingSpot spot;
    private final LocalDateTime inTime;

    Ticket(Vehicle vehicle, ParkingSpot spot, LocalDateTime inTime) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.vehicle = vehicle;
        this.spot = spot;
        this.inTime = inTime;
    }

    String getId() { return id; }
    Vehicle getVehicle() { return vehicle; }
    ParkingSpot getSpot() { return spot; }
    LocalDateTime getInTime() { return inTime; }
}

// ========== FACTORY ==========

class SpotFactory {
    static ParkingSpot create(int floor, int index, SpotType type) {
        return new ParkingSpot("F" + floor + "-" + type + "-" + index, floor, type);
    }
}

// ========== PRICING (STRATEGY) ==========

interface PricingStrategy {
    double calculate(Ticket ticket, LocalDateTime outTime);
}

class HourlyPricing implements PricingStrategy {
    private final double ratePerHour;

    HourlyPricing(double ratePerHour) {
        this.ratePerHour = ratePerHour;
    }

    @Override
    public double calculate(Ticket ticket, LocalDateTime outTime) {
        long minutes = Duration.between(ticket.getInTime(), outTime).toMinutes();
        long hours = Math.max(1, (minutes + 59) / 60); // bill at least 1 hour, round up
        return hours * ratePerHour;
    }
}

class FlatRatePricing implements PricingStrategy {
    private final double flatFee;

    FlatRatePricing(double flatFee) {
        this.flatFee = flatFee;
    }

    @Override
    public double calculate(Ticket ticket, LocalDateTime outTime) {
        return flatFee;
    }
}

// ========== OBSERVER ==========

interface OccupancyObserver {
    void onOccupancyChanged(int occupied, int capacity);
}

class DisplayBoard implements OccupancyObserver {
    private String lastMessage = "";

    @Override
    public void onOccupancyChanged(int occupied, int capacity) {
        if (occupied >= capacity) {
            lastMessage = "FULL (" + occupied + "/" + capacity + ")";
        } else {
            lastMessage = "OPEN " + occupied + "/" + capacity;
        }
        System.out.println("[BOARD] " + lastMessage);
    }

    String getLastMessage() { return lastMessage; }
}

// ========== FLOOR (FREE QUEUES) ==========

class Floor {
    private final int number;
    private final Map<SpotType, Queue<ParkingSpot>> freeByType = new HashMap<>();

    Floor(int number) {
        this.number = number;
        for (SpotType type : SpotType.values()) {
            freeByType.put(type, new ArrayDeque<>());
        }
    }

    int getNumber() { return number; }

    void addSpot(ParkingSpot spot) {
        freeByType.get(spot.getType()).offer(spot);
    }

    ParkingSpot takeFree(SpotType type) {
        return freeByType.get(type).poll();
    }

    void returnSpot(ParkingSpot spot) {
        freeByType.get(spot.getType()).offer(spot);
    }
}

// ========== FACADE ==========

class ParkingLotFacade {
    private final List<Floor> floors = new ArrayList<>();
    private final Map<String, Ticket> openTickets = new HashMap<>();
    private final List<OccupancyObserver> observers = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();

    private PricingStrategy pricing;
    private final int capacity;
    private int occupied;

    ParkingLotFacade(int floorCount, int motorcyclesPerFloor, int compactPerFloor,
                     int largePerFloor, PricingStrategy pricing) {
        this.pricing = pricing;
        int total = 0;

        for (int f = 1; f <= floorCount; f++) {
            Floor floor = new Floor(f);
            for (int i = 1; i <= motorcyclesPerFloor; i++) {
                floor.addSpot(SpotFactory.create(f, i, SpotType.MOTORCYCLE));
            }
            for (int i = 1; i <= compactPerFloor; i++) {
                floor.addSpot(SpotFactory.create(f, i, SpotType.COMPACT));
            }
            for (int i = 1; i <= largePerFloor; i++) {
                floor.addSpot(SpotFactory.create(f, i, SpotType.LARGE));
            }
            floors.add(floor);
            total += motorcyclesPerFloor + compactPerFloor + largePerFloor;
        }

        this.capacity = total;
        this.occupied = 0;
    }

    void addObserver(OccupancyObserver observer) {
        observers.add(observer);
        observer.onOccupancyChanged(occupied, capacity);
    }

    void setPricing(PricingStrategy pricing) {
        this.pricing = pricing;
    }

    /** Preferred spot types, smallest first. */
    private List<SpotType> fitOrder(VehicleType vehicleType) {
        List<SpotType> order = new ArrayList<>();
        switch (vehicleType) {
            case MOTORCYCLE:
                order.add(SpotType.MOTORCYCLE);
                order.add(SpotType.COMPACT);
                order.add(SpotType.LARGE);
                break;
            case CAR:
                order.add(SpotType.COMPACT);
                order.add(SpotType.LARGE);
                break;
            case TRUCK:
                order.add(SpotType.LARGE);
                break;
        }
        return order;
    }

    private ParkingSpot findSpot(VehicleType vehicleType) {
        List<SpotType> order = fitOrder(vehicleType);
        for (Floor floor : floors) {
            for (SpotType type : order) {
                ParkingSpot spot = floor.takeFree(type);
                if (spot != null) {
                    return spot;
                }
            }
        }
        return null;
    }

    Ticket park(Vehicle vehicle, LocalDateTime inTime) {
        lock.lock();
        try {
            ParkingSpot spot = findSpot(vehicle.getType());
            if (spot == null) {
                System.out.println("FULL — cannot park " + vehicle.getPlate());
                return null;
            }

            spot.park(vehicle);
            occupied++;
            Ticket ticket = new Ticket(vehicle, spot, inTime);
            openTickets.put(ticket.getId(), ticket);
            notifyObservers();

            System.out.println("PARK  " + vehicle.getPlate() + " → " + spot.getId()
                    + " ticket=" + ticket.getId());
            return ticket;
        } finally {
            lock.unlock();
        }
    }

    double unpark(String ticketId, LocalDateTime outTime) {
        lock.lock();
        try {
            Ticket ticket = openTickets.remove(ticketId);
            if (ticket == null) {
                throw new IllegalArgumentException("Unknown ticket: " + ticketId);
            }

            double fee = pricing.calculate(ticket, outTime);
            ParkingSpot spot = ticket.getSpot();
            spot.leave();
            floors.get(spot.getFloor() - 1).returnSpot(spot);
            occupied--;
            notifyObservers();

            System.out.println("EXIT  " + ticket.getVehicle().getPlate()
                    + " fee=" + fee + " from " + spot.getId());
            return fee;
        } finally {
            lock.unlock();
        }
    }

    int getOccupied() { return occupied; }
    int getCapacity() { return capacity; }

    private void notifyObservers() {
        for (OccupancyObserver observer : observers) {
            observer.onOccupancyChanged(occupied, capacity);
        }
    }
}

// ========== DEMO ==========

public class Main {
    public static void main(String[] args) {
        DisplayBoard board = new DisplayBoard();
        ParkingLotFacade lot = new ParkingLotFacade(
                1, 1, 1, 1, new HourlyPricing(10.0));
        lot.addObserver(board);

        LocalDateTime t0 = LocalDateTime.of(2026, 8, 19, 10, 0);

        Vehicle bike = new Vehicle("BIKE-1", VehicleType.MOTORCYCLE);
        Vehicle car = new Vehicle("CAR-1", VehicleType.CAR);
        Vehicle truck = new Vehicle("TRUCK-1", VehicleType.TRUCK);
        Vehicle extraCar = new Vehicle("CAR-2", VehicleType.CAR);

        Ticket bikeTicket = lot.park(bike, t0);
        Ticket carTicket = lot.park(car, t0);
        Ticket truckTicket = lot.park(truck, t0);

        // Compact + large taken → extra car fails until a car-sized spot frees.
        lot.park(extraCar, t0);

        double bikeFee = lot.unpark(bikeTicket.getId(), t0.plusMinutes(30)); // 1 hour min
        System.out.println("  billed hours(min 1): " + bikeFee);

        lot.unpark(carTicket.getId(), t0.plusHours(2));
        Ticket car2 = lot.park(extraCar, t0.plusHours(2));

        lot.setPricing(new FlatRatePricing(50.0));
        double truckFee = lot.unpark(truckTicket.getId(), t0.plusHours(5));
        System.out.println("  flat fee: " + truckFee);

        lot.unpark(car2.getId(), t0.plusHours(3));

        System.out.println("done occupied=" + lot.getOccupied() + "/" + lot.getCapacity());
    }
}
