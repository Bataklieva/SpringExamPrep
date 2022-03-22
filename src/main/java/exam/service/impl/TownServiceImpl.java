package exam.service.impl;

import exam.model.dto.xml.TownSeedRootDto;
import exam.model.entity.Town;
import exam.repository.TownRepository;
import exam.service.TownService;
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
public class TownServiceImpl implements TownService {

    private final static String TOWN_PATH = "/Users/betinabataklieva/Downloads/LaptopShop/src/main/resources/files/xml/towns.xml";

    private final TownRepository townRepository;

    private final ModelMapper mapper;
    private final ValidationUtil validationUtil;
    private final XmlParser xmlParser;


    @Autowired
    public TownServiceImpl(TownRepository townRepository, XmlParser xmlParser,
                           ModelMapper mapper, ValidationUtil validationUtil) {
        this.townRepository = townRepository;
        this.mapper = mapper;
        this.validationUtil = validationUtil;
        this.xmlParser = xmlParser;
    }

    @Override
    public boolean areImported() {
        return townRepository.count() > 0;
    }

    @Override
    public String readTownsFileContent() throws IOException {
        return Files.readString(Path.of(TOWN_PATH));
    }

    @Override
    public String importTowns() throws JAXBException, FileNotFoundException {

        StringBuilder builder = new StringBuilder();

        TownSeedRootDto townsSeedDto = xmlParser.fromFile(TOWN_PATH, TownSeedRootDto.class);

        townsSeedDto.getTowns().stream()
                .filter(townSeedDto -> {

                    boolean isValid = validationUtil.isValid(townSeedDto);

                    builder.append(isValid
                            ? "Successfully imported Town " + townSeedDto.getName()
                            : "Invalid town")
                            .append(System.lineSeparator());

                    return isValid;

                })
                .map(towns -> mapper.map(towns, Town.class))
                .forEach(townRepository::save);

        return builder.toString();
    }
}
