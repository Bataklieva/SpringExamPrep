package exam.model.dto.json;

import com.google.gson.annotations.Expose;
import exam.model.entity.Warranty;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.*;
import java.math.BigDecimal;

public class LaptopSeedDto {

    @Expose
    private String macAddress;

    @Expose
    private float cpuSpeed;

    @Expose
    private int ram;

    @Expose
    private int storage;

    @Expose
    private String description;

    @Expose
    private BigDecimal price;

    @Expose
    private Warranty warrantyType;

    @Expose
    private ShopNameDto shop;

    public LaptopSeedDto() {
    }

    @Size(min = 8)
    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    @Positive
    public float getCpuSpeed() {
        return cpuSpeed;
    }

    public void setCpuSpeed(float cpuSpeed) {
        this.cpuSpeed = cpuSpeed;
    }

    @Min(8)
    @Max(128)
    public int getRam() {
        return ram;
    }

    public void setRam(int ram) {
        this.ram = ram;
    }

    @Min(128)
    @Max(1024)
    public int getStorage() {
        return storage;
    }

    public void setStorage(int storage) {
        this.storage = storage;
    }

    @Size(min = 10)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Positive
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }


    @NotNull
    @Enumerated(EnumType.STRING)
    public Warranty getWarrantyType() {
        return warrantyType;
    }

    public void setWarrantyType(Warranty warrantyType) {
            this.warrantyType = warrantyType;
    }

    public ShopNameDto getShop() {
        return shop;
    }

    public void setShop(ShopNameDto shop) {
        this.shop = shop;
    }
}
