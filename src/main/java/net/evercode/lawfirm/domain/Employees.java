package net.evercode.lawfirm.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Employees.
 */
@Entity
@Table(name = "employees")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Employees implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3, max = 30)
    @Column(name = "name", length = 30, nullable = false)
    private String name;

    @NotNull
    @Size(min = 3, max = 30)
    @Column(name = "surname", length = 30, nullable = false)
    private String surname;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @Column(name = "story")
    private String story;

    @NotNull
    @Column(name = "jhi_order", nullable = false)
    private Integer order;

    @Lob
    @Column(name = "image", nullable = false)
    private byte[] image;

    @NotNull
    @Column(name = "image_content_type", nullable = false)
    private String imageContentType;

    @ManyToOne
    private ServicePoints servicePoint;

    @ManyToMany
    @JoinTable(
        name = "rel_employees__services",
        joinColumns = @JoinColumn(name = "employees_id"),
        inverseJoinColumns = @JoinColumn(name = "services_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Services> services = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Employees id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Employees name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return this.surname;
    }

    public Employees surname(String surname) {
        this.setSurname(surname);
        return this;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getTitle() {
        return this.title;
    }

    public Employees title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStory() {
        return this.story;
    }

    public Employees story(String story) {
        this.setStory(story);
        return this;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public Integer getOrder() {
        return this.order;
    }

    public Employees order(Integer order) {
        this.setOrder(order);
        return this;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public byte[] getImage() {
        return this.image;
    }

    public Employees image(byte[] image) {
        this.setImage(image);
        return this;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageContentType() {
        return this.imageContentType;
    }

    public Employees imageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
        return this;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public ServicePoints getServicePoint() {
        return this.servicePoint;
    }

    public void setServicePoint(ServicePoints servicePoints) {
        this.servicePoint = servicePoints;
    }

    public Employees servicePoint(ServicePoints servicePoints) {
        this.setServicePoint(servicePoints);
        return this;
    }

    public Set<Services> getServices() {
        return this.services;
    }

    public void setServices(Set<Services> services) {
        this.services = services;
    }

    public Employees services(Set<Services> services) {
        this.setServices(services);
        return this;
    }

    public Employees addServices(Services services) {
        this.services.add(services);
        return this;
    }

    public Employees removeServices(Services services) {
        this.services.remove(services);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Employees)) {
            return false;
        }
        return id != null && id.equals(((Employees) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Employees{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", surname='" + getSurname() + "'" +
            ", title='" + getTitle() + "'" +
            ", story='" + getStory() + "'" +
            ", order=" + getOrder() +
            ", image='" + getImage() + "'" +
            ", imageContentType='" + getImageContentType() + "'" +
            "}";
    }
}
