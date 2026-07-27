package projeto.fishertour;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        
        String email = oAuth2User.getAttribute("email");
        String nome = oAuth2User.getAttribute("name");

        // Busca o usuário no banco pelo e-mail
        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario == null) {
            // Se não existe, cria um novo usuário com os dados do Google
            usuario = new Usuario();
            usuario.setNomeCompleto(nome);
            usuario.setEmail(email);
            usuario.setProvedor("GOOGLE");
            usuarioRepository.save(usuario);
        }
        // Redireciona para a Tela 3 (Painel do Tocantins) após logar com o Google

        // Salva o usuário na sessão para a tela de informações pessoais funcionar
        HttpSession session = request.getSession();
        session.setAttribute("usuarioLogado", usuario);

        // Redireciona para a página principal
        response.sendRedirect("/painel");
    }
}