package com.likelion12th.SWUProject1Team.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class TemplateParser {
    private final SpringTemplateEngine templateEngine;

    public String parserHtmlFileToString(String templateName, Map<String, Object> vars) {
        var context = new Context();
        context.setVariables(vars);

        return templateEngine.process(templateName, context);
    }
}
