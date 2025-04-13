//? if <1.20.1 || forge {
/*package xyz.pupbrained.drop_confirm.config.screens

import xyz.pupbrained.drop_confirm.config.widgets.DropConfirmButtonControl as ButtonControl
import com.gitlab.cdagaming.unilib.utils.ItemUtils
import com.gitlab.cdagaming.unilib.utils.gui.RenderUtils
import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedTextControl
import com.gitlab.cdagaming.unilib.utils.gui.controls.ScrollableListControl
import com.gitlab.cdagaming.unilib.utils.gui.integrations.ExtendedScreen
//? if >=1.16.5
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.core./^? if <=1.18.2 {^//^Registry as BuiltInRegistries^//^?} else {^/registries.BuiltInRegistries/^?}^/
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER
import org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ENTER
import xyz.pupbrained.drop_confirm.config.DropConfirmConfig
import xyz.pupbrained.drop_confirm.config.widgets.DropConfirmButtonControl
import xyz.pupbrained.drop_confirm.util.ComponentUtils
import java.util.*

class DropConfirmListEditorScreen(private val parentScreen: Screen) :
  ExtendedScreen(ComponentUtils.translatable("option.drop_confirm.${if (DropConfirmConfig.treatAsWhitelist) "white" else "black"}listed_items").string) {
  private val itemsList: MutableList<Item> = DropConfirmConfig.blacklistedItems.toMutableList()

  private lateinit var itemDisplayList: ItemList
  private lateinit var backButton: ButtonControl
  private lateinit var newItemTextField: ExtendedTextControl
  private lateinit var addButton: ButtonControl
  private lateinit var originalAddButtonText: String

  private val listBottom: Int
    get() = this.height - LIST_BOTTOM_MARGIN

  private val listHeight: Int
    get() = listBottom - LIST_TOP_MARGIN

  private val bottomControlY: Int
    get() = listBottom + (LIST_BOTTOM_MARGIN - CONTROL_HEIGHT) / 2

  private val backButtonX: Int
    get() = SIDE_MARGIN

  private val addButtonX: Int
    get() = this.width - ADD_BUTTON_WIDTH - SIDE_MARGIN

  private val textFieldX: Int
    get() = backButtonX + BACK_BUTTON_WIDTH + BUTTON_SPACING

  private val textFieldWidth: Int
    get() = addButtonX - BUTTON_SPACING - textFieldX

  private val textFieldY: Int
    get() = bottomControlY + 1

  companion object {
    private const val CONTROL_HEIGHT = 20
    private const val BUTTON_SPACING = 8
    private const val SIDE_MARGIN = 6
    private const val BACK_BUTTON_WIDTH = 80
    private const val ADD_BUTTON_WIDTH = 120

    private const val LIST_TOP_MARGIN = 32
    private const val LIST_BOTTOM_MARGIN = 38

    private const val TEXT_FIELD_VERTICAL_ADJUST = 2
    private const val TEXT_FIELD_HEIGHT = CONTROL_HEIGHT - TEXT_FIELD_VERTICAL_ADJUST
    private const val TEXT_FIELD_DEFAULT_COLOR: Int = 0xE0E0E0 // Standard text color
    private const val TEXT_FIELD_VALID_COLOR: Int = 0x55FF55 // Bright Green
    private const val TEXT_FIELD_INVALID_COLOR: Int = 0xFF5555 // Bright Red

    // Constants for inner class ItemList
    const val ENTRY_HEIGHT = 24
    const val ICON_SIZE = 16
    const val ICON_TEXT_PADDING = 4
    const val REMOVE_BUTTON_SIZE = 20
    const val HORIZONTAL_PADDING = 4
    const val BUTTON_RIGHT_MARGIN = 4
  }

  override fun initializeUi() {
    itemDisplayList = ItemList(
      gameInstance,
      this,
      screenWidth,
      listHeight,
      LIST_TOP_MARGIN,
      listBottom,
      itemsList
    )

    backButton = ButtonControl(
      backButtonX,
      bottomControlY,
      BACK_BUTTON_WIDTH,
      CONTROL_HEIGHT,
      ComponentUtils.translatable("gui.back").string
    ) { saveAndReturn() }

    newItemTextField = object : ExtendedTextControl(
      fontRenderer,
      textFieldX,
      textFieldY,
      textFieldWidth,
      TEXT_FIELD_HEIGHT
    ) {
      private val placeholderText = ComponentUtils.translatable("option.drop_confirm.enter_item_id").string

      override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (this.isFocused && (keyCode == GLFW_KEY_ENTER || keyCode == GLFW_KEY_KP_ENTER)) {
          this@DropConfirmListEditorScreen.addItemFromTextField()
          return true
        }

        return super.keyPressed(keyCode, scanCode, modifiers)
      }

      override fun render(
        /^? if >=1.16.5 {^/poseStack: PoseStack,/^?}^/
        mouseX: Int,
        mouseY: Int,
        partialTicks: Float
      ) {
        super.render(
          /^? if >=1.16.5 {^/poseStack,/^?}^/
          mouseX,
          mouseY,
          partialTicks
        )

        if (value.isEmpty())
          fontRenderer.draw(
            /^? >=1.16.5 {^/poseStack,/^?}^/
            placeholderText,
            (x + 4).toFloat(),
            (y + (height - 8) / 2).toFloat(),
            0x808080
          )
      }
    }

    originalAddButtonText = ComponentUtils.translatable("option.drop_confirm.add").string
    addButton = ButtonControl(
      addButtonX,
      bottomControlY,
      ADD_BUTTON_WIDTH,
      CONTROL_HEIGHT,
      originalAddButtonText
    ) { addItemFromTextField() }

    newItemTextField.setResponder { updateTextFieldAndButtonState(it) }

    addControl(itemDisplayList)
    addControl(backButton)
    addControl(newItemTextField)
    addControl(addButton)

    super.initializeUi()
  }

  private fun saveAndReturn() {
    DropConfirmConfig.blacklistedItems = itemsList
    DropConfirmConfig.save()
    minecraft?.setScreen(parentScreen)
  }

  private fun addItemFromTextField() {
    val itemIdText = newItemTextField.controlMessage.trim()

    if (itemIdText.isEmpty()) return

    val fullResourceName = itemIdText.takeIf { it.contains(':') } ?: "minecraft:$itemIdText"

    val resourceLocation = ResourceLocation.tryParse(fullResourceName)
    if (resourceLocation == null) {
      addButton.controlMessage = "Invalid ID format!"
      addButton.textColor = DropConfirmButtonControl.ERROR_TEXT_COLOR
      return
    }

    val optionalItem: Optional<Item> = BuiltInRegistries.ITEM.getOptional(resourceLocation)
    if (!optionalItem.isPresent) {
      addButton.controlMessage = "Item not found!"
      addButton.textColor = DropConfirmButtonControl.ERROR_TEXT_COLOR
      return
    }

    val itemToAdd: Item = optionalItem.get()
    if (itemsList.contains(itemToAdd)) {
      addButton.controlMessage = "Already in list!"
      addButton.textColor = DropConfirmButtonControl.ERROR_TEXT_COLOR
      return
    }

    itemsList.add(itemToAdd)
    newItemTextField.controlMessage = ""

    updateVisualList()
    updateTextFieldAndButtonState("")
  }

  private fun removeItemAt(index: Int) {
    if (index in itemsList.indices) {
      itemsList.removeAt(index)
      updateVisualList()
    }
  }

  private fun updateVisualList() {
    val displayNames = itemsList.map { getItemDisplayName(it) }
    itemDisplayList.setListAndUpdate(displayNames, itemsList)
  }

  private fun getItemDisplayName(item: Item) = item.getName(ItemStack(item)).string

  private fun isInputValidAndAddable(text: String): Boolean {
    val trimmedText = text.trim()

    if (trimmedText.isEmpty()) return false

    val fullResourceName = trimmedText.takeIf { it.contains(':') } ?: "minecraft:$trimmedText"

    val resourceLocation = ResourceLocation.tryParse(fullResourceName) ?: return false

    val optionalItem: Optional<Item> = BuiltInRegistries.ITEM.getOptional(resourceLocation)

    if (!optionalItem.isPresent) return false

    if (itemsList.contains(optionalItem.get())) return false

    return true
  }

  private fun updateTextFieldAndButtonState(currentText: String) {
    if (addButton.controlMessage != originalAddButtonText) {
      addButton.controlMessage = originalAddButtonText
      addButton.textColor = DropConfirmButtonControl.DEFAULT_TEXT_COLOR
    }

    val isValid = isInputValidAndAddable(currentText)
    val color = when {
      currentText.trim().isEmpty() -> TEXT_FIELD_DEFAULT_COLOR
      isValid -> TEXT_FIELD_VALID_COLOR
      else -> TEXT_FIELD_INVALID_COLOR
    }
    newItemTextField.setTextColor(color)
  }

  inner class ItemList(
    mc: Minecraft,
    owningScreen: ExtendedScreen,
    width: Int,
    height: Int,
    top: Int,
    bottom: Int,
    initialItems: List<Item>
  ) : ScrollableListControl(
    mc, owningScreen, width, height, top, bottom,
    ENTRY_HEIGHT,
    initialItems.map { getItemDisplayName(it) },
    null
  ) {
    private var currentItems: List<Item> = initialItems.toList()
    private val removeButtons = mutableMapOf<Int, ButtonControl>()

    fun setListAndUpdate(displayNames: List<String>, actualItems: List<Item>) {
      this.setList(displayNames)
      this.currentItems = actualItems.toList()
      this.removeButtons.keys.retainAll { it < actualItems.size }
    }

    override fun renderSlotItem(
      /^? if >=1.16.5 {^/matrices: PoseStack,/^?}^/
      originalName: String?,
      x: Int,
      y: Int,
      slotWidth: Int,
      slotHeight: Int,
      mouseX: Int,
      mouseY: Int,
      isHovering: Boolean,
      partialTicks: Float
    ) {
      val index = itemList.indexOf(originalName)
      if (index == -1 || index >= currentItems.size) {
        super.renderSlotItem(
          /^? if >=1.16.5 {^/matrices,/^?}^/
          "Error: Item not found",
          x,
          y,
          slotWidth,
          slotHeight,
          mouseX,
          mouseY,
          isHovering,
          partialTicks
        )
        return
      }

      val item = currentItems[index]
      val stack = ItemStack(item)

      var currentX = x + HORIZONTAL_PADDING

      if (!ItemUtils.isItemEmpty(stack)) {
        val iconY = y + (slotHeight - ICON_SIZE) / 2
        RenderUtils.drawItemStack(
          gameInstance,
          /^? if >=1.19.4 {^/matrices,/^?}^/
          fontRenderer,
          currentX,
          iconY,
          stack,
          1.0f
        )
        currentX += ICON_SIZE + ICON_TEXT_PADDING
      }

      val buttonX = x + slotWidth - REMOVE_BUTTON_SIZE - BUTTON_RIGHT_MARGIN
      val buttonY = y + (slotHeight - REMOVE_BUTTON_SIZE) / 2

      val removeButton = removeButtons.getOrPut(index) {
        ButtonControl(buttonX, buttonY, REMOVE_BUTTON_SIZE, REMOVE_BUTTON_SIZE, "-") {
          this@DropConfirmListEditorScreen.removeItemAt(index)
        }
      }

      removeButton.controlPosX = buttonX
      removeButton.controlPosY = buttonY

      removeButton.isFocusedOver = removeButton.isControlVisible && removeButton.isControlEnabled &&
        RenderUtils.isMouseOver(mouseX.toDouble(), mouseY.toDouble(), removeButton)

      removeButton.render(
        /^? if >=1.16.5 {^/matrices,/^?}^/
        mouseX,
        mouseY,
        partialTicks
      )

      val textWidth = buttonX - currentX - HORIZONTAL_PADDING
      super.renderSlotItem(
        /^? if >=1.16.5 {^/matrices,/^?}^/
        originalName,
        currentX,
        y,
        textWidth,
        slotHeight,
        mouseX,
        mouseY,
        isHovering,
        partialTicks
      )
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
      for ((_, removeBtn) in removeButtons)
        if (removeBtn.isControlVisible && removeBtn.isMouseOver(mouseX, mouseY)) {
          removeBtn.onPress()
          return true
        }
      return super.mouseClicked(mouseX, mouseY, button)
    }
  }
}
*///?}
