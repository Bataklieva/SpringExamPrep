package exam.service.impl;

import exam.model.dto.xml.ShopSeedRootDto;
import exam.model.entity.Shop;
import exam.repository.ShopRepository;
import exam.repository.TownRepository;
import exam.service.ShopService;
import exam.util.ValidationUtil;
import exam.util.XmlParser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class ShopServiceImpl implements ShopService {

    private static final String SHOP_PATH = "/Users/betinabataklieva/Downloads/LaptopShop/src/main/resources/files/xml/shops.xml";

    private final ShopRepository shopRepository;
    private final TownRepository townRepository;

    private final ModelMapper mapper;
    private final ValidationUtil validationUtil;
    private final XmlParser xmlParser;

    @Autowired
    public ShopServiceImpl(ShopRepository shopRepository, TownRepository townRepository, ModelMapper mapper,
                           ValidationUtil validationUtil, XmlParser xmlParser) {

        this.shopRepository = shopRepository;
        this.townRepository = townRepository;
        this.mapper = mapper;
        this.validationUtil = validationUtil;
        this.xmlParser = xmlParser;
    }

    @Override
    public boolean areImported() {
        return shopRepository.count() > 0;
    }

    @Override
    public String readShopsFileContent() throws IOException {
        return Files.readString(Path.of(SHOP_PATH));
    }

    @Override
    public String importShops() throws JAXBException, FileNotFoundException {
        StringBuilder builder = new StringBuilder();

        ShopSeedRootDto shopDto = xmlParser.fromFile(SHOP_PATH, ShopSeedRootDto.class);

        shopDto.getShops().stream()
                .filter(shopSeedDto -> {

                    boolean isValid = validationUtil.isValid(shopSeedDto)
                            && !shopExists(shopSeedDto.getName());

                    builder
                            .append(isValid ? String.format("Successfully imported Shop %s - %.0f",
                                    shopSeedDto.getName(), shopSeedDto.getIncome())
                                    : "Invalid shop")
                            .append(System.lineSeparator());

                    return  isValid;
                })
                .map(shops -> {
                    Shop mappedShop = mapper.map(shops, Shop.class);

                    mappedShop.setTown(townRepository
                            .findByName(shops.getTown().getName())
                            .orElse(null));

                    return mappedShop;

                })
                .forEach(shopRepository::save);

        return builder.toString();
    }

    private boolean shopExists(String name) {

        return shopRepository.existsByName(name);
    }

}
