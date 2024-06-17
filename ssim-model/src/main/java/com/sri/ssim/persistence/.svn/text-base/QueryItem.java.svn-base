package com.sri.ssim.persistence;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 9/21/12
 */
@Deprecated
public class QueryItem {

    // and/or
    private Operator operator;
//    private SingularAttribute<Encounter, ?> singularAttribute;
//    private SetAttribute<Encounter, ?> setAttribute;
//    private ListAttribute<Encounter, ?> listAttribute;
    private Class entity;
    private String attribute;
    // >, <, =, <>
    private Comparison comparison;
    private Object value;
    private Object value2;

    private AttributeType attributeType;

    public Operator getOperator() {
        return operator;
    }
    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public Comparison getComparison() {
        return comparison;
    }
    public void setComparison(Comparison comparison) {
        this.comparison = comparison;
    }

    public Object getValue() {
        return value;
    }
    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue2() {
        return value2;
    }
    public void setValue2(Object value2) {
        this.value2 = value2;
    }

    /*
        public SingularAttribute<Encounter, ?> getSingularAttribute() {
            return singularAttribute;
        }
        public void setSingularAttribute(SingularAttribute<Encounter, ?> singularAttribute) {
            this.singularAttribute = singularAttribute;
            attributeType = AttributeType.Single;
        }

        public SetAttribute<Encounter, ?> getSetAttribute() {
            return setAttribute;
        }
        public void setSetAttribute(SetAttribute<Encounter, ?> setAttribute) {
            this.setAttribute = setAttribute;
            attributeType = AttributeType.Set;
        }

        public ListAttribute<Encounter, ?> getListAttribute() {
            return listAttribute;
        }
        public void setListAttribute(ListAttribute<Encounter, ?> listAttribute) {
            this.listAttribute = listAttribute;
            attributeType = AttributeType.List;
        }
    */

    public Class getEntity() {
        return entity;
    }
    public void setEntity(Class entity) {
        this.entity = entity;
    }

    public String getAttribute() {
        return attribute;
    }
    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public AttributeType getAttributeType() {
        return attributeType;
    }
    /*
    public static enum Comparison {
        equal, notEqual, in, between, greaterThan, greaterThanOrEqualTo, lessThan, lessThanOrEqualTo
    }
    */
    public enum Comparison {
        LIKE("LIKE"),
        IN("IN"),
        BETWEEN("BETWEEN"),
        EQUAL("="),
        NE("!="),
        GT(">"),
        LT("<"),
        GE(">="),
        LE("<=");

        private final String value;

        private Comparison(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public static enum Operator {
        and, or
    }

    public static enum AttributeType {
        Single, Set, List
    }
}
