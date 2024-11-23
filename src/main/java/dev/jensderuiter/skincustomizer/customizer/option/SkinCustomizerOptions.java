package dev.jensderuiter.skincustomizer.customizer.option;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SkinCustomizerOptions implements Cloneable {
    private List<ComponentCategory> categories;

    @Override
    public SkinCustomizerOptions clone() {
        return new SkinCustomizerOptions(categories.stream().map(ComponentCategory::clone).toList());
    }
}
