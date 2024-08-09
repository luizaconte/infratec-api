package br.com.infratec.util.rsql.bean;

import br.com.infratec.util.rsql.constant.CustomOperator;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Set;

@Configuration
public class RsqlParserBean {

    @Bean
    public RSQLParser rsqlParser() {
        Set<ComparisonOperator> operators = RSQLOperators.defaultOperators();
        Arrays.asList(CustomOperator.values()).forEach(customOperator -> {
            operators.add(customOperator.getOperator());
        });
        return new RSQLParser(operators);
    }

}
