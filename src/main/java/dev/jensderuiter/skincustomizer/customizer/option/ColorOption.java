package dev.jensderuiter.skincustomizer.customizer.option;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class ColorOption implements Cloneable {
    private Material icon;
    private List<String> colors;

    @Override
    public ColorOption clone() {
        return new ColorOption(icon, new ArrayList<>(colors));
    }
}

