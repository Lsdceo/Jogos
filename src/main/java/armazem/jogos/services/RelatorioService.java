package armazem.jogos.services;


import armazem.jogos.dtos.RelatorioEstoqueConsolidadoDTO;
import armazem.jogos.dtos.RelatorioEstoqueItemDTO;
import armazem.jogos.dtos.RelatorioMovimentacaoDTO;
import armazem.jogos.entities.Deposito;
import armazem.jogos.entities.ItemEstoque;
import armazem.jogos.entities.Movimentacao;
import armazem.jogos.entities.TipoMovimentacao;
import armazem.jogos.exception.ResourceNotFoundException;
import armazem.jogos.repositories.DepositoRepository;
import armazem.jogos.repositories.ItemEstoqueRepository;
import armazem.jogos.repositories.MovimentacaoRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

    @Autowired
    MovimentacaoRepository movimentacaoRepository;

    @Autowired
    ItemEstoqueRepository itemEstoqueRepository;

    @Autowired
    DepositoRepository depositoRepository;

    private static final DateTimeFormatter JASPER_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // Método para gerar JSON (renomeado para clareza)
    public List<RelatorioMovimentacaoDTO> gerarRelatorioMovimentacoesJson(
            LocalDateTime dataInicio, LocalDateTime dataFim,
            TipoMovimentacao tipo, Long jogoId, Long plataformaId, Long depositoId) {

        List<Movimentacao> movimentacoes = movimentacaoRepository.findByFiltrosOpcionais(
                dataInicio, dataFim, tipo, jogoId, plataformaId, depositoId
        );

        return movimentacoes.stream()
                .map(this::convertToMovimentacaoDTO)
                .collect(Collectors.toList());
    }

    // Método para gerar PDF
    public byte[] gerarRelatorioMovimentacoesPdf(
            LocalDateTime dataInicio, LocalDateTime dataFim,
            TipoMovimentacao tipoFiltro, Long jogoId, Long plataformaId, Long depositoIdFiltro) throws JRException {

        List<Movimentacao> movimentacoesBrutas = movimentacaoRepository.findByFiltrosOpcionais(
                dataInicio, dataFim, tipoFiltro, jogoId, plataformaId, depositoIdFiltro
        );

        List<Map<String, Object>> dataSourceList = movimentacoesBrutas.stream().map(mov -> {
            Map<String, Object> map = new HashMap<>();
            map.put("dataHora", mov.getDataHora() != null ? mov.getDataHora().format(JASPER_DATE_TIME_FORMATTER) : null);
            map.put("tipo", mov.getTipo() != null ? mov.getTipo().name() : null);
            map.put("jogoTitulo", mov.getJogo() != null ? mov.getJogo().getTitulo() : null);
            map.put("plataformaNome", mov.getPlataforma() != null ? mov.getPlataforma().getNome() : null);
            map.put("depositoOrigemNome", mov.getDepositoOrigem() != null ? mov.getDepositoOrigem().getNome() : null);
            map.put("depositoDestinoNome", mov.getDepositoDestino() != null ? mov.getDepositoDestino().getNome() : null);
            map.put("quantidade", mov.getQuantidade());
            map.put("precoUnitarioMomento", mov.getPrecoUnitarioMomento());
            map.put("observacao", mov.getObservacao());
            return map;
        }).collect(Collectors.toList());

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dataSourceList);

        JasperReport jasperReport;
        try (InputStream reportStream = new ClassPathResource("reports/relatorio_movimentacoes.jrxml").getInputStream()) {
            jasperReport = JasperCompileManager.compileReport(reportStream);
        } catch (Exception e) {
            throw new JRException("Erro ao carregar ou compilar o template do relatório: " + e.getMessage(), e);
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("REPORT_TITLE", "Relatório de Movimentações de Estoque");
        StringBuilder filterInfo = new StringBuilder();
        if (dataInicio != null) filterInfo.append("De: ").append(dataInicio.format(JASPER_DATE_TIME_FORMATTER)).append(" ");
        if (dataFim != null) filterInfo.append("Até: ").append(dataFim.format(JASPER_DATE_TIME_FORMATTER)).append(" ");
        if (tipoFiltro != null) filterInfo.append("Tipo: ").append(tipoFiltro.name()).append(" ");
        if (jogoId != null) filterInfo.append("Jogo ID: ").append(jogoId).append(" ");
        if (plataformaId != null) filterInfo.append("Plataforma ID: ").append(plataformaId).append(" ");
        if (depositoIdFiltro != null) filterInfo.append("Depósito ID: ").append(depositoIdFiltro).append(" ");

        parameters.put("FILTER_INFO", filterInfo.length() > 0 ? "Filtros: " + filterInfo.toString().trim() : "Sem filtros aplicados.");

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }


    private RelatorioMovimentacaoDTO convertToMovimentacaoDTO(Movimentacao mov) {
        return new RelatorioMovimentacaoDTO(
                mov.getId(),
                mov.getDataHora(),
                mov.getTipo(),
                mov.getJogo().getTitulo(),
                mov.getJogo().getId(),
                mov.getPlataforma().getNome(),
                mov.getPlataforma().getId(),
                mov.getDepositoOrigem() != null ? mov.getDepositoOrigem().getNome() : null,
                mov.getDepositoOrigem() != null ? mov.getDepositoOrigem().getId() : null,
                mov.getDepositoDestino() != null ? mov.getDepositoDestino().getNome() : null,
                mov.getDepositoDestino() != null ? mov.getDepositoDestino().getId() : null,
                mov.getQuantidade(),
                mov.getPrecoUnitarioMomento(),
                mov.getObservacao()
        );
    }

    public RelatorioEstoqueConsolidadoDTO gerarRelatorioEstoqueAtual(Long jogoId, Long plataformaId, Long depositoIdFiltro) {
        List<ItemEstoque> itensEstoque = itemEstoqueRepository.findByOptionalFilters(jogoId, plataformaId, depositoIdFiltro);
        String nomeDepositoFiltrado = null;

        if (depositoIdFiltro != null && !itensEstoque.isEmpty()) {
            // Tenta pegar o nome do depósito a partir do primeiro item filtrado,
            // ou busca no repositório se necessário (assumindo que todos os itens virão do mesmo depósito se filtrado)
            nomeDepositoFiltrado = itensEstoque.get(0).getDeposito().getNome();
        } else if (depositoIdFiltro != null) {
            Deposito d = depositoRepository.findById(depositoIdFiltro)
                    .orElseThrow(() -> new ResourceNotFoundException("Depósito não encontrado para filtro: " + depositoIdFiltro));
            nomeDepositoFiltrado = d.getNome();
        }


        List<RelatorioEstoqueItemDTO> itensRelatorio = itensEstoque.stream()
                .map(item -> new RelatorioEstoqueItemDTO(
                        item.getJogo().getId(),
                        item.getJogo().getTitulo(),
                        item.getPlataforma().getId(),
                        item.getPlataforma().getNome(),
                        item.getDeposito().getId(),
                        item.getDeposito().getNome(),
                        item.getQuantidade(),
                        item.getPrecoUnitarioAtual(),
                        item.getPrecoUnitarioAtual() != null ? item.getPrecoUnitarioAtual().multiply(new BigDecimal(item.getQuantidade())) : BigDecimal.ZERO
                ))
                .collect(Collectors.toList());

        BigDecimal valorTotalGeral = itensRelatorio.stream()
                .map(RelatorioEstoqueItemDTO::getValorTotalItem)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new RelatorioEstoqueConsolidadoDTO(itensRelatorio, valorTotalGeral, nomeDepositoFiltrado);
    }
}
