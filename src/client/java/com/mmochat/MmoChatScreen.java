package com.mmochat;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.text.SpruceTextFieldWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MmoChatScreen extends SpruceScreen {
    private SpruceTextFieldWidget textField;
    private List<String> allPlayers;
    private List<String> filteredPlayers;
    private int scrollOffset = 0;
    private int maxVisibleItems = 5;
    private int itemHeight = 20;

    public MmoChatScreen() {
        super(Text.literal("Custom Screen with Buttons"));
    }

    @Override
    protected void init() {
        super.init();

        int buttonSize = 26;
        int spacing = 6; // Расстояние между кнопками
        int totalWidth = buttonSize * 5 + spacing * 4; // Общая ширина кнопок с учетом расстояния между ними
        int centerX = width / 2 - totalWidth / 2; // Начальная позиция для первой кнопки
        int centerY = height / 2 - 90; // Центр экрана по вертикали, поднят на 10 пикселей

        // Размещение кнопок слева направо
        for (int i = 0; i < 5; i++) {
            int x = centerX + i * (buttonSize + spacing);
            int y = centerY;
            this.addDrawableChild(new SpruceButtonWidget(Position.of(x, y), buttonSize, buttonSize, Text.literal(""), btn -> {}));
        }

        // Добавляем текстовое поле под кнопками
        int textFieldWidth = buttonSize * 5 + spacing * 4;
        int textFieldHeight = 20;
        int textFieldX = width / 2 - textFieldWidth / 2;
        int textFieldY = centerY + buttonSize + 10; // Позиция под кнопками, расстояние уменьшено до 10 пикселей

        textField = new SpruceTextFieldWidget(Position.of(textFieldX, textFieldY), textFieldWidth, textFieldHeight, Text.literal(""));
        textField.setChangedListener(this::updatePlayerList);
        this.addSelectableChild(textField);

        // Инициализация списка всех игроков
        initializePlayerList();

        // Добавляем кнопки прокрутки
        int listWidgetX = width / 2 - textFieldWidth / 2;
        int listWidgetY = textFieldY + textFieldHeight + 10;
        int listWidgetHeight = maxVisibleItems * itemHeight;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("^"), btn -> scrollUp())
                .dimensions(listWidgetX + textFieldWidth - 20, listWidgetY, 20, 20)
                .build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("v"), btn -> scrollDown())
                .dimensions(listWidgetX + textFieldWidth - 20, listWidgetY + listWidgetHeight - 20, 20, 20)
                .build());
    }

    private void initializePlayerList() {
        // Добавляем 20 фейковых никнеймов для тестирования
        allPlayers = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            allPlayers.add("Player" + i);
        }
        filteredPlayers = allPlayers;
    }

    private void updatePlayerList(String text) {
        filteredPlayers = allPlayers.stream()
                .filter(player -> player.toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
        scrollOffset = 0; // Сбрасываем прокрутку при обновлении списка
    }

    private void scrollUp() {
        if (scrollOffset > 0) {
            scrollOffset--;
        }
    }

    private void scrollDown() {
        if ((scrollOffset + maxVisibleItems) < filteredPlayers.size()) {
            scrollOffset++;
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.client.world == null) {
            this.renderPanoramaBackground(context, delta);
        }

        this.applyBlur(delta);
        this.renderDarkening(context);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        textField.render(context, mouseX, mouseY, delta);

        // Отрисовываем задний фон для контейнера с никнеймами
        int listX = textField.getX();
        int listY = textField.getY() + textField.getHeight() + 10;
        int listWidth = textField.getWidth();
        int listHeight = maxVisibleItems * itemHeight;

        int backgroundColor = ColorHelper.Argb.getArgb(255, 0, 0, 0); // Черный фон
        int borderColor = ColorHelper.Argb.getArgb(255, 255, 255, 255); // Белая граница

        // Отрисовываем фон
        context.fill(listX, listY, listX + listWidth, listY + listHeight, backgroundColor);
        // Отрисовываем границу
        drawBorder(context, listX, listY, listX + listWidth, listY + listHeight, borderColor);

        // Отображаем отфильтрованный список игроков
        int y = listY + 6;
        for (int i = scrollOffset; i < scrollOffset + maxVisibleItems && i < filteredPlayers.size(); i++) {
            String player = filteredPlayers.get(i);
            context.drawText(client.textRenderer, player, listX + 6, y, 0xFFFFFF, false);
            y += itemHeight;
        }
    }

    private void drawBorder(DrawContext context, int left, int top, int right, int bottom, int color) {
        context.fill(left, top, right, top + 1, color); // Верхняя граница
        context.fill(left, bottom - 1, right, bottom, color); // Нижняя граница
        context.fill(left, top, left + 1, bottom, color); // Левая граница
        context.fill(right - 1, top, right, bottom, color); // Правая граница
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return textField.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (super.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        return textField.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        }
        return textField.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (verticalAmount > 0) {
            scrollUp();
        } else if (verticalAmount < 0) {
            scrollDown();
        }
        return true; // Возвращаем true, чтобы указать, что событие обработано
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return textField.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (super.keyReleased(keyCode, scanCode, modifiers)) {
            return true;
        }
        return textField.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (super.charTyped(chr, modifiers)) {
            return true;
        }
        return textField.charTyped(chr, modifiers);
    }
}
