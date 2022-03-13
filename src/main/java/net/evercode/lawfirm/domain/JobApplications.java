package net.evercode.lawfirm.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A JobApplications.
 */
@Entity
@Table(name = "job_applications")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class JobApplications implements Serializable {

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
    @Column(name = "email", length = 50, nullable = false)
    private String email;

    @NotNull
    @Size(min = 7, max = 14)
    @Column(name = "phone", length = 14, nullable = false)
    private String phone;

    @NotNull
    @Size(max = 100)
    @Column(name = "area", length = 100, nullable = false)
    private String area;

    @Column(name = "message")
    private String message;

    @Lob
    @Column(name = "cv", nullable = false)
    private byte[] cv;

    @NotNull
    @Column(name = "cv_content_type", nullable = false)
    private String cvContentType;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public JobApplications id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public JobApplications name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return this.surname;
    }

    public JobApplications surname(String surname) {
        this.setSurname(surname);
        return this;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return this.email;
    }

    public JobApplications email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public JobApplications phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getArea() {
        return this.area;
    }

    public JobApplications area(String area) {
        this.setArea(area);
        return this;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getMessage() {
        return this.message;
    }

    public JobApplications message(String message) {
        this.setMessage(message);
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public byte[] getCv() {
        return this.cv;
    }

    public JobApplications cv(byte[] cv) {
        this.setCv(cv);
        return this;
    }

    public void setCv(byte[] cv) {
        this.cv = cv;
    }

    public String getCvContentType() {
        return this.cvContentType;
    }

    public JobApplications cvContentType(String cvContentType) {
        this.cvContentType = cvContentType;
        return this;
    }

    public void setCvContentType(String cvContentType) {
        this.cvContentType = cvContentType;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JobApplications)) {
            return false;
        }
        return id != null && id.equals(((JobApplications) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "JobApplications{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", surname='" + getSurname() + "'" +
            ", email='" + getEmail() + "'" +
            ", phone='" + getPhone() + "'" +
            ", area='" + getArea() + "'" +
            ", message='" + getMessage() + "'" +
            ", cv='" + getCv() + "'" +
            ", cvContentType='" + getCvContentType() + "'" +
            "}";
    }
}
