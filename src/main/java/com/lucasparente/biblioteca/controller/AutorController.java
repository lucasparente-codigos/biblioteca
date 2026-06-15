package com.lucasparente.biblioteca.controller;

import com.lucasparente.biblioteca.model.Autor;
import com.lucasparente.biblioteca.service.AutorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/autores")
public class AutorController {

    @Autowired
    private AutorService autorService;

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
    public String editarAutor(@PathVariable("id") Long id, Model model) {
        Autor autor = autorService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Autor inválido:" + id));
        model.addAttribute("autor", autor);
        return "autores/formulario";
    }

    @PostMapping("/editar/{id}")
    public String atualizarAutor(@PathVariable("id") Long id, @Valid @ModelAttribute("autor") Autor autor, BindingResult result) {
        if (result.hasErrors()) {
            return "autores/formulario";
        }
        autor.setId(id);
        autorService.save(autor);
        return "redirect:/autores";
    }

    @GetMapping("/excluir/{id}")
    public String excluirAutor(@PathVariable("id") Long id) {
        autorService.deleteById(id);
        return "redirect:/autores";
    }
}
