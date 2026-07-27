package projeto.fishertour;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private JavaMailSender mailSender;

    // 1. Mostra a página de login
    @GetMapping("/login")
    public String mostrarTelaLogin() {
        return "login"; // Vai procurar o arquivo login.html na pasta templates
    }

    @GetMapping("/")
    public String paginaInicial(HttpSession session) {
        Object usuarioLogado = session.getAttribute("usuarioLogado");
        
        // Se não houver usuário na sessão, redireciona para o login
        if (usuarioLogado == null) {
            return "redirect:/login";
        }
        
        return "index"; // Retorna o index.html
    }

    // 2. Recebe os dados do formulário de CADASTRO
    @PostMapping("/cadastrar")
    public String cadastrarUsuario(Usuario usuario) {
        // Salva no banco de dados
        repository.save(usuario);
        // Redireciona de volta para o login para ele poder entrar
        return "redirect:/login"; 
    }

    // 3. Recebe os dados do formulário de LOGIN
 @PostMapping("/entrar")
    public String fazerLogin(@RequestParam String email, @RequestParam String senha, HttpSession session, Model model) {
        email = email.trim();
        Usuario usuarioEncontrado = repository.findByEmail(email);

        if (usuarioEncontrado == null) {
            model.addAttribute("erro", "E-mail não encontrado!");
            return "login";
        }

        if ("GOOGLE".equals(usuarioEncontrado.getProvedor())) {
            model.addAttribute("erro", "Esta conta usa o login do Google. Clique em 'Entrar com Google'!");
            return "login";
        }

        if (usuarioEncontrado.getSenha() != null && usuarioEncontrado.getSenha().equals(senha)) {
            session.setAttribute("usuarioLogado", usuarioEncontrado);
            return "redirect:/dashboard"; // <-- Redireciona para a Tela 3 (Tocantins)
        } else {
            model.addAttribute("erro", "Senha incorreta!");
            return "login"; 
        }
    }
@GetMapping("/informacoes-pessoais")
public String telaInformacoesPessoais(HttpSession session, Model model) {
    Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
    
    // Se não estiver logado, manda pro login
    if (usuario == null) {
        return "redirect:/login";
    }
    
    model.addAttribute("usuario", usuario);
    return "informacoes-pessoais"; // Vai abrir o arquivo informacoes-pessoais.html
}

    // --- FLUXO DE RECUPERAÇÃO DE SENHA ---

    // 4. Mostra a tela onde o usuário digita o e-mail para recuperar a senha
    @GetMapping("/recuperar-senha")
    public String telaRecuperarSenha() {
        return "recuperar-senha";
    }
    
    @GetMapping("/logout")
    public String sairConta(HttpSession session) {
    session.invalidate(); // Limpa a sessão
    return "redirect:/login";
}

    // 5. Gera o código de 6 dígitos, salva no banco e envia por e-mail
    @PostMapping("/enviar-codigo")
    public String enviarCodigo(@RequestParam("email") String email, Model model) {
        Usuario usuario = repository.findByEmail(email);
        
        if (usuario == null) {
            model.addAttribute("erro", "E-mail não encontrado no sistema.");
            return "recuperar-senha";
        }

        // Gera um código numérico aleatório de 6 dígitos
        String codigo = String.valueOf((int) ((Math.random() * 900000) + 100000));
        
        usuario.setCodigoRecuperacao(codigo);
        usuario.setExpiracaoCodigo(LocalDateTime.now().plusMinutes(15)); // Validade de 15 minutos
        repository.save(usuario);

        // Dispara o e-mail com o código
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo(email);
        mensagem.setSubject("Fisher Tour - Código de Recuperação de Senha");
        mensagem.setText("Olá, " + usuario.getNomeCompleto() + "!\n\nSeu código de recuperação de senha é: " + codigo + "\n\nEste código expira em 15 minutos.");
        
        try {
            mailSender.send(mensagem);
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao enviar o e-mail. Verifique as configurações de SMTP.");
            return "recuperar-senha";
        }

        model.addAttribute("email", email);
        return "redefinir-senha"; // Vai para a tela de digitar o código e a nova senha
    }

    // 6. Valida o código inserido e atualiza a nova senha no banco
    @PostMapping("/salvar-nova-senha")
    public String salvarNovaSenha(@RequestParam("email") String email,
                                   @RequestParam("codigo") String codigo,
                                   @RequestParam("novaSenha") String novaSenha,
                                   Model model) {
        Usuario usuario = repository.findByEmail(email);
        
        if (usuario == null || !codigo.equals(usuario.getCodigoRecuperacao()) || 
            LocalDateTime.now().isAfter(usuario.getExpiracaoCodigo())) {
            
            model.addAttribute("erro", "Código inválido ou expirado!");
            model.addAttribute("email", email);
            return "redefinir-senha";
        }

        // Atualiza a senha e limpa os campos temporários de recuperação
        usuario.setSenha(novaSenha);
        usuario.setCodigoRecuperacao(null);
        usuario.setExpiracaoCodigo(null);
        repository.save(usuario);

        return "redirect:/login";
    }
}