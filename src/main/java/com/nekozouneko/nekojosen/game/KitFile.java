package com.nekozouneko.nekojosen.game;

import com.google.gson.Gson;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.*;
import java.nio.charset.StandardCharsets;

public final class KitFile {

    public static KitFile load(File path) throws IOException {
        Gson gson = new Gson();

        if (path.exists()) {
            try (
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            new FileInputStream(path), StandardCharsets.UTF_8
                    ))
            ) {
                return gson.fromJson(reader, KitFile.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new KitFile(new ItemStack[41]);
    }

    public static void save(KitFile data, File path) throws IOException {
        Gson gson = new Gson();

        try (
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(path), StandardCharsets.UTF_8
                ))
        ) {
            gson.toJson(data, KitFile.class, writer);

            writer.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String content;

    public KitFile(ItemStack[] array) {
        setContent(array);
    }

    public void setContent(ItemStack[] array) {
        try (
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            BukkitObjectOutputStream out = new BukkitObjectOutputStream(bytes)
        ) {
            out.writeObject(array);
            content = Base64Coder.encodeLines(bytes.toByteArray());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ItemStack[] getContent() {
        try (
            ByteArrayInputStream bytes = new ByteArrayInputStream(Base64Coder.decodeLines(content));
            BukkitObjectInputStream in = new BukkitObjectInputStream(bytes)
        ) {
            Object obj = in.readObject();

            if (obj instanceof ItemStack[]) return ((ItemStack[]) obj);
            else return null;
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }


}
