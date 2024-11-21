package dev.jensderuiter.skincustomizer.customizer.option;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SkinCustomizerOptions {
    private List<ComponentCategory> categories;
}
