package br.com.infratec.security;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import java.security.Principal;

@Value
@ToString
@Builder
public class CustomPrincipal implements Principal {

    private String name;

    private Integer userId;

}
