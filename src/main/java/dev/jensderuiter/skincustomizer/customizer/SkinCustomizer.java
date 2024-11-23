package dev.jensderuiter.skincustomizer.customizer;

import com.google.gson.Gson;
import dev.jensderuiter.skincustomizer.CustomizerPlugin;
import dev.jensderuiter.skincustomizer.Util;
import dev.jensderuiter.skincustomizer.customizer.option.SkinCustomizerOptions;
import dev.jensderuiter.skincustomizer.customizer.preview.SkinPreview;
import dev.jensderuiter.skincustomizer.customizer.ui.ColoredScrollingButtons;
import dev.jensderuiter.skincustomizer.customizer.ui.base.InteractableButton;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.property.InputDataResult;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.api.storage.PlayerStorage;
import net.skinsrestorer.api.storage.SkinStorage;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineskin.GenerateOptions;
import org.mineskin.data.Visibility;
import org.mineskin.exception.MineSkinRequestException;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class SkinCustomizer {

    @Getter
    @Setter
    private static SkinCustomizerOptions defaultOptions;

    private static final String GENERATION_ENDPOINT = "https://jens.skin/api/generate/";
    private Gson gson = new Gson();
    public static final HashMap<String, TextureData> textureCache = new HashMap<>();

    @Getter
    @Setter
    private HashMap<String, Map> options;

    @Getter
    private SkinPreview preview;
    private final Location previewLocation;

    private TextureData skinData;

    private List<Destroyable> uiItems;

    private boolean processing;

    private InteractableButton applyButton;

    private final ItemStack checkHead = Util.getSkull(CustomizerPlugin.getTextureConfig().getString("check"));
    private final ItemStack loadingHead = Util.getSkull(CustomizerPlugin.getTextureConfig().getString("loading"));

    private final SkinCustomizerOptions config;


    public SkinCustomizer(Location location) {
        this(location, defaultOptions);
    }

    public SkinCustomizer(Location location, SkinCustomizerOptions config) {
        CustomizerPlugin.getCustomizers().add(this);
        this.spawnPreview();
        this.config = config;
        this.options = new HashMap<>();

        config.getCategories().forEach(category -> {
            this.options.put(
                    String.valueOf(category.getCategoryId()),
                    new HashMap<>(Map.of("value", category.getComponents().get(0)))
            );
        });

        this.uiItems = new ArrayList<>();
        this.processing = false;

        // summon the preview in opposite direction
        previewLocation = location.clone();
        previewLocation.setPitch(0);
        previewLocation.add(
                previewLocation.getDirection().getX(),
                0,
                previewLocation.getDirection().getZ()
        );
        previewLocation.setYaw(location.getYaw() - 180);
    }

    public void spawnPreview() {
        // if we return here, this method will be called again once Citizens initializes
        if (!CustomizerPlugin.isCitizensEnabled()) {
            this.preview = null;
            return;
        };
        this.preview = new SkinPreview(this);
    }

    private void ifNotProcessing(Runnable runnable) {
        if (!processing) runnable.run();
    }

    @SneakyThrows
    public void applyToPlayer(Player player) {
        // create hash to identify
        String skinHash = createSkinHash();

        // store skin in skinrestorer storage
        SkinStorage skinStorage = CustomizerPlugin.getSkinRestorer().getSkinStorage();
        skinStorage.setCustomSkinData(
                skinHash,
                SkinProperty.of(this.skinData.getValue(), this.skinData.getSignature())
        );
        PlayerStorage playerStorage = CustomizerPlugin.getSkinRestorer().getPlayerStorage();
        Optional<InputDataResult> result = skinStorage.findOrCreateSkinData(skinHash);

        // if all goes well: apply
        result.ifPresent(inputDataResult -> {
            playerStorage.setSkinIdOfPlayer(player.getUniqueId(), inputDataResult.getIdentifier());
            try {
                CustomizerPlugin.getSkinRestorer().getSkinApplier(Player.class).applySkin(player);
            } catch (DataRequestException e) {
                throw new RuntimeException(e);
            }
        });

    }

    @SneakyThrows
    public String createSkinHash() {
        MessageDigest m = MessageDigest.getInstance("MD5");
        m.update(gson.toJson(this.options).getBytes(StandardCharsets.UTF_8));
        BigInteger bigInt = new BigInteger(1,m.digest());
        return bigInt.toString(16);
    }

    private void summonUI() {
        // apply button
        Location applyButtonLocation = previewLocation.clone();
        applyButtonLocation.setYaw(applyButtonLocation.getYaw() - 180);
        applyButtonLocation.add(0, 2.4, 0);

        applyButton = new InteractableButton(
                applyButtonLocation,
                checkHead,
                this::applyToPlayer
        );
        this.uiItems.add(applyButton);

        config.getCategories().forEach(category -> {
            String categoryId = String.valueOf(category.getCategoryId());
            this.uiItems.add(
                    new ColoredScrollingButtons<>(
                            previewLocation,
                            category.getYOffset(),
                            category.getComponents(),
                            (newId) -> ifNotProcessing(() -> {
                                Map subOptions = this.options.getOrDefault(categoryId, new HashMap<>());
                                subOptions.put("value", newId);
                                this.options.put(categoryId, subOptions);
                                this.updateSkinData();
                            }),
                            (color) -> ifNotProcessing(() -> {
                                Map<String, Object> subOptions = this.options.getOrDefault(categoryId, new HashMap<>());
                                subOptions.put("color", color);
                                this.options.put(categoryId, subOptions);
                                this.updateSkinData();
                            }),
                            category.getColorOptions()
                    )
            );
        });
    }

    public void summon() {
        this.getSkinAndAction(
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        summonUI();

                        preview.setSkinData(skinData);
                        preview.summon(previewLocation);
                    }
                }
        );
    }

    public void updateSkinData() {
        this.setProcessing(true);
        this.getSkinAndAction(
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            preview.setSkinData(skinData);
                        } finally {
                            setProcessing(false);
                        }

                    }
                }
        );
    }

    private void setProcessing(boolean newState) {
        this.processing = newState;
        applyButton.setItemStack(this.processing ? this.loadingHead : this.checkHead);
    }

    private void getSkinAndAction(BukkitRunnable runnable) {
        this.getSkinData()
                .thenAccept((skinData -> {
                    runnable.runTask(CustomizerPlugin.getInstance());
                })).exceptionally(throwable -> {
                    throwable.printStackTrace();
                    setProcessing(false);
                    return null;
                });
    }

    public void destroy() {
        this.uiItems.forEach(Destroyable::destroy);
    }

    private CompletableFuture<TextureData> getSkinData() {
        CompletableFuture<TextureData> future = new CompletableFuture<>();

        String skinHash = createSkinHash();

        if (textureCache.containsKey(skinHash)) {
            TextureData textureData = textureCache.get(skinHash);
            skinData = textureData;
            future.complete(textureData);
        } else {
            StringEntity requestEntity = new StringEntity(
                    this.gson.toJson(options),
                    ContentType.APPLICATION_JSON);

            HttpPost postMethod = new HttpPost(GENERATION_ENDPOINT);
            postMethod.setEntity(requestEntity);

            CloseableHttpClient httpclient = HttpClients.createDefault();
            try {
                HttpResponse rawResponse = httpclient.execute(postMethod);
                InputStream skinRes = rawResponse.getEntity().getContent();

                GenerateOptions options = GenerateOptions.create()
                        .name(skinHash.substring(0, 24))
                        .visibility(Visibility.UNLISTED);
                CustomizerPlugin.getMineskin().generateUpload(skinRes, options)
                        .thenAccept(response -> {
                            TextureData data = new TextureData(
                                    response.getSkin().data().texture().value(),
                                    response.getSkin().data().texture().signature(),
                                    skinHash
                            );
                            textureCache.put(skinHash, data);
                            skinData = data;
                            future.complete(data);
                        })
                        .exceptionally(throwable -> {
                            if (throwable.getCause() instanceof MineSkinRequestException req) {
                                System.out.println(req.getResponse());
                            }
                            future.completeExceptionally(throwable);
                            return null;
                        });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return future;
    }

}
