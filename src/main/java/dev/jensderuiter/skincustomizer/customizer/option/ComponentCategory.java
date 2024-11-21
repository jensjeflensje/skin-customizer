package dev.jensderuiter.skincustomizer.customizer.option;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ComponentCategory {

    private final int categoryId;
    private final double yOffset;
    private final List<ColorOption> colorOptions;
    private final List<Integer> components;

}
