// src/main/java/com/lucasparente/biblioteca/controller/AutorController.java
package com.lucasparente.biblioteca.controller;

import com.lucasparente.biblioteca.model.Autor;
import com.lucasparente.biblioteca.service.AutorService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/autores")
public class AutorController {

    // MELHORIA: injeção via construtor — deixa as dependências explícitas
    // e facilita testes unitários (sem necessidade de reflection)
    private final AutorService autorService;

    public AutorController(AutorService autorService) {
        this.autorService = autorService;
    }

    @GetMapping
    public String listarAutores(Model model) {
        List<Autor> autores = autorService.findAll();
        model.addAttribute("autores", autores);
        return "autores/lista";
    }

    @GetMapping("/novo")
    public String novoAutor(Model model) {
        model.addAttribute("autor", new Autor());
        return "autores/formulario";
    }

    @PostMapping("/novo")
    public String salvarAutor(@Valid @ModelAttribute("autor") Autor autor, BindingResult result) {
        if (result.hasErrors()) {
            return "autores/formulario";
        }
        autorService.save(autor);
        return "redirect:/autores";
    }

    @GetMapping("/editar/{id}")
    public String editarAutor(@PathVariable Long id, Model model) {
        Autor autor = autorService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Autor inválido: " + id));
        model.addAttribute("autor", autor);
        return "autores/formulario";
    }

    @PostMapping("/editar/{id}")
    public String atualizarAutor(@PathVariable Long id,
                                 @Valid @ModelAttribute("autor") Autor autor,
                                 BindingResult result) {
        if (result.hasErrors()) {
            return "autores/formulario";
        }
        autor.setId(id);
        autorService.save(autor);
        return "redirect:/autores";
    }

    // CORREÇÃO: mapeamento alterado para POST
    // Excluir via GET é semanticamente errado e arriscado
    // (browsers podem pré-carregar links e disparar a exclusão)
    @PostMapping("/excluir/{id}")
    public String excluirAutor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            autorService.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            // CORREÇÃO: captura violação de FK (autor com livros associados)
            // Sem isso, o usuário veria uma tela de erro genérica do Spring
            redirectAttributes.addFlashAttribute("erro",
                    "Não é possível excluir este autor pois ele possui livros cadastrados.");
        }
        return "redirect:/autores";
    }
}
