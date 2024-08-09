package br.com.infratec.util.rsql.constant;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomOperator {

    EQUAL_IGNORE_CASE(new ComparisonOperator("=eic=")),
    NOT_EQUAL_IGNORE_CASE(new ComparisonOperator("=neic=")),
    NOT_EQUAL(new ComparisonOperator("!=")),
    GREATER_THAN(new ComparisonOperator("=gt=", ">")),
    GREATER_THAN_OR_EQUAL(new ComparisonOperator("=ge=", ">=")),
    LESS_THAN(new ComparisonOperator("=lt=", "<")),
    LESS_THAN_OR_EQUAL(new ComparisonOperator("=le=", "<=")),
    IN(new ComparisonOperator("=in=", true)),
    NOT_IN(new ComparisonOperator("=out=", true)),
    IS_NULL(new ComparisonOperator("=na=", "=isnull=", "=null=")),
    NOT_NULL(new ComparisonOperator("=nn=", "=notnull=", "=isnotnull=")),
    LIKE(new ComparisonOperator("=ke=", "=like=")),
    NOT_LIKE(new ComparisonOperator("=nk=", "=notlike=")),
    IGNORE_CASE(new ComparisonOperator("=ic=", "=icase=")),
    IGNORE_CASE_LIKE(new ComparisonOperator("=ik=", "=ilike=")),
    IGNORE_CASE_NOT_LIKE(new ComparisonOperator("=ni=", "=inotlike=")),
    BETWEEN(new ComparisonOperator("=bt=", "=between=", true)),
    NOT_BETWEEN(new ComparisonOperator("=nb=", "=notbetween=", true));


    private ComparisonOperator operator;

    }
