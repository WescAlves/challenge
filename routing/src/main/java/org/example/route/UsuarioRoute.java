package org.example.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.example.DTO.UsuarioDTO;
import org.example.exception.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
@Component
public class UsuarioRoute extends RouteBuilder {
    public void configure() {
        // Trata os possíveis erros que vêm do serviço atôomico
        onException(org.apache.camel.http.base.HttpOperationFailedException.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("${exception.statusCode}"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setBody().simple("${exception.responseBody}").unmarshal().json();

        // Trata possível erro no telefone recebido da requisição
        onException(TelefoneInvalidoException.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant((400)))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setBody().constant(new TelefoneInvalidoException().getMessage());

        // Trata possível erro no email recebido nda requisição
        onException(EmailInvalidoException.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant((400)))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setBody().constant(new EmailInvalidoException().getMessage());
        // Trata possível erro no id recebido da requisição
        onException(IdInvalidoException.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant((400)))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setBody().constant(new IdInvalidoException().getMessage());
        // Trata exception lançada ao cadastrar menor de idade
        onException(MenorDeIdadeException.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant((400)))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setBody().constant(new MenorDeIdadeException().getMessage());
        // Trata possível erro no nome recebido da requisição
        onException(NomeInvalidoException.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant((400)))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setBody().constant(new NomeInvalidoException().getMessage());






        // Configurações do Camel e de padrões de formatos
        restConfiguration().component("servlet").bindingMode(RestBindingMode.json).dataFormatProperty("objectMapper", "#objectMapper");

        // Rotas disponíveis do orquestrador
        rest("/usuarios").post().type(UsuarioDTO.class).to("direct:create")
                              .get("/{id}").to("direct:findById")
                              .delete("/{id}").to("direct:deleteById")
                              .put("/{id}").type(UsuarioDTO.class).to("direct:update")
                              .get("/saudacao/{id}").to("direct:saudacao");

        // Serviço de validação dos dados de usuários
        from("direct:validation").process(exchange -> {
                    UsuarioDTO dto = exchange.getIn().getBody(UsuarioDTO.class);
                    if(!dto.getEmail().contains(("@")) || !dto.getEmail().contains(".")){
                        throw new EmailInvalidoException();
                    }
                    if(dto.getNome().matches(".*\\d.*")){
                       throw new NomeInvalidoException();
                    }
                    if(Period.between(dto.getDataNascimento(), LocalDate.now()).getYears()<18){
                        throw new MenorDeIdadeException();
                    };
                    if(!dto.getTelefone().matches("\\d{10,11}")){
                        throw new TelefoneInvalidoException();
                    }

                })
                .choice()
                .when(header("operation").isEqualTo("create"))
                    .to("direct:save")
                .when(header("operation").isEqualTo("update"))
                    .to("direct:updateById");

        // Receptor de requisição POST
        from("direct:create").setHeader("operation", constant("create")).to("direct:validation");

        // Receptor de requisição PUT
        from("direct:update").setHeader("operation",constant("update")).to("direct:validation");

        // Redireciona a requisição POST validada para o serviço atômico
        from("direct:save").process(exchange -> {
                    UsuarioDTO dto = exchange.getIn().getBody(UsuarioDTO.class);
                    if(dto.getNome().contains(" ")){
                        log.info("Bem vindo, " + dto.getNome().substring(0, dto.getNome().indexOf(" ")) + "!");
                    }
                }).marshal("jsonDataFormat")
                .to("http://localhost:8080/usuarios?bridgeEndpoint=true").unmarshal("jsonDataFormat");

        // Recebe a requisição GET e redireciona para o serviço atômico
        from("direct:findById").process(exchange -> {
            Long id = exchange.getIn().getHeader("id", Long.class);
            if(id == null || id <= 0){
                throw new IdInvalidoException();
            }
        }).toD("http://localhost:8080/usuarios/${header.id}?bridgeEndpoint=true").unmarshal("jsonDataFormat");

        // Recebe a requisição DELETE e redireciona para o serviço atômico
        from("direct:deleteById").process(exchange -> {
            Long id = exchange.getIn().getHeader("id", Long.class);
            if(id == null || id <= 0){
                throw new IdInvalidoException();
            }
        }).toD("http://localhost:8080/usuarios/${header.id}?bridgeEndpoint=true").convertBodyTo(String.class).unmarshal("jsonDataFormat");

        // Redireciona arequisição PUT validada para o serviço atômico
        from("direct:updateById").process(exchange -> {
            Long id = exchange.getIn().getHeader("id", Long.class);
            if(id == null || id <= 0){
                throw new IdInvalidoException();
            }
        }).marshal("jsonDataFormat")
                .toD("http://localhost:8080/usuarios/${header.id}?bridgeEndpoint=true").unmarshal("jsonDataFormat");
        from("direct:saudacao").process(exchange -> {
            Long id = exchange.getIn().getHeader("id", Long.class);
            if(id == null || id <= 0){
                throw new IdInvalidoException();
            }
        }).toD("http://localhost:8080/usuarios/saudacao/${header.id}?bridgeEndpoint=true").convertBodyTo(String.class).unmarshal("jsonDataFormat");



    }
}
