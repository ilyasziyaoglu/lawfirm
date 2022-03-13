package net.evercode.lawfirm.service.criteria;

import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Criteria class for the {@link net.evercode.lawfirm.domain.Properties} entity. This class is used
 * in {@link net.evercode.lawfirm.web.rest.PropertiesResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /properties?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class PropertiesCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter key;

    private StringFilter value;

    private StringFilter language;

    private Boolean distinct;

    public PropertiesCriteria() {}

    public PropertiesCriteria(PropertiesCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.key = other.key == null ? null : other.key.copy();
        this.value = other.value == null ? null : other.value.copy();
        this.language = other.language == null ? null : other.language.copy();
        this.distinct = other.distinct;
    }

    @Override
    public PropertiesCriteria copy() {
        return new PropertiesCriteria(this);
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

    public StringFilter getKey() {
        return key;
    }

    public StringFilter key() {
        if (key == null) {
            key = new StringFilter();
        }
        return key;
    }

    public void setKey(StringFilter key) {
        this.key = key;
    }

    public StringFilter getValue() {
        return value;
    }

    public StringFilter value() {
        if (value == null) {
            value = new StringFilter();
        }
        return value;
    }

    public void setValue(StringFilter value) {
        this.value = value;
    }

    public StringFilter getLanguage() {
        return language;
    }

    public StringFilter language() {
        if (language == null) {
            language = new StringFilter();
        }
        return language;
    }

    public void setLanguage(StringFilter language) {
        this.language = language;
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
        final PropertiesCriteria that = (PropertiesCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(key, that.key) &&
            Objects.equals(value, that.value) &&
            Objects.equals(language, that.language) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, key, value, language, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PropertiesCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (key != null ? "key=" + key + ", " : "") +
            (value != null ? "value=" + value + ", " : "") +
            (language != null ? "language=" + language + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
