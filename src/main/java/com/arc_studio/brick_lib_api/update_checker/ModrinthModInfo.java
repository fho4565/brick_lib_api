package com.arc_studio.brick_lib_api.update_checker;

import com.arc_studio.brick_lib_api.core.PlatformInfo;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

/**
 * @author fho4565
 */
public final class ModrinthModInfo {
    @SerializedName("featured")
    private final boolean featured;
    @SerializedName("version_type")
    private final String versionType;
    @SerializedName("changelog")
    private final String changelog;
    @SerializedName("version_number")
    private final String versionNumber;
    @SerializedName("dependencies")
    private final List<String> dependencies;
    @SerializedName("loaders")
    private final List<String> loaders;
    @SerializedName("project_id")
    private final String projectId;
    @SerializedName("date_published")
    private final String datePublished;
    @SerializedName("downloads")
    private final int downloads;
    @SerializedName("name")
    private final String name;
    @SerializedName("files")
    private final List<ModrinthModsItem> files;
    @SerializedName("id")
    private final String id;
    @SerializedName("game_versions")
    private final List<String> gameVersions;
    @SerializedName("author_id")
    private final String authorId;
    @SerializedName("status")
    private final String status;

    private transient PlatformInfo platform;

    public ModrinthModInfo(
            boolean featured,
            String versionType,
            String changelog,
            String versionNumber,
            List<String> dependencies,
            List<String> loaders,
            String projectId,
            String datePublished,
            int downloads,
            String name,
            List<ModrinthModsItem> files,
            String id,
            List<String> gameVersions,
            String authorId,
            String status
    ) {
        this.featured = featured;
        this.versionType = versionType;
        this.changelog = changelog;
        this.versionNumber = versionNumber;
        this.dependencies = dependencies;
        this.loaders = loaders;
        this.projectId = projectId;
        this.datePublished = datePublished;
        this.downloads = downloads;
        this.name = name;
        this.files = files;
        this.id = id;
        this.gameVersions = gameVersions;
        this.authorId = authorId;
        this.status = status;
    }

    @SerializedName("featured")
    public boolean featured() {
        return featured;
    }

    @SerializedName("version_type")
    public String versionType() {
        return versionType;
    }

    @SerializedName("changelog")
    public String changelog() {
        return changelog;
    }

    @SerializedName("version_number")
    public String versionNumber() {
        return versionNumber;
    }

    @SerializedName("dependencies")
    public List<String> dependencies() {
        return dependencies;
    }

    @SerializedName("loaders")
    public List<String> loaders() {
        return loaders;
    }

    @SerializedName("project_id")
    public String projectId() {
        return projectId;
    }

    @SerializedName("date_published")
    public String datePublished() {
        return datePublished;
    }

    @SerializedName("downloads")
    public int downloads() {
        return downloads;
    }

    @SerializedName("name")
    public String name() {
        return name;
    }

    @SerializedName("files")
    public List<ModrinthModsItem> files() {
        return files;
    }

    @SerializedName("id")
    public String id() {
        return id;
    }

    @SerializedName("game_versions")
    public List<String> gameVersions() {
        return gameVersions;
    }

    @SerializedName("author_id")
    public String authorId() {
        return authorId;
    }

    @SerializedName("status")
    public String status() {
        return status;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (ModrinthModInfo) obj;
        return this.featured == that.featured &&
                Objects.equals(this.versionType, that.versionType) &&
                Objects.equals(this.changelog, that.changelog) &&
                Objects.equals(this.versionNumber, that.versionNumber) &&
                Objects.equals(this.dependencies, that.dependencies) &&
                Objects.equals(this.loaders, that.loaders) &&
                Objects.equals(this.projectId, that.projectId) &&
                Objects.equals(this.datePublished, that.datePublished) &&
                this.downloads == that.downloads &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.files, that.files) &&
                Objects.equals(this.id, that.id) &&
                Objects.equals(this.gameVersions, that.gameVersions) &&
                Objects.equals(this.authorId, that.authorId) &&
                Objects.equals(this.status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(featured, versionType, changelog, versionNumber, dependencies, loaders, projectId, datePublished, downloads, name, files, id, gameVersions, authorId, status);
    }

    @Override
    public String toString() {
        return "ModrinthModInfo[" +
                "featured=" + featured + ", " +
                "versionType=" + versionType + ", " +
                "changelog=" + changelog + ", " +
                "versionNumber=" + versionNumber + ", " +
                "dependencies=" + dependencies + ", " +
                "loaders=" + loaders + ", " +
                "projectId=" + projectId + ", " +
                "datePublished=" + datePublished + ", " +
                "downloads=" + downloads + ", " +
                "name=" + name + ", " +
                "files=" + files + ", " +
                "id=" + id + ", " +
                "gameVersions=" + gameVersions + ", " +
                "authorId=" + authorId + ", " +
                "status=" + status + ']';
    }

    public PlatformInfo platform(){
        if(this.platform == null){
            PlatformInfo type = new PlatformInfo();
            type.removeAll();
            for (String s : loaders) {
                if ("forge".equals(s)) {
                    type.setForge();
                } else if ("fabric".equals(s)) {
                    type.setFabric();
                } else if ("neoforge".equals(s)) {
                    type.setNeoForge();
                }
            }
            this.platform = type;
        }
        return platform;
    }
}