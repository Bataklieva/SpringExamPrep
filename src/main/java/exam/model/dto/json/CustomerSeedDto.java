package exam.model.dto.json;

import com.google.gson.annotations.Expose;
import exam.model.dto.xml.TownNameSeedDto;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class CustomerSeedDto {

    @Expose
    private String firstName;

    @Expose
    private String lastName;

    @Expose
    private String email;

    @Expose
    private String registeredOn;

    @Expose
    private TownNameSeedDto town;

    public CustomerSeedDto() {
    }

    @Size(min = 2)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Size(min = 2)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Pattern(regexp = "[a-z]+.?[a-z]+.?@[a-z]+.?[a-z]+")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegisteredOn() {
        return registeredOn;
    }

    public void setRegisteredOn(String registeredOn) {
        this.registeredOn = registeredOn;
    }

    public TownNameSeedDto getTown() {
        return town;
    }

    public void setTown(TownNameSeedDto town) {
        this.town = town;
    }
}
