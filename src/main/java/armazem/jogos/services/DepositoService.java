package armazem.jogos.services;

import armazem.jogos.entities.Deposito;
import armazem.jogos.exception.ResourceNotFoundException;
import armazem.jogos.repositories.DepositoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepositoService {
    @Autowired
    DepositoRepository depositoRepository;

    public List<Deposito> listarTodos() {
        return depositoRepository.findAll();
    }

    public Deposito buscarPorId(Long id) {
        return depositoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Depósito não encontrado com id: " + id));
    }

    @Transactional
    public Deposito salvar(Deposito deposito) {
        depositoRepository.findByNome(deposito.getNome()).ifPresent(d -> {
            if (!d.getId().equals(deposito.getId())) {
                throw new IllegalArgumentException("Depósito com nome '" + deposito.getNome() + "' já existe.");
            }
        });
        return depositoRepository.save(deposito);
    }

    @Transactional
    public Deposito atualizar(Long id, Deposito depositoDetalhes) {
        Deposito deposito = buscarPorId(id);
        deposito.setNome(depositoDetalhes.getNome());
        deposito.setLocalizacao(depositoDetalhes.getLocalizacao());
        return salvar(deposito);
    }

    @Transactional
    public void deletar(Long id) {
        if (!depositoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Depósito não encontrado para deleção com id: " + id);
        }
        // Adicionar verificações de integridade se necessário
        depositoRepository.deleteById(id);
    }
}