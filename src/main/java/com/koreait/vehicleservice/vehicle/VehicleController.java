package com.koreait.vehicleservice.vehicle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("vehicle")
public class VehicleController {
<<<<<<< HEAD
    @Autowired
    public VehicleService service;

=======
@Autowired private VehicleService service;
>>>>>>> 8c3d634b58c777243b6f702860f9f57c2d871ab0
    @GetMapping("/list")
    public void list() {}

    @GetMapping("/list/for")
    public void listfor(){}

    @GetMapping("/detail")
    public void detail(){

    }

    @GetMapping("/write")
    public void write(){

    }

    @PostMapping("/write")
    public String inVehicle(VehicleDto dto, @RequestParam MultipartFile mainimg){
        service.inVehicle(dto);
        String fileNm = service.uploadMainImg(mainimg);
            return "redirect:list";
    }
}
