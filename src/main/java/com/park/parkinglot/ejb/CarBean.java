/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.park.parkinglot.ejb;

import com.park.parkinglot.common.CarDetails;
import com.park.parkinglot.entity.User;
import com.park.parkinglot.entity.car;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Alex
 */
@Stateless
public class CarBean {

    private static final Logger LOG = Logger.getLogger(CarBean.class.getName());
    @PersistenceContext
    private EntityManager em;
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public CarDetails findById(Integer carId){
        car car = em.find(car.class, carId);
        return new CarDetails(car.getId(), car.getLicensePlate(), car.getParkingSpot(), car.getUser().getUsername());
    
    }
    public List<CarDetails> getAllCars() {
        LOG.info("getAllCars");
        try {
            Query query = em.createQuery("SELECT c FROM car c");
            List<car> cars = (List<car>) query.getResultList();
            return copyCarsToDetails(cars);
        } catch (Exception ex) {
            throw new EJBException(ex);
        }
    }

    private List<CarDetails> copyCarsToDetails(List<car> cars) {

        List<CarDetails> detailsList = new ArrayList<>();
        for (car Car : cars) {
            CarDetails carDetails = new CarDetails(Car.getId(),
                    Car.getLicensePlate(),
                    Car.getParkingSpot(),
                    Car.getUser().getUsername());
            detailsList.add(carDetails);
        }
        return detailsList;
    }
    
    public void createCar(String licensePlate, String parkingSpot, Integer userId){
        LOG.info("createCar");
        car car = new car();
        car.setLicensePlate(licensePlate);
        car.setParkingSpot(parkingSpot);
    
        User user = em.find(User.class, userId);
        user.getCars().add(car);
        car.setUser(user);
        
        em.persist(car);
    
    }

    public void updateCar(Integer carId, String licensePlate, String parkingSpot, Integer userId) {
        LOG.info("updateCar");
        car car = em.find(car.class, carId);
        car.setLicensePlate(licensePlate);;
        car.setParkingSpot(parkingSpot);
        
        User oldUser = car.getUser();
        oldUser.getCars().remove(car);
        
        User user = em.find(User.class, userId);
        user.getCars().add(car);
        car.setUser(user);
    }

    public void deleteCarsByIds(Collection<Integer> ids) {
        LOG.info("deleteCarsByIds");
        for(Integer id : ids){
            car car = em.find(car.class, id);
            em.remove(car);
        }
    }
}
