package com.mahara.fxgenerator.util;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

public class FreeMarkerConfig {
    private static FreeMarkerConfig fmc = new FreeMarkerConfig();
    private Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
    private FreeMarkerConfig(){
    }
    public static FreeMarkerConfig instance() {
        return fmc;
    }
    public void init(){
        cfg.setClassLoaderForTemplateLoading(this.getClass().getClassLoader(), "");

        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setInterpolationSyntax(freemarker.template.Configuration.SQUARE_BRACKET_INTERPOLATION_SYNTAX);
        cfg.setLogTemplateExceptions(false);
        cfg.setOutputEncoding("UTF-8");
    }

    public Configuration cfg() {
        return cfg;
    }
}
