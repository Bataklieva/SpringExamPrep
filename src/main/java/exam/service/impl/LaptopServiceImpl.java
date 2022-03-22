package exam.service.impl;

import com.google.gson.Gson;
import exam.model.dto.json.LaptopSeedDto;
import exam.model.entity.Laptop;
import exam.model.entity.Warranty;
import exam.repository.LaptopRepository;
import exam.repository.ShopRepository;
import exam.service.LaptopService;
import exam.util.ValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

@Service
public class LaptopServiceImpl implements LaptopService {
    private static final String LAPTOP_PATH = "/Users/betinabataklieva/Downloads/LaptopShop/src/main/resources/files/json/laptops.json";

    private final LaptopRepository laptopRepository;
    private final ShopRepository shopRepository;

    private final ModelMapper mapper;
    private final ValidationUtil validationUtil;
    private final Gson gson;


    @Autowired
    public LaptopServiceImpl(LaptopRepository laptopRepository, ShopRepository shopRepository,
                             ModelMapper mapper, ValidationUtil validationUtil, Gson gson) {

        this.laptopRepository = laptopRepository;
        this.shopRepository = shopRepository;
        this.mapper = mapper;
        this.validationUtil = validationUtil;
        this.gson = gson;
    }

    @Override
    public boolean areImported() {
        return laptopRepository.count() > 0;
    }

    @Override
    public String readLaptopsFileContent() throws IOException {
        return Files.readString(Path.of(LAPTOP_PATH));
    }

    @Override
    public String importLaptops() throws IOException {

        StringBuilder builder = new StringBuilder();

        LaptopSeedDto[] laptopSeedDtos = gson.fromJson(readLaptopsFileContent(), LaptopSeedDto[].class);

        Arrays.stream(laptopSeedDtos)
                .filter(laptopSeedDto -> {

                    boolean isValid = validationUtil.isValid(laptopSeedDto)
                            && addressExisting(laptopSeedDto.getMacAddress());

                    builder
                            .append(isValid
                            ? String.format("Successfully imported Laptop %s - %.2f - %d - %d",
                                    laptopSeedDto.getMacAddress(), laptopSeedDto.getCpuSpeed(),
                                    laptopSeedDto.getRam(), laptopSeedDto.getStorage())
                            : "Invalid Laptop")
                            .append(System.lineSeparator());

                    return  isValid;
                })
                .map(toLaptop -> {
                    Laptop laptop = mapper.map(toLaptop, Laptop.class);

                    laptop.setShops(shopRepository.getByName
                            (toLaptop.getShop().getName()).orElse(null));

                    return laptop;

                }).forEach(laptopRepository::save);

        return builder.toString();
    }

    @Override
    public String exportBestLaptops() {
        StringBuilder export = new StringBuilder();

        laptopRepository.findBestLaptops().forEach(laptop -> export.append(String.format
                ("""
                  Laptop - %s
                  *Cpu speed - %.2f
                  **Ram - %d
                  ***Storage - %d
                  ****Price - %.2f
                  #Shop name - %s
                  ##Town - %s
                                      
                        """, laptop.getMacAddress(), laptop.getCpuSpeed(),
                        laptop.getRam(), laptop.getStorage(), laptop.getPrice(),
                        laptop.getShops().getName(), laptop.getShops().getTown().getName())));


        return export.toString();
    }

    private boolean addressExisting(String macAddress) {

        Optional<Laptop> existingLaptop =
                laptopRepository.findByMacAddress(macAddress);

        return existingLaptop.isEmpty();
    }
}
