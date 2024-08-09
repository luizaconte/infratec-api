package br.com.infratec.config;

import br.com.infratec.dto.ConfiguracaoDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    public static ConfiguracaoDTO APP_CONFIG;

    public static Long NUM_NFE;

    public static final String USUARIO_DEFAULT = "meli.api";

}
