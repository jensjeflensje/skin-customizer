package dev.jensderuiter.skincustomizer.customizer.option;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@Setter
public class ComponentCategory implements Cloneable {

    private final int categoryId;
    private final double yOffset;
    private final List<ColorOption> colorOptions;
    private final List<Integer> components;

    @Override
    public ComponentCategory clone() {
        return new ComponentCategory(
                categoryId,
                yOffset,
                colorOptions.stream().map(ColorOption::clone).collect(Collectors.toCollection(ArrayList::new)),
                new ArrayList<>(components)
        );
    }
}
