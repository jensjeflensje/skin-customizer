package dev.jensderuiter.skincustomizer;

import dev.jensderuiter.skincustomizer.command.StartCustomizerCommand;
import dev.jensderuiter.skincustomizer.customizer.SkinCustomizer;
import dev.jensderuiter.skincustomizer.customizer.TextureData;
import dev.jensderuiter.skincustomizer.customizer.option.ColorOption;
import dev.jensderuiter.skincustomizer.customizer.option.ComponentCategory;
import dev.jensderuiter.skincustomizer.customizer.option.SkinCustomizerOptions;
import dev.jensderuiter.skincustomizer.customizer.ui.base.InteractableButton;
import dev.jensderuiter.skincustomizer.listener.CitizensListener;
import dev.jensderuiter.skincustomizer.listener.InteractionClickListener;
import lombok.Getter;
import lombok.Setter;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.MenuFunctionListener;
import org.mineskin.ApacheRequestHandler;
import org.mineskin.MineSkinClient;

import java.util.ArrayList;
import java.util.List;

import static dev.jensderuiter.skincustomizer.customizer.SkinCustomizer.textureCache;

public final class CustomizerPlugin extends JavaPlugin {

    @Getter
    private static MineSkinClient mineskin;

    @Getter
    private static SkinsRestorer skinRestorer;

    @Getter
    private static CustomizerPlugin instance;

    @Getter
    private static List<SkinCustomizer> customizers;

    @Getter
    @Setter
    private static boolean citizensEnabled;

    @Getter
    private static ConfigurationSection textureConfig;

    @Getter
    private static SkinCustomizerOptions skinCustomizerOptions;

    @Override
    public void onEnable() {
        instance = this;
        customizers = new ArrayList<>();

        getServer().getPluginManager().registerEvents(new InteractionClickListener(), this);
        getServer().getPluginManager().registerEvents(new CitizensListener(), this);
        getServer().getPluginManager().registerEvents(new MenuFunctionListener(), this);

        getCommand("customizer").setExecutor(new StartCustomizerCommand());

        this.saveDefaultConfig();
        FileConfiguration config = getConfig();

        mineskin = MineSkinClient.builder()
                .requestHandler(ApacheRequestHandler::new)
                .apiKey(config.getString("mineskin_key"))
                .build();
        skinRestorer = SkinsRestorerProvider.get();


        ConfigurationSection cacheSection = this.getConfig().getConfigurationSection("cache");
        for (String key : cacheSection.getKeys(false)) {
            ConfigurationSection textureSection = cacheSection.getConfigurationSection(key);
            TextureData textureData = new TextureData(
                    textureSection.getString("value"),
                    textureSection.getString("signature"),
                    key
            );
            textureCache.put(key, textureData);
        }
        textureConfig = this.getConfig().getConfigurationSection("textures");

        List<ComponentCategory> categories = new ArrayList<>();
        ConfigurationSection categoriesSection = this.getConfig().getConfigurationSection("categories");
        for (String key : categoriesSection.getKeys(false)) {
            ConfigurationSection categorySection = categoriesSection.getConfigurationSection(key);
            int categoryId = categorySection.getInt("id");
            double yOffset = categorySection.getDouble("y_offset");
            List<Integer> componentIds = categorySection.getIntegerList("components");
            ConfigurationSection colorSection = categorySection.getConfigurationSection("colors");
            List<ColorOption> colors = colorSection.getKeys(false).stream().map(colorKey -> {
                Material icon = Material.getMaterial(colorKey);
                List<String> colorList = colorSection.getStringList(colorKey).stream().toList();
                return new ColorOption(icon, colorList);
            }).toList();
            categories.add(new ComponentCategory(categoryId, yOffset, colors, componentIds));
        }
        skinCustomizerOptions = SkinCustomizerOptions.builder().categories(categories).build();
    }

    @Override
    public void onDisable() {
        customizers.forEach(SkinCustomizer::destroy);
        customizers.clear();

        InteractableButton.getInstances().clear();

        ConfigurationSection cacheSection = this.getConfig().getConfigurationSection("cache");
        for (TextureData texture : textureCache.values()) {
            ConfigurationSection textureSection = cacheSection.createSection(texture.getHash());
            textureSection.set("value", texture.getValue());
            textureSection.set("signature", texture.getSignature());
        }
        this.saveConfig();
    }
}
