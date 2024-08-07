package com.mmochat;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class MmoChatModClient implements ClientModInitializer {
    private static KeyBinding openMenuKey;

    @Override
    public void onInitializeClient() {
        // Регистрируем привязку клавиш
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Open menu MMOchat", // Идентификатор привязки клавиш
                InputUtil.Type.KEYSYM, // Тип ввода
                GLFW.GLFW_KEY_R, // Клавиша по умолчанию
                "MMOchat" // Категория привязки клавиш
        ));

        // Обрабатываем событие нажатия клавиш
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openMenuKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new MmoChatScreen());
                }
            }
        });
    }
}