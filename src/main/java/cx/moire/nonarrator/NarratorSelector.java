package cx.moire.nonarrator;

import com.mojang.text2speech.Narrator;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Narrator.class) public interface NarratorSelector {}
