package cx.moire.nonarrator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class NarratorDisabler implements IMixinConfigPlugin {

	@Override public void onLoad(final String s) {}

	@Override public String getRefMapperConfig() { return null; }

	@Override public boolean shouldApplyMixin(final String clazz, final String s) { return true; }

	@Override public void acceptTargets(final Set<String> m, final Set<String> o) {}

	@Override public List<String> getMixins() { return null; }

	@Override public void preApply(final String t, final ClassNode clazz, final String m, final IMixinInfo i) {
		for (final MethodNode method : clazz.methods) if ("getNarrator".equals(method.name)) {
			// have to insert in reverse order
			method.instructions.insert(new InsnNode(Opcodes.ARETURN));

			// on some minecraft versions, we load from the field EMPTY, on others we have to create a dummy narrator
			for (final FieldNode field : clazz.fields) if ("EMPTY".equals(field.name)) { // field is present
				method.instructions.insert(new FieldInsnNode(Opcodes.GETSTATIC, "com/mojang/text2speech/Narrator",
						"EMPTY", "Lcom/mojang/text2speech/Narrator;"));
				return;
			}

			// field not there - create dummy narrator
			method.instructions.insert(new MethodInsnNode(Opcodes.INVOKESPECIAL,
					"com/mojang/text2speech/NarratorDummy", "<init>", "()V", false));
			method.instructions.insert(new InsnNode(Opcodes.DUP));
			method.instructions.insert(new TypeInsnNode(Opcodes.NEW, "com/mojang/text2speech/NarratorDummy"));

			return;
		}
	}

	@Override public void postApply(final String t, final ClassNode c, String m, IMixinInfo i) {}
}
