package br.com.tabela_fipe.Principal;

import br.com.tabela_fipe.model.Avaliacao;
import br.com.tabela_fipe.model.Dados;
import br.com.tabela_fipe.model.Modelos;
import br.com.tabela_fipe.service.ConsumoApi;
import br.com.tabela_fipe.service.ConverteDados;

import java.util.*;

public class Principal {
    private List<String> veiculos = new ArrayList<>(Arrays.asList("carros", "motos", "caminhoes"));
    private List<Dados> dados = new ArrayList<>();
    private List<Avaliacao> avaliacoes = new ArrayList<>();

    private Scanner entrada = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    private final String ENDERECO = "https://parallelum.com.br/fipe/api/v1/";

    public void exibeMenu () {
        System.out.println("Informe o número do tipo do veículo: ");
        exibeTiposVeiculos();

        var opcao = -1;

        while (true) {
            opcao = entrada.nextInt();
            entrada.nextLine();

            if (opcao < 1 || opcao > veiculos.size()) {
                System.out.println("\nEscolha uma opção válida:");
                exibeTiposVeiculos();
            } else {
                break;
            }
        }

        var tipoVeiculo = veiculos.get(opcao - 1);
        var endereco = ENDERECO + tipoVeiculo + "/marcas";

        var jsonMarcas = consumoApi.obterDados(endereco);
        setDados(conversor.obterDadosLista(jsonMarcas, Dados.class));
        System.out.println();
        exibeDados();

        System.out.println("\nInforme o número da marca: ");
        var codigoMarca = entrada.nextInt();
        entrada.nextLine();

        endereco += "/" + codigoMarca + "/modelos";
        var jsonModelos = consumoApi.obterDados(endereco);
        setDados(conversor.obterDados(jsonModelos, Modelos.class).modelos());
        System.out.println();
        exibeDados();

        System.out.println("\nDigite um trecho do modelo do veículo: ");
        var trechoNomeModelo = entrada.nextLine();
        System.out.println();
        exibeDadosFiltrados(trechoNomeModelo);

        System.out.println("\nInforme o número do modelo que deseja: ");
        var codigoModelo = entrada.nextInt();
        entrada.nextLine();

        endereco += "/" + codigoModelo + "/anos";
        var jsonAnos = consumoApi.obterDados(endereco);
        List<Dados> anosVeiculo = conversor.obterDadosLista(jsonAnos, Dados.class);


        String finalEndereco = endereco;
        anosVeiculo.stream()
                .forEach(a -> {
                    var jsonAvaliacao = consumoApi.obterDados(finalEndereco + "/" + a.codigo());
                    var avaliacao = conversor.obterDados(jsonAvaliacao, Avaliacao.class);
                    this.avaliacoes.add(avaliacao);
                });

        System.out.println("\nAqui estão as avalições para este modelo: \n");
        exibeDadosAvaliacoes();
    }



    private void setDados(List<Dados> dados) {
        this.dados = dados;
    }

    private void exibeTiposVeiculos() {
        veiculos.forEach(v -> {
            System.out.print("[");
            System.out.print(veiculos.indexOf(v) + 1);
            System.out.println("] " + v);
        });
    }

    private void exibeDados() {
        dados.stream()
                .forEach(m -> {
                    System.out.print("[");
                    System.out.print(m.codigo());
                    System.out.println("] " + m.nome());
                });
    }

    private void exibeDadosFiltrados(String pesquisa) {
        dados.stream()
                .filter(d -> d.nome().toLowerCase().contains(pesquisa.toLowerCase()))
                .forEach(d -> {
                    System.out.print("[");
                    System.out.print(d.codigo());
                    System.out.println("] " + d.nome());
                });
    }

    private void exibeDadosAvaliacoes() {
        avaliacoes.stream()
                .sorted(Comparator.comparing(Avaliacao::ano))
                .forEach(a -> {
                    System.out.print(a.marca());
                    System.out.print(" " + a.modelo());
                    System.out.print(" ano: " + a.ano());
                    System.out.print(" valor: " + a.valor());
                    System.out.println(" combustivel: " + a.combustivel());
                });
    }
}
