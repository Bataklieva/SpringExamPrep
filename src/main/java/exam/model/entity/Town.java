package exam.model.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "towns")
public class Town extends BaseEntity {

    private String name;

    @Column(unique = true, nullable = false)
    private int population;

    @Column(name = "travel_guide",columnDefinition = "TEXT", nullable = false)
    private String travelGuide;

    @OneToMany(mappedBy = "town", cascade = CascadeType.ALL)
    private Set<Customer> customers;

    public Town() {
        this.customers = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public String getTravelGuide() {
        return travelGuide;
    }

    public void setTravelGuide(String travelGuide) {
        this.travelGuide = travelGuide;
    }

    public Set<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(Set<Customer> customers) {
        this.customers = customers;
    }
}
