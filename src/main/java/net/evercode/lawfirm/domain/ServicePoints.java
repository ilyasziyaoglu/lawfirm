package net.evercode.lawfirm.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A ServicePoints.
 */
@Entity
@Table(name = "service_points")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ServicePoints implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(name = "email", length = 50, nullable = false)
    private String email;

    @NotNull
    @Size(min = 7, max = 14)
    @Column(name = "phone", length = 14, nullable = false)
    private String phone;

    @NotNull
    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "map_url")
    private String mapUrl;

    @Column(name = "latitude")
    private Integer latitude;

    @Column(name = "longitude")
    private Integer longitude;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ServicePoints id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public ServicePoints name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public ServicePoints email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public ServicePoints phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return this.address;
    }

    public ServicePoints address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMapUrl() {
        return this.mapUrl;
    }

    public ServicePoints mapUrl(String mapUrl) {
        this.setMapUrl(mapUrl);
        return this;
    }

    public void setMapUrl(String mapUrl) {
        this.mapUrl = mapUrl;
    }

    public Integer getLatitude() {
        return this.latitude;
    }

    public ServicePoints latitude(Integer latitude) {
        this.setLatitude(latitude);
        return this;
    }

    public void setLatitude(Integer latitude) {
        this.latitude = latitude;
    }

    public Integer getLongitude() {
        return this.longitude;
    }

    public ServicePoints longitude(Integer longitude) {
        this.setLongitude(longitude);
        return this;
    }

    public void setLongitude(Integer longitude) {
        this.longitude = longitude;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServicePoints)) {
            return false;
        }
        return id != null && id.equals(((ServicePoints) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ServicePoints{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", email='" + getEmail() + "'" +
            ", phone='" + getPhone() + "'" +
            ", address='" + getAddress() + "'" +
            ", mapUrl='" + getMapUrl() + "'" +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            "}";
    }
}
