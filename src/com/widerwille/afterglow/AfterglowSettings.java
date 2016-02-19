package com.widerwille.afterglow;


import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Tag;
import org.jetbrains.annotations.Nullable;


@State(
        name = "AfterglowSettings",
        storages = @Storage(id = "Afterglow", file = "$APP_CONFIG$/afterglow.xml")
)
public class AfterglowSettings implements PersistentStateComponent<AfterglowSettings> {

    private static final String THEME_IMPORT_KEY = "com.widerwille.Afterglow.theme";

    @Tag
    public String theme;

    public AfterglowSettings() {

        PropertiesComponent component = PropertiesComponent.getInstance();
        theme = component.getValue(THEME_IMPORT_KEY, "Default");
    }

    @Nullable
    @Override
    public AfterglowSettings getState() {
        return this;
    }

    @Override
    public void loadState(AfterglowSettings afterglowSettings) {
        XmlSerializerUtil.copyBean(afterglowSettings, this);
    }

    public static AfterglowSettings getInstance() {
        return ServiceManager.getService(AfterglowSettings.class);
    }
}
