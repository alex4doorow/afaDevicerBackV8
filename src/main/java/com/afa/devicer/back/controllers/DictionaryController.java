package com.afa.devicer.back.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.afa.devicer.back.controllers.internal.ControllerConstants.DICTIONARIES;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(DICTIONARIES)
@Tag(name = "Dictionary controller", description = "Operations pertaining to...")
public class DictionaryController {

}
