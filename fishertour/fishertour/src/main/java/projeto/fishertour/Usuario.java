package projeto.fishertour;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios") // Corrigido para o plural para corresponder à tabela do banco
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeCompleto;
    private String email;
    private String senha;
    
    private Integer idade;
    private String dataNascimento;
    private String cpf;
    private String endereco;
    private String cep;
    private String provedor;

    private String codigoRecuperacao;
    private java.time.LocalDateTime expiracaoCodigo;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public Integer getIdade() { return idade; }
    public void setIdade(Integer idade) { this.idade = idade; }

    public String getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(String dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getProvedor() { return provedor; }
    public void setProvedor(String provedor) { this.provedor = provedor; }

    public String getCodigoRecuperacao() { return codigoRecuperacao; }
    public void setCodigoRecuperacao(String codigoRecuperacao) { this.codigoRecuperacao = codigoRecuperacao; }

    public java.time.LocalDateTime getExpiracaoCodigo() { return expiracaoCodigo; }
    public void setExpiracaoCodigo(java.time.LocalDateTime expiracaoCodigo) { this.expiracaoCodigo = expiracaoCodigo; }
}