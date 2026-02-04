package com.grupo51.oficinamecanica.agendamento.service;

import com.grupo51.oficinamecanica.agendamento.model.Agendamento;
import com.grupo51.oficinamecanica.agendamento.repository.AgendamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository repository;

    public Agendamento agendar(Agendamento agendamento) {
        // Lógica para calcular e verificar horários livres
        if (estaDisponivel(agendamento)) {
            agendamento.setConfirmado(true);
            return repository.save(agendamento);
        }
        throw new RuntimeException("Recurso ocupado para esta Janela de Serviço.");
    }

    private boolean estaDisponivel(Agendamento novo) {
        // Verifica se o recurso já possui agendamento no mesmo período
        return !repository.existsByRecursoIdAndJanelaOverlap(
                novo.getRecursoId(),
                novo.getJanela().getDataHoraInicio(),
                novo.getJanela().getDataHoraFim()
        );
    }

    public Optional<Agendamento> buscarPorId(UUID id) {
        return repository.findById(id);
    }
}
