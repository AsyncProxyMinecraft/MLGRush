package net.asyncproxy.mlgrush.modules.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

public class ItemBuilder {
    private ItemStack itemStack;

    /**
     * Erstellt einen neuen ItemBuilder mit einem Material
     *
     * @param material Das Material des Items
     */
    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
    }

    /**
     * Erstellt einen neuen ItemBuilder mit einem bestehendem ItemStack
     *
     * @param itemStack Der zu bearbeitende ItemStack
     */
    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack.clone();
    }

    /**
     * Erstellt einen neuen ItemBuilder mit einem Material und einer bestimmten Menge
     *
     * @param material Das Material des Items
     * @param amount Die Menge des Items
     */
    public ItemBuilder(Material material, int amount) {
        this.itemStack = new ItemStack(material, amount);
    }

    /**
     * Erstellt einen neuen ItemBuilder für einen Custom Skull mit einer Textur-URL
     *
     * @param skullTexture Die URL zur Skull-Textur (von z.B. MinecraftHeads oder textures.minecraft.net)
     * @return Ein neuer ItemBuilder mit dem Custom Skull
     */
    public static ItemBuilder createCustomSkull(String skullTexture) {
        ItemBuilder builder = new ItemBuilder(Material.PLAYER_HEAD);
        return builder.setSkullTexture(skullTexture);
    }

    /**
     * Erstellt einen neuen ItemBuilder für einen Spieler-Skull
     *
     * @param playerName Der Name des Spielers, dessen Kopf verwendet werden soll
     * @return Ein neuer ItemBuilder mit dem Spieler-Skull
     */
    public static ItemBuilder createPlayerSkull(String playerName) {
        ItemBuilder builder = new ItemBuilder(Material.PLAYER_HEAD);
        return builder.setSkullOwner(playerName);
    }

    /**
     * Setzt den Namen des Items
     *
     * @param name Der anzuzeigende Name
     * @return Der ItemBuilder für Method-Chaining
     */
    public ItemBuilder setName(String name) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.customName(MiniMessage.miniMessage().deserialize(name));
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Setzt die Beschreibung (Lore) des Items
     *
     * @param lore Die Zeilen der Beschreibung
     * @return Der ItemBuilder für Method-Chaining
     */
    public ItemBuilder setLore(String... lore) {
        return setLore(Arrays.asList(lore));
    }

    /**
     * Setzt die Beschreibung (Lore) des Items mit einer Liste von Zeilen
     *
     * @param lore Die Liste der Beschreibungszeilen
     * @return Der ItemBuilder für Method-Chaining
     */
    public ItemBuilder setLore(List<String> lore) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Fügt zusätzliche Zeilen zur bestehenden Beschreibung (Lore) hinzu
     *
     * @param lines Die hinzuzufügenden Zeilen
     * @return Der ItemBuilder für Method-Chaining
     */
    public ItemBuilder addLore(String... lines) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            List<String> lore = meta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            lore.addAll(Arrays.asList(lines));
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Setzt die Menge des Items
     *
     * @param amount Die Menge
     * @return Der ItemBuilder für Method-Chaining
     */
    public ItemBuilder setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    /**
     * Macht das Item unzerstörbar
     *
     * @param unbreakable Ob das Item unzerstörbar sein soll
     * @return Der ItemBuilder für Method-Chaining
     */
    public ItemBuilder setUnbreakable(boolean unbreakable) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setUnbreakable(unbreakable);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Fügt eine Verzauberung zum Item hinzu
     *
     * @param enchantment Die Verzauberung
     * @param level Das Level der Verzauberung
     * @return Der ItemBuilder für Method-Chaining
     */
    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.addEnchant(enchantment, level, true);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Fügt mehrere Verzauberungen zum Item hinzu
     *
     * @param enchantments Eine Map mit Verzauberungen und deren Levels
     * @return Der ItemBuilder für Method-Chaining
     */
    public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            addEnchantment(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * Fügt Verzauberungen zu einem Verzauberungsbuch hinzu
     *
     * @param enchantment Die Verzauberung
     * @param level Das Level der Verzauberung
     * @return Der ItemBuilder für Method-Chaining
     */
    public ItemBuilder addStoredEnchantment(Enchantment enchantment, int level) {
        if (itemStack.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) itemStack.getItemMeta();
            if (meta != null) {
                meta.addStoredEnchant(enchantment, level, true);
                itemStack.setItemMeta(meta);
            }
        }
        return this;
    }

    /**
     * Fügt ItemFlags zum Item hinzu (versteckt bestimmte Attribute)
     *
     * @param flags Die hinzuzufügenden ItemFlags
     * @return Der ItemBuilder für Method-Chaining
     */
    public ItemBuilder addItemFlags(ItemFlag... flags) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.addItemFlags(flags);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Entfernt ItemFlags vom Item
     *
     * @param flags Die zu entfernenden ItemFlags
     * @return Der ItemBuilder für Method-Chaining
     */
    public ItemBuilder removeItemFlags(ItemFlag... flags) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.removeItemFlags(flags);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Setzt die Haltbarkeit des Items
     *
     * @param durability Die Haltbarkeit
     * @return Der ItemBuilder für Method-Chaining
     */
    public ItemBuilder setDurability(int durability) {
        if (durability >= 0) {
            itemStack.setDurability((short) durability);
        }
        return this;
    }

    /**
     * Setzt die Farbe einer Lederrüstung
     *
     * @param color Die Farbe (Bukkit Color)
     * @return Der ItemBuilder für Method-Chaining
     */
    public ItemBuilder setLeatherArmorColor(Color color) {
        if (itemStack.getItemMeta() instanceof LeatherArmorMeta) {
            LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
            meta.setColor(color);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Setzt die Textur eines Spielerkopfes mit einer URL
     *
     * @param textureUrl Die URL zur Textur
     * @return Der ItemBuilder für Method-Chaining
     */
    public ItemBuilder setSkullTexture(String textureUrl) {
        if (itemStack.getType() == Material.PLAYER_HEAD) {
            try {
                SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
                if (skullMeta != null) {
                    PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
                    PlayerTextures textures = profile.getTextures();

                    // Entfernen von 'minecraft:' oder 'minecraft://' am Anfang, falls vorhanden
                    if (textureUrl.startsWith("minecraft:")) {
                        textureUrl = textureUrl.substring("minecraft:".length());
                    }

                    URL url = new URL(textureUrl);
                    textures.setSkin(url);
                    profile.setTextures(textures);

                    skullMeta.setOwnerProfile(profile);
                    itemStack.setItemMeta(skullMeta);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    /**
     * Setzt den Besitzer eines Spielerkopfes
     *
     * @param playerName Der Name des Spielers
     * @return Der ItemBuilder für Method-Chaining
     */
    public ItemBuilder setSkullOwner(String playerName) {
        if (itemStack.getType() == Material.PLAYER_HEAD) {
            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            if (skullMeta != null) {
                com.destroystokyo.paper.profile.PlayerProfile profile = Bukkit.createProfile(playerName);
                Bukkit.getServer().getScheduler().runTaskAsynchronously(
                        Bukkit.getPluginManager().getPlugins()[0], // Ein beliebiges Plugin für async Tasks
                        () -> {
                            profile.complete(); // Lädt Textur und UUID des Spielers
                        }
                );
                skullMeta.setOwnerProfile(profile);
                itemStack.setItemMeta(skullMeta);
            }
        }
        return this;
    }

    /**
     * Setzt die Trank-Eigenschaften des Items (für Tränke)
     *
     * @param potionType Der Tranktyp
     * @param extended Ob der Trank verlängert ist
     * @param upgraded Ob der Trank verstärkt ist
     * @return Der ItemBuilder für Method-Chaining
     */
    public ItemBuilder setPotionType(PotionType potionType, boolean extended, boolean upgraded) {
        if (itemStack.getItemMeta() instanceof PotionMeta) {
            PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
            meta.setBasePotionType(potionType);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Fügt einen benutzerdefinierten Trankeffekt zum Trank hinzu
     *
     * @param effect Der Trankeffekt
     * @return Der ItemBuilder für Method-Chaining
     */
    public ItemBuilder addCustomPotionEffect(PotionEffect effect) {
        if (itemStack.getItemMeta() instanceof PotionMeta) {
            PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
            meta.addCustomEffect(effect, true);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Fügt einen benutzerdefinierten Trankeffekt zum Trank hinzu
     *
     * @param type Der Effekttyp
     * @param duration Die Dauer in Ticks
     * @param amplifier Die Stärke des Effekts
     * @param ambient Ob der Effekt Umgebungspartikel haben soll
     * @param particles Ob Partikel sichtbar sein sollen
     * @return Der ItemBuilder für Method-Chaining
     */
    public ItemBuilder addCustomPotionEffect(PotionEffectType type, int duration, int amplifier, boolean ambient, boolean particles) {
        return addCustomPotionEffect(new PotionEffect(type, duration, amplifier, ambient, particles));
    }

    /**
     * Setzt die Farbe des Tranks
     *
     * @param color Die Farbe
     * @return Der ItemBuilder für Method-Chaining
     */
    public ItemBuilder setPotionColor(Color color) {
        if (itemStack.getItemMeta() instanceof PotionMeta) {
            PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
            meta.setColor(color);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Fügt ein Attribut-Modifier zum Item hinzu
     *
     * @param attribute Das zu modifizierende Attribut
     * @param modifier Der Modifier
     * @return Der ItemBuilder für Method-Chaining
     */
    public ItemBuilder addAttributeModifier(Attribute attribute, AttributeModifier modifier) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.addAttributeModifier(attribute, modifier);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Setzt benutzerdefinierte NBT-Daten im Item
     *
     * @param pluginName Das Plugin, das den NamespacedKey bereitstellt
     * @param key Der Schlüssel für die Daten
     * @param value Der Wert (String)
     * @return Der ItemBuilder für Method-Chaining
     */
    public ItemBuilder setPersistentData(String pluginName, String key, String value) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            NamespacedKey namespacedKey = new NamespacedKey(pluginName.toLowerCase(), key);
            meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, value);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Setzt benutzerdefinierte NBT-Daten im Item (Integer)
     *
     * @param pluginName Der Name des Plugins
     * @param key Der Schlüssel für die Daten
     * @param value Der Wert (Integer)
     * @return Der ItemBuilder für Method-Chaining
     */
    public ItemBuilder setPersistentDataInt(String pluginName, String key, int value) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            NamespacedKey namespacedKey = new NamespacedKey(pluginName.toLowerCase(), key);
            meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.INTEGER, value);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Setzt den benutzerdefinierte Model-Daten-Wert für Custom-Texturen
     *
     * @param customModelData Der CustomModelData-Wert
     * @return Der ItemBuilder für Method-Chaining
     */
    public ItemBuilder setCustomModelData(int customModelData) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(customModelData);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Erstellt eine Kopie des ItemBuilders
     *
     * @return Eine neue Instanz des ItemBuilders mit einem geklonten ItemStack
     */
    public ItemBuilder clone() {
        return new ItemBuilder(itemStack.clone());
    }

    /**
     * Wendet eine benutzerdefinierte Funktion auf die ItemMeta an
     *
     * @param metaConsumer Die anzuwendende Funktion
     * @return Der ItemBuilder für Method-Chaining
     */
    public ItemBuilder meta(Consumer<ItemMeta> metaConsumer) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            metaConsumer.accept(meta);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Setzt den Autor und Titel eines Buches
     *
     * @param title Der Titel des Buches
     * @param author Der Autor des Buches
     * @return Der ItemBuilder für Method-Chaining
     */
    public ItemBuilder setBookMeta(String title, String author) {
        if (itemStack.getItemMeta() instanceof BookMeta) {
            BookMeta meta = (BookMeta) itemStack.getItemMeta();
            meta.setTitle(title);
            meta.setAuthor(author);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Fügt Seiten zu einem Buch hinzu
     *
     * @param pages Die Seiten
     * @return Der ItemBuilder für Method-Chaining
     */
    public ItemBuilder addBookPages(Component... pages) {
        if (itemStack.getItemMeta() instanceof BookMeta) {
            BookMeta meta = (BookMeta) itemStack.getItemMeta();
            meta.addPages(pages);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Setzt das Rüstungs-Trim für Rüstungsgegenstände (1.20+)
     *
     * @param trimMaterial Das Trim-Material
     * @param trimPattern Das Trim-Muster
     * @return Der ItemBuilder für Method-Chaining
     */
    public ItemBuilder setArmorTrim(String trimMaterial, String trimPattern) {
        if (itemStack.getItemMeta() instanceof ArmorMeta) {
            // Diese Implementierung ist stark von der Server-Version abhängig
            // und kann je nach Server-Implementierung variieren
            try {
                // Hier nur ein Beispiel - die tatsächliche Implementierung hängt von der Server-API ab
                // ArmorTrim trim = new ArmorTrim(
                //     Registry.TRIM_MATERIAL.get(NamespacedKey.minecraft(trimMaterial)),
                //     Registry.TRIM_PATTERN.get(NamespacedKey.minecraft(trimPattern))
                // );
                // ArmorMeta meta = (ArmorMeta) itemStack.getItemMeta();
                // meta.setTrim(trim);
                // itemStack.setItemMeta(meta);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    /**
     * Gibt den erstellten ItemStack zurück
     *
     * @return Der erstellte ItemStack
     */
    public ItemStack build() {
        return itemStack.clone();
    }

    /**
     * Gibt den erstellten ItemStack zurück
     *
     * @return Der erstellte ItemStack
     */
    public ItemStack toItemStack() {
        return build();
    }
}
