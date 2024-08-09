package br.com.infratec.service.sales;

import br.com.infratec.model.sales.TbLog;
import br.com.infratec.repository.sales.LogSistemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LogService {

    private final LogSistemaRepository logSistemaRepository;

    @Autowired
    public LogService(LogSistemaRepository logSistemaRepository) {
        this.logSistemaRepository = logSistemaRepository;
    }

    public void salvar(String ip, String evento, String msg) {
        logSistemaRepository.save(
                TbLog.builder()
                        .ip(ip)
                        .evento(evento)
                        .mensagem(msg)
                        .dataEvento(LocalDateTime.now())
                        .build()
        );
    }
}
