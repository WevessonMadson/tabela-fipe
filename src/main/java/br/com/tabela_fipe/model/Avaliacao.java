package br.com.tabela_fipe.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Avaliacao(@JsonAlias("Marca") String marca,
                        @JsonAlias("Modelo") String modelo,
                        @JsonAlias("AnoModelo") Integer ano,
                        @JsonAlias("Valor") String  valor,
                        @JsonAlias("Combustivel") String combustivel) {
}
