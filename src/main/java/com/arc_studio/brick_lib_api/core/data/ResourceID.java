package com.arc_studio.brick_lib_api.core.data;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

//? if > 1.18.2 {
import net.minecraft.IdentifierException;
import net.minecraft.network.chat.Component;
//?} else {
/*import net.minecraft.network.chat.TranslatableComponent;
*///?}

//? if > 1.12.5 {
import net.minecraft.resources.Identifier;
//? } else {
/*import net.minecraft.resources.ResourceLocation;
import net.minecraft.ResourceLocationException;*/
//? }


import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

public class ResourceID extends
    //~ if > 1.21.5 'ResourceLocation' -> 'Identifier'
    Identifier
{
    protected static final SimpleCommandExceptionType ERROR_INVALID =
        new SimpleCommandExceptionType(
            //? if <= 1.18.2 {
            /*new TranslatableComponent("argument.id.invalid")
            *///?} else {
            Component.translatable("argument.id.invalid")
            //?}
        );
    public ResourceID(String location) {
        //? if > 1.20.6 {
        this(decompose(location, ':'));
        //?} else {
        /*super(location);
        *///?}
    }

    public ResourceID(String namespace, String path) {
        super(namespace, path);
    }

    public ResourceID(ResourceID rl) {
        super(rl.getNamespace(),rl.getPath());
    }

    public ResourceID(String[] strings) {
        super(strings[0],strings[1]);
    }

    public static ResourceID of(String location, char separator) {
        return new ResourceID(decompose(location, separator));
    }

    public static ResourceID of(String namespace, String path) {
        return new ResourceID(namespace, path);
    }

    @Nullable
    public static ResourceID tryParse(String location) {
        try {
            return new ResourceID(location);
        } catch (/*? if < 1.21.5 {*/ /*ResourceLocationException*/ /*?} else {*/ IdentifierException /*?}*/ var2) {
            return null;
        }
    }

    @Nullable
    public static ResourceID tryBuild(String namespace, String path) {
        try {
            return new ResourceID(namespace, path);
        } catch (/*? if < 1.21.5 {*/ /*ResourceLocationException*/ /*?} else {*/ IdentifierException /*?}*/ var3) {
            return null;
        }
    }

    protected static String[] decompose(String location, char separator) {
        String[] astring = new String[]{"minecraft", location};
        int i = location.indexOf(separator);
        if (i >= 0) {
            astring[1] = location.substring(i + 1);
            if (i >= 1) {
                astring[0] = location.substring(0, i);
            }
        }

        return astring;
    }

    public static ResourceID read(StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();

        while(reader.canRead() && isAllowedInResourceLocation(reader.peek())) {
            reader.skip();
        }

        String s = reader.getString().substring(i, reader.getCursor());

        try {
            return new ResourceID(s);
        } catch (/*? if < 1.21.5 {*/ /*ResourceLocationException*/ /*?} else {*/ IdentifierException /*?}*/ var4) {
            reader.setCursor(i);
            throw ERROR_INVALID.createWithContext(reader);
        }
    }

    public static boolean isAllowedInResourceLocation(char character) {
        return character >= '0' && character <= '9' || character >= 'a' && character <= 'z' || character == '_' || character == ':' || character == '/' || character == '.' || character == '-';
    }

    public static boolean isValidPath(String path) {
        for(int i = 0; i < path.length(); ++i) {
            if (!validPathChar(path.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public static boolean isValidNamespace(String namespace) {
        for(int i = 0; i < namespace.length(); ++i) {
            if (!validNamespaceChar(namespace.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    private static String assertValidNamespace(String namespace, String path) {
        if (!isValidNamespace(namespace)) {
            throw new /*? if < 1.21.5 {*/ /*ResourceLocationException*/ /*?} else {*/ IdentifierException /*?}*/("Non [a-z0-9_.-] character in namespace of ResourceID: " + namespace + ":" + path);
        } else {
            return namespace;
        }
    }

    public static boolean validPathChar(char pathChar) {
        return pathChar == '_' || pathChar == '-' || pathChar >= 'a' && pathChar <= 'z' || pathChar >= '0' && pathChar <= '9' || pathChar == '/' || pathChar == '.';
    }

    public static boolean validNamespaceChar(char namespaceChar) {
        return namespaceChar == '_' || namespaceChar == '-' || namespaceChar >= 'a' && namespaceChar <= 'z' || namespaceChar >= '0' && namespaceChar <= '9' || namespaceChar == '.';
    }

    public static boolean isValidResourceLocation(String location) {
        String[] astring = decompose(location, ':');
        return isValidNamespace(StringUtils.isEmpty(astring[0]) ? "minecraft" : astring[0]) && isValidPath(astring[1]);
    }

    private static String assertValidPath(String namespace, String path) {
        if (!isValidPath(path)) {
            throw new /*? if < 1.21.5 {*/ /*ResourceLocationException*/ /*?} else {*/ IdentifierException /*?}*/("Non [a-z0-9/._-] character in path of ResourceID: " + namespace + ":" + path);
        } else {
            return path;
        }
    }
}
