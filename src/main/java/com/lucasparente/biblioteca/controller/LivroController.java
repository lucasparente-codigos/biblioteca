package com.lucasparente.biblioteca.controller;

import com.lucasparente.biblioteca.model.Livro;
import com.lucasparente.biblioteca.service.AutorService;
import com.lucasparente.biblioteca.service.LivroService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/livros")
public class LivroController {

    // MELHORIA: injeção via construtor
    private final LivroService livroService;
    private final AutorService autorService;

    public LivroController(LivroService livroService, AutorService autorService) {
        this.livroService = livroService;
        this.autorService = autorService;
    }

    @GetMapping
    public String listarLivros(Model model) {
        List<Livro> livros = livroService.findAll();
        model.addAttribute("livros", livros);
        return "livros/lista";
    }

    @GetMapping("/novo")
    public String novoLivro(Model model) {
        model.addAttribute("livro", new Livro());
        model.addAttribute("autores", autorService.findAll());
        return "livros/formulario";
    }

    @PostMapping("/novo")
    public String salvarLivro(@Valid @ModelAttribute("livro") Livro livro,
                              BindingResult result,
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute("autores", autorService.findAll());
            return "livros/formulario";
        }
        livroService.save(livro);
        return "redirect:/livros";
    }

    @GetMapping("/editar/{id}")
    public String editarLivro(@PathVariable Long id, Model model) {
        Livro livro = livroService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Livro inválido: " + id));
        model.addAttribute("livro", livro);
        model.addAttribute("autores", autorService.findAll());
        return "livros/formulario";
    }

    @PostMapping("/editar/{id}")
    public String atualizarLivro(@PathVariable Long id,
                                 @Valid @ModelAttribute("livro") Livro livro,
                                 BindingResult result,
                                 Model model) {
        if (result.hasErrors()) {
            model.addAttribute("autores", autorService.findAll());
            return "livros/formulario";
        }
        livro.setId(id);
        livroService.save(livro);
        return "redirect:/livros";
    }

    // CORREÇÃO: mapeamento alterado para POST (mesma razão do AutorController)
    @PostMapping("/excluir/{id}")
    public String excluirLivro(@PathVariable Long id) {
        livroService.deleteById(id);
        return "redirect:/livros";
    }
}
