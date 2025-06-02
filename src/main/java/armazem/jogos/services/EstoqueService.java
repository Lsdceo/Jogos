package armazem.jogos.services;

import armazem.jogos.dtos.MovimentacaoRequestDTO;
import armazem.jogos.entities.*;
import armazem.jogos.exception.InsufficientStockException;
import armazem.jogos.exception.ResourceNotFoundException;
import armazem.jogos.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EstoqueService {

    @Autowired ItemEstoqueRepository itemEstoqueRepository;
    @Autowired MovimentacaoRepository movimentacaoRepository;
    @Autowired JogoRepository jogoRepository;
    @Autowired PlataformaRepository plataformaRepository;
    @Autowired DepositoRepository depositoRepository;

    @Transactional
    public Movimentacao registrarMovimentacao(MovimentacaoRequestDTO request) {
        Jogo jogo = jogoRepository.findById(request.getJogoId())
                .orElseThrow(() -> new ResourceNotFoundException("Jogo não encontrado: " + request.getJogoId()));
        Plataforma plataforma = plataformaRepository.findById(request.getPlataformaId())
                .orElseThrow(() -> new ResourceNotFoundException("Plataforma não encontrada: " + request.getPlataformaId()));

        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setJogo(jogo);
        movimentacao.setPlataforma(plataforma);
        movimentacao.setQuantidade(request.getQuantidade());
        movimentacao.setTipo(request.getTipo());
        movimentacao.setDataHora(LocalDateTime.now());
        movimentacao.setPrecoUnitarioMomento(request.getPrecoUnitarioMomento() != null ? request.getPrecoUnitarioMomento() : jogo.getPrecoSugerido());
        movimentacao.setObservacao(request.getObservacao());

        switch (request.getTipo()) {
            case ENTRADA:
            case AJUSTE_POSITIVO:
                handleEntrada(request, jogo, plataforma, movimentacao);
                break;
            case SAIDA:
            case AJUSTE_NEGATIVO:
                handleSaida(request, jogo, plataforma, movimentacao);
                break;
            case TRANSFERENCIA_SAIDA:
            case TRANSFERENCIA_ENTRADA:
                throw new UnsupportedOperationException("Use o método registrarTransferencia para transferências ou ajuste as chamadas.");
            default:
                throw new IllegalArgumentException("Tipo de movimentação inválido: " + request.getTipo());
        }
        return movimentacaoRepository.save(movimentacao);
    }


    @Transactional
    public void registrarTransferencia(Long jogoId, Long plataformaId, Long depositoOrigemId, Long depositoDestinoId, int quantidade, String observacao) {
        Jogo jogo = jogoRepository.findById(jogoId).orElseThrow(() -> new ResourceNotFoundException("Jogo não encontrado: "+jogoId));
        Plataforma plataforma = plataformaRepository.findById(plataformaId).orElseThrow(() -> new ResourceNotFoundException("Plataforma não encontrada: "+plataformaId));
        Deposito depositoOrigem = depositoRepository.findById(depositoOrigemId).orElseThrow(() -> new ResourceNotFoundException("Depósito de origem não encontrado: "+depositoOrigemId));
        Deposito depositoDestino = depositoRepository.findById(depositoDestinoId).orElseThrow(() -> new ResourceNotFoundException("Depósito de destino não encontrado: "+depositoDestinoId));

        if (depositoOrigem.getId().equals(depositoDestino.getId())) {
            throw new IllegalArgumentException("Depósito de origem e destino não podem ser os mesmos.");
        }

        // SAÍDA DO DEPÓSITO DE ORIGEM
        ItemEstoque itemOrigem = itemEstoqueRepository.findByJogoAndPlataformaAndDeposito(jogo, plataforma, depositoOrigem)
                .orElseThrow(() -> new ResourceNotFoundException("Item " + jogo.getTitulo() + "/" + plataforma.getNome() + " não encontrado no depósito de origem '" + depositoOrigem.getNome() + "' para transferência."));

        if (itemOrigem.getQuantidade() < quantidade) {
            throw new InsufficientStockException("Estoque insuficiente de "+ jogo.getTitulo() + "/" + plataforma.getNome() +" no depósito '"+depositoOrigem.getNome()+"' para transferência. Solicitado: "+quantidade+", Disponível: "+itemOrigem.getQuantidade());
        }
        itemOrigem.setQuantidade(itemOrigem.getQuantidade() - quantidade);
        itemEstoqueRepository.save(itemOrigem);

        Movimentacao movSaida = new Movimentacao(null, LocalDateTime.now(), TipoMovimentacao.TRANSFERENCIA_SAIDA,
                jogo, plataforma, depositoOrigem, depositoDestino, // Destino aqui é para rastreio da transf.
                quantidade, itemOrigem.getPrecoUnitarioAtual(), // Usar preço do item em estoque
                "Transferência para: " + depositoDestino.getNome() + (observacao != null ? " - " + observacao : ""));
        movimentacaoRepository.save(movSaida);

        // ENTRADA NO DEPÓSITO DE DESTINO
        ItemEstoque itemDestino = itemEstoqueRepository.findByJogoAndPlataformaAndDeposito(jogo, plataforma, depositoDestino)
                .orElseGet(() -> {
                    ItemEstoque novoItem = new ItemEstoque();
                    novoItem.setJogo(jogo);
                    novoItem.setPlataforma(plataforma);
                    novoItem.setDeposito(depositoDestino);
                    novoItem.setQuantidade(0);
                    // Para transferências, geralmente o custo do item é o mesmo.
                    // Se o item não existir no destino, pode-se usar o preço do item de origem ou o preço base do jogo.
                    novoItem.setPrecoUnitarioAtual(itemOrigem.getPrecoUnitarioAtual());
                    return novoItem;
                });
        itemDestino.setQuantidade(itemDestino.getQuantidade() + quantidade);
        itemEstoqueRepository.save(itemDestino);

        Movimentacao movEntrada = new Movimentacao(null, LocalDateTime.now(), TipoMovimentacao.TRANSFERENCIA_ENTRADA,
                jogo, plataforma, depositoOrigem, // Origem aqui é para rastreio da transf.
                depositoDestino, quantidade, itemDestino.getPrecoUnitarioAtual(), // Usar preço do item no destino
                "Transferência de: " + depositoOrigem.getNome() + (observacao != null ? " - " + observacao : ""));
        movimentacaoRepository.save(movEntrada);
    }


    private void handleEntrada(MovimentacaoRequestDTO request, Jogo jogo, Plataforma plataforma, Movimentacao movimentacao) {
        if (request.getDepositoDestinoId() == null) {
            throw new IllegalArgumentException("Depósito de destino é obrigatório para entradas/ajustes positivos.");
        }
        Deposito deposito = depositoRepository.findById(request.getDepositoDestinoId())
                .orElseThrow(() -> new ResourceNotFoundException("Depósito de destino não encontrado: " + request.getDepositoDestinoId()));
        movimentacao.setDepositoDestino(deposito);
        // Depósito de origem é nulo para entradas diretas/ajustes.
        movimentacao.setDepositoOrigem(null);


        ItemEstoque item = itemEstoqueRepository.findByJogoAndPlataformaAndDeposito(jogo, plataforma, deposito)
                .orElseGet(() -> {
                    ItemEstoque novoItem = new ItemEstoque();
                    novoItem.setJogo(jogo);
                    novoItem.setPlataforma(plataforma);
                    novoItem.setDeposito(deposito);
                    novoItem.setQuantidade(0);
                    novoItem.setPrecoUnitarioAtual(movimentacao.getPrecoUnitarioMomento()); // Preço da compra inicial
                    return novoItem;
                });
        int quantidadeAnterior = item.getQuantidade();
        BigDecimal valorTotalAnterior = item.getPrecoUnitarioAtual().multiply(new BigDecimal(quantidadeAnterior));

        item.setQuantidade(quantidadeAnterior + request.getQuantidade());

        // Lógica de Custo Médio Ponderado para ENTRADAS (compras)
        if (request.getTipo() == TipoMovimentacao.ENTRADA && request.getPrecoUnitarioMomento() != null && item.getQuantidade() > 0) {
            BigDecimal valorNovaEntrada = request.getPrecoUnitarioMomento().multiply(new BigDecimal(request.getQuantidade()));
            BigDecimal novoValorTotalEstoque = valorTotalAnterior.add(valorNovaEntrada);
            item.setPrecoUnitarioAtual(novoValorTotalEstoque.divide(new BigDecimal(item.getQuantidade()), 2, BigDecimal.ROUND_HALF_UP));
        } else if (item.getQuantidade() == request.getQuantidade()) { // Primeiro item em estoque
            item.setPrecoUnitarioAtual(movimentacao.getPrecoUnitarioMomento());
        }
        // Para AJUSTE_POSITIVO, o preço unitário atual do item não é alterado pela movimentação de ajuste em si,
        // a menos que seja o primeiro item (nesse caso, o preço da movimentação pode ser usado)
        // ou que haja uma regra de negócio específica para reavaliar o preço em ajustes.

        itemEstoqueRepository.save(item);
    }

    private void handleSaida(MovimentacaoRequestDTO request, Jogo jogo, Plataforma plataforma, Movimentacao movimentacao) {
        if (request.getDepositoOrigemId() == null) {
            throw new IllegalArgumentException("Depósito de origem é obrigatório para saídas/ajustes negativos.");
        }
        Deposito deposito = depositoRepository.findById(request.getDepositoOrigemId())
                .orElseThrow(() -> new ResourceNotFoundException("Depósito de origem não encontrado: " + request.getDepositoOrigemId()));
        movimentacao.setDepositoOrigem(deposito);
        // Depósito de destino é nulo para saídas diretas/ajustes.
        movimentacao.setDepositoDestino(null);


        ItemEstoque item = itemEstoqueRepository.findByJogoAndPlataformaAndDeposito(jogo, plataforma, deposito)
                .orElseThrow(() -> new ResourceNotFoundException("Item "+jogo.getTitulo()+"/"+plataforma.getNome()+" não encontrado no estoque '"+deposito.getNome()+"' para saída."));

        if (item.getQuantidade() < request.getQuantidade()) {
            throw new InsufficientStockException("Estoque insuficiente para " + jogo.getTitulo() + "/" + plataforma.getNome() + " no depósito '"+deposito.getNome()+"'. Solicitado: "+request.getQuantidade()+", Disponível: "+item.getQuantidade());
        }
        item.setQuantidade(item.getQuantidade() - request.getQuantidade());
        // Para SAIDAS (vendas) ou AJUSTE_NEGATIVO, o precoUnitarioAtual do item em estoque geralmente não muda.
        // O precoUnitarioMomento da movimentação registra o valor da venda/perda.
        itemEstoqueRepository.save(item);
    }


    public List<ItemEstoque> consultarEstoque(Long jogoId, Long plataformaId, Long depositoId) {
        return itemEstoqueRepository.findByOptionalFilters(jogoId, plataformaId, depositoId);
    }

    public BigDecimal calcularValorTotalEstoque(Long depositoId) {
        List<ItemEstoque> itens;
        if (depositoId != null) {
            Deposito deposito = depositoRepository.findById(depositoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Depósito não encontrado: " + depositoId));
            itens = itemEstoqueRepository.findByDeposito(deposito);
        } else {
            itens = itemEstoqueRepository.findAll();
        }

        return itens.stream()
                .map(item -> item.getPrecoUnitarioAtual().multiply(new BigDecimal(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
