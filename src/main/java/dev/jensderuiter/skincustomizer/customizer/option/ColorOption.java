package dev.jensderuiter.skincustomizer.customizer.option;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

import java.util.List;

@AllArgsConstructor
@Getter
public class ColorOption {
    private Material icon;
    private List<String> colors;
}

