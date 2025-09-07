package com.cartechindia.service;

import com.cartechindia.dto.CarSellingDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CarSellingService {
    CarSellingDto addCar(CarSellingDto dto);
    Page<CarSellingDto> getAllCars(int page, int size);

}
