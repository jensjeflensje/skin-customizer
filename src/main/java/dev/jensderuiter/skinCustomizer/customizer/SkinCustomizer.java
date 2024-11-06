package dev.jensderuiter.skinCustomizer.customizer;

import com.google.gson.Gson;
import dev.jensderuiter.skinCustomizer.CustomizerPlugin;
import dev.jensderuiter.skinCustomizer.Util;
import dev.jensderuiter.skinCustomizer.customizer.preview.SkinPreview;
import dev.jensderuiter.skinCustomizer.customizer.ui.ColoredScrollingButtons;
import dev.jensderuiter.skinCustomizer.customizer.ui.ScrollingButtons;
import dev.jensderuiter.skinCustomizer.customizer.ui.base.InteractableButton;
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

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class SkinCustomizer {

    private static final String GENERATION_ENDPOINT = "https://jens.skin/api/generate/";
    private Gson gson = new Gson();
    public static final HashMap<String, TextureData> textureCache = new HashMap<>();

    @Getter
    @Setter
    private HashMap<String, HashMap> options;

    private final SkinPreview preview;
    private final Location previewLocation;

    private TextureData skinData;

    private List<Destroyable> uiItems;

    private boolean processing;

    private InteractableButton applyButton;

    private ItemStack checkHead = Util.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDk5ODBjMWQyMTE4MDlhOWI2NTY1MDg4ZjU2YTM4ZjJlZjQ5MTE1YzEwNTRmYTY2MjQ1MTIyZTllZWVkZWNjMiJ9fX0=");
    private ItemStack refreshHead = Util.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTg4N2NjMzg4YzhkY2ZjZjFiYThhYTVjM2MxMDJkY2U5Y2Y3YjFiNjNlNzg2YjM0ZDRmMWMzNzk2ZDNlOWQ2MSJ9fX0=");

    public SkinCustomizer(Location location) {
        CustomizerPlugin.getCustomizers().add(this);
        this.preview = new SkinPreview(this);
        this.options = new HashMap<>();
        this.uiItems = new ArrayList<>();
        this.processing = false;

        previewLocation = location.clone();
        previewLocation.setPitch(0);
        previewLocation.add(
                previewLocation.getDirection().getX(),
                0,
                previewLocation.getDirection().getZ()
        );
        previewLocation.setYaw(location.getYaw() - 180);


        // apply button
        Location applyButtonLocation = previewLocation.clone();
        applyButtonLocation.setYaw(applyButtonLocation.getYaw() - 180);
        applyButtonLocation.add(0, 2.4, 0);

        applyButton = new InteractableButton(
                applyButtonLocation,
                Util.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDk5ODBjMWQyMTE4MDlhOWI2NTY1MDg4ZjU2YTM4ZjJlZjQ5MTE1YzEwNTRmYTY2MjQ1MTIyZTllZWVkZWNjMiJ9fX0="),
                this::applyToPlayer
        );
        this.uiItems.add(applyButton);

        // hair
        this.uiItems.add(
                new ColoredScrollingButtons<>(
                        previewLocation,
                        1.9,
                        List.of(18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 76, 77, 78, 79, 80, 81),
                        (newId) -> ifNotProcessing(() -> {
                            HashMap<String, Object> subOptions = this.options.getOrDefault("4", new HashMap<>());
                            subOptions.put("value", newId);
                            this.options.put("4", subOptions);
                            this.updateSkinData();
                        }),
                        (color) -> ifNotProcessing(() -> {
                            HashMap<String, Object> subOptions = this.options.getOrDefault("4", new HashMap<>());
                            subOptions.put("color", color);
                            this.options.put("4", subOptions);
                            this.updateSkinData();
                        }),
                        ColoredScrollingButtons.HAIR_OPTIONS
                )
        );

        // hair
        this.uiItems.add(
            new ColoredScrollingButtons<>(
                    previewLocation,
                    1.5,
                    List.of(1, 2, 3, 4, 49, 50, 51, 52, 66, 67, 68, 69, 70, 71, 72, 73),
                    (newId) -> ifNotProcessing(() -> {
                        HashMap<String, Object> subOptions = this.options.getOrDefault("1", new HashMap<>());
                        subOptions.put("value", newId);
                        this.options.put("1", subOptions);
                        this.updateSkinData();
                    }),
                    (color) -> ifNotProcessing(() -> {
                        HashMap<String, Object> subOptions = this.options.getOrDefault("1", new HashMap<>());
                        subOptions.put("color", color);
                        this.options.put("1", subOptions);
                        this.updateSkinData();
                    }),
                    ColoredScrollingButtons.EYE_OPTIONS
            )
        );

        // shirt
        this.uiItems.add(
            new ColoredScrollingButtons<>(
                    previewLocation,
                    1.1,
                    List.of(42, 43, 61),
                    (newId) -> ifNotProcessing(() -> {
                        HashMap<String, Object> subOptions = this.options.getOrDefault("8", new HashMap<>());
                        subOptions.put("value", newId);
                        this.options.put("8", subOptions);
                        this.updateSkinData();
                    }),
                    (color) -> ifNotProcessing(() -> {
                        HashMap<String, Object> subOptions = this.options.getOrDefault("8", new HashMap<>());
                        subOptions.put("color", color);
                        this.options.put("8", subOptions);
                        this.updateSkinData();
                    }),
                    ColoredScrollingButtons.SHIRT_OPTIONS
            )
        );

        // pants
        this.uiItems.add(
            new ColoredScrollingButtons<>(
                    previewLocation,
                    0.7,
                    List.of(40, 64),
                    (newId) -> ifNotProcessing(() -> {
                        HashMap<String, Object> subOptions = this.options.getOrDefault("9", new HashMap<>());
                        subOptions.put("value", newId);
                        this.options.put("9", subOptions);
                        this.updateSkinData();
                    }),
                    (color) -> ifNotProcessing(() -> {
                        HashMap<String, Object> subOptions = this.options.getOrDefault("9", new HashMap<>());
                        subOptions.put("color", color);
                        this.options.put("9", subOptions);
                        this.updateSkinData();
                    }),
                    ColoredScrollingButtons.PANT_OPTIONS
            )
        );

        // shoes
        this.uiItems.add(
            new ColoredScrollingButtons<>(
                    previewLocation,
                    0.3,
                    List.of(39, 95),
                    (newId) -> ifNotProcessing(() -> {
                        HashMap<String, Object> subOptions = this.options.getOrDefault("10", new HashMap<>());
                        subOptions.put("value", newId);
                        this.options.put("10", subOptions);
                        this.updateSkinData();
                    }),
                    (color) -> ifNotProcessing(() -> {
                        HashMap<String, Object> subOptions = this.options.getOrDefault("10", new HashMap<>());
                        subOptions.put("color", color);
                        this.options.put("10", subOptions);
                        this.updateSkinData();
                    }),
                    ColoredScrollingButtons.SHOE_OPTIONS
            )
        );
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

    public void summon() {
        this.getSkinAndAction(
                new BukkitRunnable() {
                    @Override
                    public void run() {
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
        applyButton.setItemStack(this.processing ? this.refreshHead : this.checkHead);
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
        Bukkit.getLogger().info(this.gson.toJson(options));
        Bukkit.getLogger().info(skinHash);

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
                        .name(skinHash)
                        .visibility(Visibility.UNLISTED);
                CustomizerPlugin.getMineskin().generateUpload(skinRes, options)
                        .thenAccept(response -> {
                            Bukkit.getLogger().info("got response " + response.getMessageOrError());
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
