package com.mahara.stocker.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Scope("prototype")
public class FXMLLoaderUtil implements ApplicationContextAware {
    private ApplicationContext context;
    private FXMLLoader fxmlLoader;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    public void initialize(String resource) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(resource));
        loader.setControllerFactory(this.context::getBean);
        loader.load();
        this.fxmlLoader = loader;
    }

    public <T> T getController() throws IllegalStateException {
        if (this.fxmlLoader == null) {
            throw new IllegalStateException("FXMLLoaderUtil is not initialed.");
        }
        return (T)this.fxmlLoader.getController();
    }

    public Parent getView() throws IllegalStateException {
        if (this.fxmlLoader == null) {
            throw new IllegalStateException("FXMLLoaderUtil is not initialed.");
        }
        return this.fxmlLoader.getRoot();
    }
}
