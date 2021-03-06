package net.minecraftforge.gradle;

import argo.jdom.JdomParser;

import com.google.common.base.Joiner;

import org.gradle.api.Project;

import groovy.lang.Closure;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class Constants
{
    // OS
    public static enum OperatingSystem
    {
        WINDOWS, OSX, LINUX;
        
        public String toString()
        {
            return name().toLowerCase();
        }
    }

    public static final OperatingSystem OPERATING_SYSTEM = getOs();

    // extension nam
    public static final String EXT_NAME_MC      = "minecraft";
    public static final String EXT_NAME_JENKINS = "jenkins";

    // json parser
    public static final JdomParser PARSER = new JdomParser();
    @SuppressWarnings("serial")
    public static final Closure<Boolean> CALL_FALSE = new Closure<Boolean>(null){ public Boolean call(Object o){ return false; }};

    // urls
    public static final String MC_VERSION       = "{MC_VERSION}";
    public static final String MC_JAR_URL       = "http://s3.amazonaws.com/Minecraft.Download/versions/{MC_VERSION}/{MC_VERSION}.jar";
    public static final String MC_SERVER_URL    = "http://s3.amazonaws.com/Minecraft.Download/versions/{MC_VERSION}/minecraft_server.{MC_VERSION}.jar";
    public static final String MCP_URL          = "http://mcp.ocean-labs.de/files/archive/mcp804.zip";
    public static final String INSTALLER_URL    = "http://files.minecraftforge.net/installer/forge-installer-{INSTALLER_VERSION}-shrunk.jar";

    // things in the cache dir.
    public static final String CACHE_DIR        = "{CACHE_DIR}/minecraft";
    public static final String JAR_CLIENT_FRESH = "{CACHE_DIR}/minecraft/net/minecraft/minecraft/{MC_VERSION}/minecraft-{MC_VERSION}.jar";
    public static final String JAR_SERVER_FRESH = "{CACHE_DIR}/minecraft/net/minecraft/minecraft_server/{MC_VERSION}/minecraft_server-{MC_VERSION}.jar";
    public static final String JAR_MERGED       = "{CACHE_DIR}/minecraft/net/minecraft/minecraft_merged/{MC_VERSION}/minecraft_merged-{MC_VERSION}.jar";
    public static final String JAR_SRG          = "{CACHE_DIR}/minecraft/net/minecraft/minecraft_srg/{MC_VERSION}/minecraft_srg-{MC_VERSION}.jar";
    public static final String ZIP_DECOMP       = "{CACHE_DIR}/minecraft/net/minecraft/minecraft_decomp/{MC_VERSION}/minecraft_decomp-{MC_VERSION}.zip";
    public static final String ZIP_FML          = "{CACHE_DIR}/minecraft/net/minecraft/minecraft_decomp/{MC_VERSION}/minecraft_fml-{MC_VERSION}.zip";
    public static final String PACKAGED_SRG     = "{CACHE_DIR}/minecraft/net/minecraft/minecraft_srg/{MC_VERSION}/packaged-{MC_VERSION}.srg";
    public static final String PACKAGED_EXC     = "{CACHE_DIR}/minecraft/net/minecraft/minecraft_srg/{MC_VERSION}/packaged-{MC_VERSIOn}.exc";
    public static final String DEOBF_DATA       = "{CACHE_DIR}/minecraft/net/minecraft/minecraft_srg/{MC_VERSION}/deobfuscation_data-{MC_VERSION}.lzma";
    public static final String FERNFLOWER       = "{CACHE_DIR}/minecraft/fernflower.jar";
    public static final String EXCEPTOR         = "{CACHE_DIR}/minecraft/exceptor.jar";
    public static final String INSTALLER_BASE   = "{BUILD_DIR}/tmp/installer_base.{INSTALLER_VERSION}.jar";
    public static final String INSTALL_PROFILE  = "{BUILD_DIR}/tmp/install_profile.json";
    public static final String REOBF_TMP        = "{BUILD_DIR}/tmp/recomp_obfed.jar";
    public static final String BINPATCH_TMP     = "{BUILD_DIR}/tmp/bin_patches.jar";

    // eclipse folders
    public static final String WORKSPACE = "eclipse";
    public static final String ECLIPSE_CLEAN = WORKSPACE + "/Clean";
    public static final String ECLIPSE_FML = WORKSPACE + "/FML";
    public static final String ECLIPSE_RUN = WORKSPACE + "/run";
    public static final String ECLIPSE_NATIVES = ECLIPSE_RUN + "/bin/natives";

    // src dirs
    public static final String BUKKIT_SRC = "bukkit";
    public static final String PATCH_DIR = "patches/minecraft";

    // util
    public static final String NEWLINE = System.getProperty("line.separator");
    private static final OutputStream NULL_OUT = new OutputStream()
    {
        public void write(int b) throws IOException{}
    };

    // helper methods
    public static File cacheFile(Project project, String... otherFiles)
    {
        return Constants.file(project.getGradle().getGradleUserHomeDir(), otherFiles);
    }

    public static File file(File file, String... otherFiles)
    {
        String othersJoined = Joiner.on('/').join(otherFiles);
        return new File(file, othersJoined);
    }

    public static File file(String... otherFiles)
    {
        String othersJoined = Joiner.on('/').join(otherFiles);
        return new File(othersJoined);
    }

    public static List<String> getClassPath()
    {
        URL[] urls = ((URLClassLoader) ExtensionObject.class.getClassLoader()).getURLs();

        ArrayList<String> list = new ArrayList<String>();
        for (URL url : urls)
        {
            list.add(url.getPath());
        }
        //System.out.println(Joiner.on(';').join(((URLClassLoader) ExtensionObject.class.getClassLoader()).getURLs()));
        return list;
    }

    private static OperatingSystem getOs()
    {
        String name = System.getProperty("os.name").toString().toLowerCase();
        if (name.contains("windows"))
        {
            return OperatingSystem.WINDOWS;
        }
        else if (name.contains("mac"))
        {
            return OperatingSystem.OSX;
        }
        else if (name.contains("linux"))
        {
            return OperatingSystem.LINUX;
        }
        else
        {
            return null;
        }
    }

    public static String hash(File file)
    {
        try
        {

            InputStream fis = new FileInputStream(file);

            byte[] buffer = new byte[1024];
            MessageDigest complete = MessageDigest.getInstance("MD5");
            int numRead;

            do
            {
                numRead = fis.read(buffer);
                if (numRead > 0)
                {
                    complete.update(buffer, 0, numRead);
                }
            } while (numRead != -1);

            fis.close();
            byte[] hash = complete.digest();

            String result = "";

            for (int i = 0; i < hash.length; i++)
            {
                result += Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1);
            }
            return result;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static String hash(String str)
    {
        try
        {
            MessageDigest complete = MessageDigest.getInstance("MD5");
            byte[] hash = complete.digest(str.getBytes());

            String result = "";

            for (int i = 0; i < hash.length; i++)
            {
                result += Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1);
            }
            return result;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * DON'T FORGET TO CLOSE
     */
    public static OutputStream getNullStream()
    {
        return NULL_OUT;
    }
}
