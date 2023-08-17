package com.jsh.erp.datasource.entities;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class VehicleExample {

    protected String orderByClause;

    protected List<Criteria> oredCriteria;

    public VehicleExample() {
        oredCriteria = new ArrayList<>();
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andLicensePlateNumberIsNull() {
            addCriterion("license_plate_number is null");
            return (Criteria) this;
        }

        public Criteria andLicensePlateNumberIsNotNull() {
            addCriterion("license_plate_number is not null");
            return (Criteria) this;
        }

        public Criteria andLicensePlateNumberEqualTo(String value) {
            addCriterion("license_plate_number =", value, "license_plate_number");
            return (Criteria) this;
        }

        public Criteria andLicensePlateNumberNotEqualTo(String value) {
            addCriterion("license_plate_number <>", value, "license_plate_number");
            return (Criteria) this;
        }

        public Criteria andLicensePlateNumberGreaterThan(String value) {
            addCriterion("license_plate_number >", value, "license_plate_number");
            return (Criteria) this;
        }

        public Criteria andLicensePlateNumberGreaterThanOrEqualTo(String value) {
            addCriterion("license_plate_number >=", value, "license_plate_number");
            return (Criteria) this;
        }

        public Criteria andLicensePlateNumberLessThan(String value) {
            addCriterion("license_plate_number <", value, "license_plate_number");
            return (Criteria) this;
        }

        public Criteria andLicensePlateNumberLessThanOrEqualTo(String value) {
            addCriterion("license_plate_number <=", value, "license_plate_number");
            return (Criteria) this;
        }

        public Criteria andLicensePlateNumberLike(String value) {
            addCriterion("license_plate_number like", value, "license_plate_number");
            return (Criteria) this;
        }

        public Criteria andLicensePlateNumberNotLike(String value) {
            addCriterion("license_plate_number not like", value, "license_plate_number");
            return (Criteria) this;
        }

        public Criteria andLicensePlateNumberIn(List<String> values) {
            addCriterion("license_plate_number in", values, "license_plate_number");
            return (Criteria) this;
        }

        public Criteria andLicensePlateNumberNotIn(List<String> values) {
            addCriterion("license_plate_number not in", values, "license_plate_number");
            return (Criteria) this;
        }

        public Criteria andLicensePlateNumberBetween(String value1, String value2) {
            addCriterion("license_plate_number between", value1, value2, "license_plate_number");
            return (Criteria) this;
        }

        public Criteria andLicensePlateNumberNotBetween(String value1, String value2) {
            addCriterion("license_plate_number not between", value1, value2, "license_plate_number");
            return (Criteria) this;
        }

        public Criteria andBrandModelIsNull() {
            addCriterion("brand_model is null");
            return (Criteria) this;
        }

        public Criteria andBrandModelIsNotNull() {
            addCriterion("brand_model is not null");
            return (Criteria) this;
        }

        public Criteria andBrandModelEqualTo(String value) {
            addCriterion("brand_model =", value, "brand_model");
            return (Criteria) this;
        }

        public Criteria andBrandModelNotEqualTo(String value) {
            addCriterion("brand_model <>", value, "brand_model");
            return (Criteria) this;
        }

        public Criteria andBrandModelGreaterThan(String value) {
            addCriterion("brand_model >", value, "brand_model");
            return (Criteria) this;
        }

        public Criteria andBrandModelGreaterThanOrEqualTo(String value) {
            addCriterion("brand_model >=", value, "brand_model");
            return (Criteria) this;
        }

        public Criteria andBrandModelLessThan(String value) {
            addCriterion("brand_model <", value, "brand_model");
            return (Criteria) this;
        }

        public Criteria andBrandModelLessThanOrEqualTo(String value) {
            addCriterion("brand_model <=", value, "brand_model");
            return (Criteria) this;
        }

        public Criteria andBrandModelLike(String value) {
            addCriterion("brand_model like", value, "brand_model");
            return (Criteria) this;
        }

        public Criteria andBrandModelNotLike(String value) {
            addCriterion("brand_model not like", value, "brand_model");
            return (Criteria) this;
        }

        public Criteria andBrandModelIn(List<String> values) {
            addCriterion("brand_model in", values, "brand_model");
            return (Criteria) this;
        }

        public Criteria andBrandModelNotIn(List<String> values) {
            addCriterion("brand_model not in", values, "brand_model");
            return (Criteria) this;
        }

        public Criteria andBrandModelBetween(String value1, String value2) {
            addCriterion("brand_model between", value1, value2, "brand_model");
            return (Criteria) this;
        }

        public Criteria andBrandModelNotBetween(String value1, String value2) {
            addCriterion("brand_model not between", value1, value2, "brand_model");
            return (Criteria) this;
        }

        public Criteria andDriverIsNull() {
            addCriterion("driver is null");
            return (Criteria) this;
        }

        public Criteria andDriverIsNotNull() {
            addCriterion("driver is not null");
            return (Criteria) this;
        }

        public Criteria andDriverEqualTo(String value) {
            addCriterion("driver =", value, "driver");
            return (Criteria) this;
        }

        public Criteria andDriverNotEqualTo(String value) {
            addCriterion("driver <>", value, "driver");
            return (Criteria) this;
        }

        public Criteria andDriverGreaterThan(String value) {
            addCriterion("driver >", value, "driver");
            return (Criteria) this;
        }

        public Criteria andDriverGreaterThanOrEqualTo(String value) {
            addCriterion("driver >=", value, "driver");
            return (Criteria) this;
        }

        public Criteria andDriverLessThan(String value) {
            addCriterion("driver <", value, "driver");
            return (Criteria) this;
        }

        public Criteria andDriverLessThanOrEqualTo(String value) {
            addCriterion("driver <=", value, "driver");
            return (Criteria) this;
        }

        public Criteria andDriverLike(String value) {
            addCriterion("driver like", value, "driver");
            return (Criteria) this;
        }

        public Criteria andDriverNotLike(String value) {
            addCriterion("driver not like", value, "driver");
            return (Criteria) this;
        }

        public Criteria andDriverIn(List<String> values) {
            addCriterion("driver in", values, "driver");
            return (Criteria) this;
        }

        public Criteria andDriverNotIn(List<String> values) {
            addCriterion("driver not in", values, "driver");
            return (Criteria) this;
        }

        public Criteria andDriverBetween(String value1, String value2) {
            addCriterion("driver between", value1, value2, "driver");
            return (Criteria) this;
        }

        public Criteria andDriverNotBetween(String value1, String value2) {
            addCriterion("driver not between", value1, value2, "driver");
            return (Criteria) this;
        }

    }

    public static class Criteria extends GeneratedCriteria {
        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}
