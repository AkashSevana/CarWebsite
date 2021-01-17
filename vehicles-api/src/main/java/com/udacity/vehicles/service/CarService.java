package com.udacity.vehicles.service;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
@Service
public class CarService {

    private final CarRepository repository;
    private final MapsClient mapsClient;
    private final PriceClient priceClient;

    public CarService(CarRepository repository,
                      MapsClient mapsClient,
                      PriceClient priceClient) {
        this.repository = repository;
        this.mapsClient = mapsClient;
        this.priceClient = priceClient;
    }

    /**
     * Gathers a list of all vehicles
     * @return a list of all vehicles in the CarRepository
     */
    public List<Car> list() {
        return repository.findAll()
                .stream()
                .map(car -> setLocationAndPrice(car.getId(), car))
                .collect(Collectors.toList());
    }

    /**
     * Gets car information by ID (or throws exception if non-existent)
     * @param id the ID number of the car to gather information on
     * @return the requested car's information, including location and price
     */
    public Car findById(Long id) {

        Car car = repository.findById(id).orElseThrow(CarNotFoundException::new);
        car = setLocationAndPrice(id, car);
        return car;
    }

    private Car setLocationAndPrice(Long id, Car car) {
        /**
         * Note: The car class file uses @transient, meaning you will need to call
         *   the pricing service each time to get the price.
         */
        String priceResponse = priceClient.getPrice(id);
        car.setPrice(priceResponse);

        /**
         * Note: The Location class file also uses @transient for the address,
         * meaning the Maps service needs to be called each time for the address.
         */
        Location carLocation = mapsClient.getAddress(car.getLocation());
        car.setLocation(carLocation);

        return car;
    }

    /**
     * Either creates or updates a vehicle, based on prior existence of car
     * @param car A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    public Car save(Car car) {
        if (car.getId() != null) {
            return repository.findById(car.getId())
                    .map(carToBeUpdated -> {
                        carToBeUpdated.setDetails(car.getDetails());
                        carToBeUpdated.setCondition(car.getCondition());
                        carToBeUpdated.setCreatedAt(car.getCreatedAt());
                        carToBeUpdated.setModifiedAt(LocalDateTime.now());
                        carToBeUpdated = setLocationAndPrice(car.getId(), carToBeUpdated);
                        return repository.save(carToBeUpdated);
                    }).orElseThrow(CarNotFoundException::new);
        }

        car.setCreatedAt(LocalDateTime.now());
        return repository.save(car);
    }

    /**
     * Deletes a given car by ID
     * @param id the ID number of the car to delete
     */
    public void delete(Long id) {

        Car car = repository.findById(id).orElseThrow(CarNotFoundException::new);
        repository.delete(car);

    }
}
