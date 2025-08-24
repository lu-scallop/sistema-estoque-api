package com.personal.crudapi.controller;

import com.personal.crudapi.entity.ReservaEstoque;
import com.personal.crudapi.service.ReservaEstoqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservas")
public class ReservaEstoqueController {

    @Autowired
    private ReservaEstoqueService service;




}
