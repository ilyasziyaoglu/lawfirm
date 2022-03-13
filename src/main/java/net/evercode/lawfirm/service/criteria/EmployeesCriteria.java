package net.evercode.lawfirm.service.criteria;

import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Criteria class for the {@link net.evercode.lawfirm.domain.Employees} entity. This class is used
 * in {@link net.evercode.lawfirm.web.rest.EmployeesResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /employees?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class EmployeesCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter surname;

    private StringFilter title;

    private StringFilter story;

    private IntegerFilter order;

    private LongFilter servicePointId;

    private LongFilter servicesId;

    private Boolean distinct;

    public EmployeesCriteria() {}

    public EmployeesCriteria(EmployeesCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.surname = other.surname == null ? null : other.surname.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.story = other.story == null ? null : other.story.copy();
        this.order = other.order == null ? null : other.order.copy();
        this.servicePointId = other.servicePointId == null ? null : other.servicePointId.copy();
        this.servicesId = other.servicesId == null ? null : other.servicesId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public EmployeesCriteria copy() {
        return new EmployeesCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getSurname() {
        return surname;
    }

    public StringFilter surname() {
        if (surname == null) {
            surname = new StringFilter();
        }
        return surname;
    }

    public void setSurname(StringFilter surname) {
        this.surname = surname;
    }

    public StringFilter getTitle() {
        return title;
    }

    public StringFilter title() {
        if (title == null) {
            title = new StringFilter();
        }
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getStory() {
        return story;
    }

    public StringFilter story() {
        if (story == null) {
            story = new StringFilter();
        }
        return story;
    }

    public void setStory(StringFilter story) {
        this.story = story;
    }

    public IntegerFilter getOrder() {
        return order;
    }

    public IntegerFilter order() {
        if (order == null) {
            order = new IntegerFilter();
        }
        return order;
    }

    public void setOrder(IntegerFilter order) {
        this.order = order;
    }

    public LongFilter getServicePointId() {
        return servicePointId;
    }

    public LongFilter servicePointId() {
        if (servicePointId == null) {
            servicePointId = new LongFilter();
        }
        return servicePointId;
    }

    public void setServicePointId(LongFilter servicePointId) {
        this.servicePointId = servicePointId;
    }

    public LongFilter getServicesId() {
        return servicesId;
    }

    public LongFilter servicesId() {
        if (servicesId == null) {
            servicesId = new LongFilter();
        }
        return servicesId;
    }

    public void setServicesId(LongFilter servicesId) {
        this.servicesId = servicesId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EmployeesCriteria that = (EmployeesCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(surname, that.surname) &&
            Objects.equals(title, that.title) &&
            Objects.equals(story, that.story) &&
            Objects.equals(order, that.order) &&
            Objects.equals(servicePointId, that.servicePointId) &&
            Objects.equals(servicesId, that.servicesId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname, title, story, order, servicePointId, servicesId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EmployeesCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (surname != null ? "surname=" + surname + ", " : "") +
            (title != null ? "title=" + title + ", " : "") +
            (story != null ? "story=" + story + ", " : "") +
            (order != null ? "order=" + order + ", " : "") +
            (servicePointId != null ? "servicePointId=" + servicePointId + ", " : "") +
            (servicesId != null ? "servicesId=" + servicesId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
